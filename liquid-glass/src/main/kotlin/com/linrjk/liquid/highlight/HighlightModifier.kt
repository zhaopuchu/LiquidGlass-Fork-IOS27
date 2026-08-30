package com.linrjk.liquid.highlight

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastCoerceAtMost
import com.linrjk.liquid.RuntimeShaderCacheImpl
import com.linrjk.liquid.internal.ShapeProvider
import com.linrjk.liquid.internal.blur
import com.linrjk.liquid.internal.clipOutline
import com.linrjk.liquid.internal.insetOutline
import com.linrjk.liquid.internal.setRuntimeShader
import com.linrjk.liquid.isRuntimeShaderSupported
import kotlin.math.ceil

internal class HighlightElement(
    val shapeProvider: ShapeProvider,
    val highlight: () -> Highlight?
) : ModifierNodeElement<HighlightNode>() {

    override fun create(): HighlightNode {
        return HighlightNode(shapeProvider, highlight)
    }

    override fun update(node: HighlightNode) {
        node.shapeProvider = shapeProvider
        node.highlight = highlight
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "highlight"
        properties["shapeProvider"] = shapeProvider
        properties["highlight"] = highlight
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HighlightElement) return false

        if (shapeProvider != other.shapeProvider) return false
        if (highlight != other.highlight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shapeProvider.hashCode()
        result = 31 * result + highlight.hashCode()
        return result
    }
}

internal class HighlightNode(
    var shapeProvider: ShapeProvider,
    var highlight: () -> Highlight?
) : DrawModifierNode, Modifier.Node() {

    override val shouldAutoInvalidate: Boolean = false

    private var highlightLayer: GraphicsLayer? = null

    private val highlightPaint =
        Paint().apply {
            style = PaintingStyle.Stroke
        }
    private val darkEdgePaint =
        Paint().apply {
            style = PaintingStyle.Stroke
        }
    private val darkEdgeSpreadPaint =
        Paint().apply {
            style = PaintingStyle.Stroke
        }
    private var clipPath: Path? = null

    private val runtimeShaderCache = RuntimeShaderCacheImpl()

    private var prevStyle: HighlightStyle? = null

    override fun ContentDrawScope.draw() {
        val highlight = highlight()
        if (highlight == null || (highlight.darkEdge == null && highlight.width.value <= 0f)) {
            return drawContent()
        }

        drawContent()

        val highlightLayer = highlightLayer
        if (highlightLayer != null) {
            val size = size
            val density: Density = this
            val layoutDirection = layoutDirection
            val shape = shapeProvider.innerShape
            val darkEdge =
                highlight.darkEdge?.takeIf {
                    (it.width.value > 0f || it.spread.value > 0f) && it.alpha > 0f
                }

            val safeSize =
                IntSize(
                    ceil(size.width).toInt() + 2,
                    ceil(size.height).toInt() + 2
                )

            val outline = shapeProvider.shape.createOutline(size, layoutDirection, density)
            val clipPath =
                if (outline is Outline.Rounded) {
                    clipPath ?: Path().also { clipPath = it }
                } else {
                    null
                }

            val ios27Style = highlight.style as? HighlightStyle.IOS27
            val uniformStroke = ios27Style != null

            if (darkEdge != null) {
                configureDarkEdgePaints(darkEdge, ios27Style, shape)
                if (!uniformStroke && darkEdge.spread.value > 0f && darkEdge.spreadAlpha > 0f) {
                    drawClippedOutline(outline, clipPath, darkEdgeSpreadPaint)
                }
                if (darkEdge.width.value > 0f) {
                    if (uniformStroke) {
                        drawInsetOutline(outline, clipPath, darkEdgePaint)
                    } else {
                        drawClippedOutline(outline, clipPath, darkEdgePaint)
                    }
                }
            }

            configureHighlightPaint(highlight, shape, uniformStroke)

            // 等宽模式下高光让位到暗边内侧，避免顶/底的白色高光用 Plus 混合
            // 把那圈发丝暗边冲淡成缺口。
            val highlightInset =
                if (uniformStroke && darkEdge != null && darkEdge.width.value > 0f) {
                    darkEdgePaint.strokeWidth
                } else {
                    0f
                }

            highlightLayer.alpha = highlight.alpha
            highlightLayer.blendMode = highlight.style.blendMode
            recordOutline(
                layer = highlightLayer,
                safeSize = safeSize,
                outline = outline,
                clipPath = clipPath,
                paint = highlightPaint,
                uniform = uniformStroke,
                extraInset = highlightInset
            )

            translate(-1f, -1f) {
                drawLayer(highlightLayer)
            }
        }
    }

    override fun onAttach() {
        val graphicsContext = requireGraphicsContext()
        highlightLayer = graphicsContext.createGraphicsLayer()
    }

    override fun onDetach() {
        val graphicsContext = requireGraphicsContext()
        highlightLayer?.let { layer ->
            graphicsContext.releaseGraphicsLayer(layer)
            highlightLayer = null
        }
        clipPath = null
        runtimeShaderCache.clear()
        prevStyle = null
    }

    private fun DrawScope.configureHighlightPaint(
        highlight: Highlight,
        shape: Shape,
        uniformStroke: Boolean
    ) {
        highlightPaint.color = highlight.style.color
        highlightPaint.strokeWidth =
            strokeWidth(highlight.resolvedStrokeWidth().toPx(), uniformStroke)
        highlightPaint.blur(if (uniformStroke) 0f else highlight.resolvedBlurRadius().toPx())
        if (isRuntimeShaderSupported()) {
            val shader =
                with(highlight.style) {
                    createShader(
                        shape = shape,
                        runtimeShaderCache = runtimeShaderCache
                    )
                }
            highlightPaint.setRuntimeShader(shader)
        }
    }

    private fun DrawScope.configureDarkEdgePaints(
        darkEdge: DarkEdge,
        ios27Style: HighlightStyle.IOS27?,
        shape: Shape
    ) {
        val uniformStroke = ios27Style != null
        val alpha = darkEdge.color.alpha * darkEdge.alpha

        darkEdgeSpreadPaint.color =
            darkEdge.color.copy(alpha = alpha * darkEdge.spreadAlpha)
        darkEdgeSpreadPaint.strokeWidth =
            strokeWidth((darkEdge.width + darkEdge.spread).toPx(), uniformStroke)
        darkEdgeSpreadPaint.blur(if (uniformStroke) 0f else darkEdge.blurRadius.toPx())

        darkEdgePaint.color =
            darkEdge.color.copy(alpha = alpha)
        darkEdgePaint.strokeWidth = strokeWidth(darkEdge.width.toPx(), uniformStroke)
        darkEdgePaint.blur(0f)
        // 让暗边在顶/底高光核心处淡出，亮线旁边就不会压着一条灰线。
        // 切换预设时 darkEdgePaint 会复用，非 iOS 27 必须显式清掉着色器。
        darkEdgePaint.setRuntimeShader(
            ios27Style?.let { style ->
                with(style) { createDarkEdgeFadeShader(darkEdge, shape, runtimeShaderCache) }
            }
        )
    }

    private fun DrawScope.strokeWidth(width: Float, uniform: Boolean = false): Float {
        val clamped = width.fastCoerceAtMost(size.minDimension / 2f)
        if (!uniform) return ceil(clamped) * 2f
        // 等宽描边画在内缩轮廓上，无需加倍再裁掉外半圈；保留亚像素宽度让抗锯齿
        // 生成发丝级细线，但不低于 1px，否则高密度屏上细线会淡到几乎看不见。
        return if (clamped <= 0f) 0f else clamped.coerceAtLeast(1f)
    }

    private fun DrawScope.recordOutline(
        layer: GraphicsLayer,
        safeSize: IntSize,
        outline: Outline,
        clipPath: Path?,
        paint: Paint,
        uniform: Boolean,
        extraInset: Float = 0f
    ) {
        layer.record(safeSize) {
            translate(1f, 1f) {
                if (uniform) {
                    drawInsetOutline(outline, clipPath, paint, extraInset)
                } else {
                    drawClippedOutline(outline, clipPath, paint)
                }
            }
        }
    }

    /**
     * 把描边完整画在内缩后的轮廓上，避开没有抗锯齿的路径裁剪。
     * 轮廓无法内缩时退回裁剪方案。
     */
    private fun DrawScope.drawInsetOutline(
        outline: Outline,
        clipPath: Path?,
        paint: Paint,
        extraInset: Float = 0f
    ) {
        val inset = insetOutline(outline, extraInset + paint.strokeWidth / 2f)
        if (inset == null) {
            drawClippedOutline(outline, clipPath, paint)
        } else {
            drawContext.canvas.drawOutline(inset, paint)
        }
    }

    private fun DrawScope.drawClippedOutline(
        outline: Outline,
        clipPath: Path?,
        paint: Paint
    ) {
        val canvas = drawContext.canvas
        canvas.save()
        canvas.clipOutline(outline, clipPath)
        canvas.drawOutline(outline, paint)
        canvas.restore()
    }
}
