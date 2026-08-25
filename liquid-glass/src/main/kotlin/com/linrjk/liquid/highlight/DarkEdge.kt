package com.linrjk.liquid.highlight

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class DarkEdge(
    val width: Dp = 0.5f.dp,
    val color: Color = Color.Black,
    @param:FloatRange(from = 0.0, to = 1.0) val alpha: Float = 0.18f,
    val spread: Dp = 2f.dp,
    val blurRadius: Dp = 1.25f.dp,
    @param:FloatRange(from = 0.0, to = 1.0) val spreadAlpha: Float = 0.45f
) {

    companion object {

        @Stable
        val Default: DarkEdge = DarkEdge()
    }
}
