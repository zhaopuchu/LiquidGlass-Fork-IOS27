package com.linrjk.liquid.highlight

import androidx.annotation.FloatRange
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastCoerceAtMost
import com.linrjk.liquid.RuntimeShader
import com.linrjk.liquid.RuntimeShaderCache
import com.linrjk.liquid.internal.DefaultHighlightShaderString
import com.linrjk.liquid.internal.IOS27DarkEdgeShaderString
import com.linrjk.liquid.internal.IOS27HighlightShaderString
import com.linrjk.liquid.isRuntimeShaderSupported
import com.linrjk.liquid.shapes.RoundedRectangularShape
import kotlin.math.PI

@Immutable
interface HighlightStyle {

    val color: Color

    val blendMode: BlendMode

    fun DrawScope.createShader(
        shape: Shape,
        runtimeShaderCache: RuntimeShaderCache
    ): RuntimeShader?

    @Immutable
    data class Plain(
        override val color: Color = Color.White.copy(alpha = 0.38f),
        override val blendMode: BlendMode = BlendMode.Plus
    ) : HighlightStyle {

        override fun DrawScope.createShader(
            shape: Shape,
            runtimeShaderCache: RuntimeShaderCache
        ): RuntimeShader? = null
    }

    @Immutable
    data class Default(
        override val color: Color = Color.White.copy(alpha = 0.5f),
        override val blendMode: BlendMode = BlendMode.Plus,
        val angle: Float = 45f,
        @param:FloatRange(from = 0.0) val falloff: Float = 1f,
        @param:FloatRange(from = 0.0, to = 1.0) val ambient: Float = 0f,
        @param:FloatRange(from = 0.0, to = 1.0) val edgeBlend: Float = 0f
    ) : HighlightStyle {

        override fun DrawScope.createShader(
            shape: Shape,
            runtimeShaderCache: RuntimeShaderCache
        ): RuntimeShader? {
            return if (isRuntimeShaderSupported()) {
                runtimeShaderCache.obtainRuntimeShader(
                    "Default",
                    DefaultHighlightShaderString
                ).apply {
                    setFloatUniform("size", size.width, size.height)
                    setFloatUniform("cornerRadii", getCornerRadii(shape))
                    setColorUniform("color", color.copy(alpha = 1f))
                    setFloatUniform("angle", angle * (PI / 180f).toFloat())
                    setFloatUniform("falloff", falloff)
                    setFloatUniform("ambient", ambient)
                    setFloatUniform("edgeBlend", edgeBlend)
                }
            } else {
                null
            }
        }
    }

    @Immutable
    data class IOS27(
        override val color: Color = Color.White,
        override val blendMode: BlendMode = BlendMode.Plus,
        // falloff 只管延伸范围：越大高光越向正上正下收窄。
        @param:FloatRange(from = 0.0) val falloff: Float = 16f,
        // gain 只管核心亮度：大于 1 时正上正下会形成一段饱和的亮带。
        @param:FloatRange(from = 0.0) val gain: Float = 2.5f,
        // 暗边在高光核心处的淡出比例，1 表示核心处完全透明。
        // 取 0.5 让核心处保留一半强度：既能衬出亮线，又不至于把描边断开。
        @param:FloatRange(from = 0.0, to = 1.0) val darkEdgeFade: Float = 0.5f
    ) : HighlightStyle {

        override fun DrawScope.createShader(
            shape: Shape,
            runtimeShaderCache: RuntimeShaderCache
        ): RuntimeShader? {
            return if (isRuntimeShaderSupported()) {
                runtimeShaderCache.obtainRuntimeShader(
                    "IOS27",
                    IOS27HighlightShaderString
                ).apply {
                    setFloatUniform("size", size.width, size.height)
                    setFloatUniform("cornerRadii", getCornerRadii(shape))
                    setColorUniform("color", color.copy(alpha = 1f))
                    setFloatUniform("falloff", falloff)
                    setFloatUniform("gain", gain)
                }
            } else {
                null
            }
        }

        /**
         * 暗边专用着色器：沿周长按 [darkEdgeFade] 在顶/底高光核心处淡出。
         * 必须用独立的缓存键，否则会和高光共用同一个 RuntimeShader 实例而互相覆盖 uniform。
         */
        internal fun DrawScope.createDarkEdgeFadeShader(
            darkEdge: DarkEdge,
            shape: Shape,
            runtimeShaderCache: RuntimeShaderCache
        ): RuntimeShader? {
            return if (isRuntimeShaderSupported()) {
                runtimeShaderCache.obtainRuntimeShader(
                    "IOS27DarkEdge",
                    IOS27DarkEdgeShaderString
                ).apply {
                    setFloatUniform("size", size.width, size.height)
                    setFloatUniform("cornerRadii", getCornerRadii(shape))
                    setColorUniform("color", darkEdge.color.copy(alpha = 1f))
                    setFloatUniform("falloff", falloff)
                    setFloatUniform("gain", gain)
                    setFloatUniform("fade", darkEdgeFade)
                }
            } else {
                null
            }
        }
    }

    companion object {

        @Stable
        val Default: Default = Default()

        @Stable
        val Plain: Plain = Plain()

        @Stable
        val IOS27: IOS27 = IOS27()
    }
}

internal fun DrawScope.getCornerRadii(shape: Shape): FloatArray {
    val size = size
    val maxRadius = size.minDimension / 2f
    return when (shape) {
        is RoundedRectangularShape -> {
            val corners = shape.corners(size, layoutDirection, this)
            floatArrayOf(
                corners.topLeft,
                corners.topRight,
                corners.bottomRight,
                corners.bottomLeft
            )
        }

        is AbsoluteRoundedCornerShape -> {
            floatArrayOf(
                shape.topStart.toPx(size, this),
                shape.topEnd.toPx(size, this),
                shape.bottomEnd.toPx(size, this),
                shape.bottomStart.toPx(size, this)
            ).apply {
                for (index in indices) {
                    this[index] = this[index].fastCoerceAtMost(maxRadius)
                }
            }
        }

        is CornerBasedShape -> {
            val isLtr = layoutDirection == LayoutDirection.Ltr
            val topLeft =
                if (isLtr) shape.topStart.toPx(size, this)
                else shape.topEnd.toPx(size, this)
            val topRight =
                if (isLtr) shape.topEnd.toPx(size, this)
                else shape.topStart.toPx(size, this)
            val bottomRight =
                if (isLtr) shape.bottomEnd.toPx(size, this)
                else shape.bottomStart.toPx(size, this)
            val bottomLeft =
                if (isLtr) shape.bottomStart.toPx(size, this)
                else shape.bottomEnd.toPx(size, this)
            floatArrayOf(
                topLeft.fastCoerceAtMost(maxRadius),
                topRight.fastCoerceAtMost(maxRadius),
                bottomRight.fastCoerceAtMost(maxRadius),
                bottomLeft.fastCoerceAtMost(maxRadius)
            )
        }

        else -> FloatArray(4) { maxRadius }
    }
}
