package com.linrjk.liquid.highlight

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class HighlightTest {

    @Test
    fun resolvedStrokeWidthUsesHighlightWidthWhenDarkEdgeIsAbsent() {
        val highlight = Highlight(width = 1.25f.dp)

        assertEquals(1.25f.dp, highlight.resolvedStrokeWidth())
    }

    @Test
    fun resolvedStrokeWidthUsesDarkEdgeWidthWhenAssigned() {
        val highlight =
            Highlight(
                width = 1.25f.dp,
                darkEdge = DarkEdge(width = 2f.dp)
            )

        assertEquals(2f.dp, highlight.resolvedStrokeWidth())
    }

    @Test
    fun resolvedBlurRadiusKeepsHighlightBlurWhenDarkEdgeIsAbsent() {
        val highlight = Highlight(width = 1.25f.dp, blurRadius = 0.6f.dp)

        assertEquals(0.6f.dp, highlight.resolvedBlurRadius())
    }

    @Test
    fun resolvedBlurRadiusClearsHighlightBlurWhenDarkEdgeIsAssigned() {
        val highlight =
            Highlight(
                width = 1.25f.dp,
                blurRadius = 0.6f.dp,
                darkEdge = DarkEdge(width = 2f.dp)
            )

        assertEquals(0f.dp, highlight.resolvedBlurRadius())
    }
}
