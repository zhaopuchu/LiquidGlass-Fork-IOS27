package com.linrjk.liquid.highlight

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Highlight(
    val width: Dp = 0.5f.dp,
    val blurRadius: Dp = width / 2f,
    @param:FloatRange(from = 0.0, to = 1.0) val alpha: Float = 1f,
    val style: HighlightStyle = HighlightStyle.Default,
    val darkEdge: DarkEdge? = null
) {

    companion object {

        @Stable
        val Default: Highlight = Highlight()

        @Stable
        val Plain: Highlight = Highlight(style = HighlightStyle.Plain)

        @Stable
        val IOS27: Highlight =
            Highlight(
                width = Ios27StrokeWidth,
                style = HighlightStyle.IOS27,
                darkEdge = Ios27DarkEdge
            )
    }
}

internal val Ios27StrokeWidth = 0.5f.dp

internal val Ios27DarkEdge: DarkEdge =
    DarkEdge(
        width = Ios27StrokeWidth,
        spread = 0f.dp,
        blurRadius = 0f.dp,
        directionality = 0f
    )

internal fun Highlight.resolvedStrokeWidth(): Dp {
    return darkEdge?.width ?: width
}

internal fun Highlight.resolvedBlurRadius(): Dp {
    return if (darkEdge != null) 0f.dp else blurRadius
}
