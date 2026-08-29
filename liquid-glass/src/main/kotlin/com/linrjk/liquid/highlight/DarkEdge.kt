package com.linrjk.liquid.highlight

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.linrjk.liquid.RuntimeShader
import com.linrjk.liquid.RuntimeShaderCache
import com.linrjk.liquid.internal.DarkEdgeShaderString
import com.linrjk.liquid.isRuntimeShaderSupported
import kotlin.math.PI

@Immutable
data class DarkEdge(
    val width: Dp = 0.5f.dp,
    val color: Color = Color.Black,
    @param:FloatRange(from = 0.0, to = 1.0) val alpha: Float = 0.18f,
    val spread: Dp = 2f.dp,
    val blurRadius: Dp = 1.25f.dp,
    @param:FloatRange(from = 0.0, to = 1.0) val spreadAlpha: Float = 0.45f,
    val angle: Float = 90f,
    @param:FloatRange(from = 0.0, to = 1.0) val directionality: Float = 0.35f,
    @param:FloatRange(from = 0.0) val falloff: Float = 4f
) {

    companion object {

        @Stable
        val Default: DarkEdge = DarkEdge()
    }
}

private const val PeakDarknessScale = 0.7f
private const val LowLevelSpreadScale = 2.5f

internal val DarkEdge.resolvedAlpha: Float
    get() = alpha.fastCoerceIn(0f, 1f) * PeakDarknessScale

internal val DarkEdge.resolvedFalloff: Float
    get() = lerp(falloff * LowLevelSpreadScale, falloff, alpha.fastCoerceIn(0f, 1f))

internal fun DrawScope.createDarkEdgeShader(
    darkEdge: DarkEdge,
    shape: Shape,
    runtimeShaderCache: RuntimeShaderCache
): RuntimeShader? {
    return if (isRuntimeShaderSupported()) {
        runtimeShaderCache.obtainRuntimeShader(
            "DarkEdge",
            DarkEdgeShaderString
        ).apply {
            setFloatUniform("size", size.width, size.height)
            setFloatUniform("cornerRadii", getCornerRadii(shape))
            setColorUniform("color", darkEdge.color.copy(alpha = 1f))
            setFloatUniform("angle", darkEdge.angle * (PI / 180f).toFloat())
            setFloatUniform("directionality", darkEdge.directionality)
            setFloatUniform("falloff", darkEdge.resolvedFalloff)
        }
    } else {
        null
    }
}
