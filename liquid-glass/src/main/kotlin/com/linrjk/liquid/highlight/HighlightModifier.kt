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
        if (highlight == null || highlight.width.value <= 0f) {
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

            if (darkEdge != null) {
                configureDarkEdgePaints(darkEdge)
                if (darkEdge.spread.value > 0f && darkEdge.spreadAlpha > 0f) {
                    drawClippedOutline(outline, clipPath, darkEdgeSpreadPaint)
                }
                if (darkEdge.width.value > 0f) {
                    drawClippedOutline(outline, clipPath, darkEdgePaint)
                }
            }

            configureHighlightPaint(highlight, shape)

            highlightLayer.alpha = highlight.alpha
            highlightLayer.blendMode = highlight.style.blendMode
            recordOutline(highlightLayer, safeSize, outline, clipPath, highlightPaint)

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
        shape: Shape
    ) {
        highlightPaint.color = highlight.style.color
        highlightPaint.strokeWidth = strokeWidth(highlight.width.toPx())
        highlightPaint.blur(highlight.blurRadius.toPx())
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

    private fun DrawScope.configureDarkEdgePaints(darkEdge: DarkEdge) {
        val alpha = darkEdge.color.alpha * darkEdge.alpha

        darkEdgeSpreadPaint.color =
            darkEdge.color.copy(alpha = alpha * darkEdge.spreadAlpha)
        darkEdgeSpreadPaint.strokeWidth =
            strokeWidth((darkEdge.width + darkEdge.spread).toPx())
        darkEdgeSpreadPaint.blur(darkEdge.blurRadius.toPx())

        darkEdgePaint.color =
            darkEdge.color.copy(alpha = alpha)
        darkEdgePaint.strokeWidth = strokeWidth(darkEdge.width.toPx())
        darkEdgePaint.blur(0f)
    }

    private fun DrawScope.strokeWidth(width: Float): Float {
        return ceil(width.fastCoerceAtMost(size.minDimension / 2f)) * 2f
    }

    private fun DrawScope.recordOutline(
        layer: GraphicsLayer,
        safeSize: IntSize,
        outline: Outline,
        clipPath: Path?,
        paint: Paint
    ) {
        layer.record(safeSize) {
            translate(1f, 1f) {
                val canvas = drawContext.canvas
                canvas.save()
                canvas.clipOutline(outline, clipPath)
                canvas.drawOutline(outline, paint)
                canvas.restore()
            }
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
