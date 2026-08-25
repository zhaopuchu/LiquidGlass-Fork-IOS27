package com.linrjk.liquid.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ShapesTest {

    private val density = Density(1f)
    private val size = Size(100f, 80f)

    @Test
    fun roundedRectangleClampsRadiusToHalfMinDimension() {
        val corners = RoundedRectangle(100f.dp).corners(size, LayoutDirection.Ltr, density)

        assertEquals(40f, corners.topLeft)
        assertEquals(40f, corners.topRight)
        assertEquals(40f, corners.bottomRight)
        assertEquals(40f, corners.bottomLeft)
    }

    @Test
    fun unevenRoundedRectangleMapsStartAndEndForRtl() {
        val shape = UnevenRoundedRectangle(
            topStart = 10f.dp,
            topEnd = 20f.dp,
            bottomEnd = 30f.dp,
            bottomStart = 40f.dp
        )

        val ltr = shape.corners(size, LayoutDirection.Ltr, density)
        val rtl = shape.corners(size, LayoutDirection.Rtl, density)

        assertEquals(10f, ltr.topLeft)
        assertEquals(20f, ltr.topRight)
        assertEquals(30f, ltr.bottomRight)
        assertEquals(40f, ltr.bottomLeft)
        assertEquals(20f, rtl.topLeft)
        assertEquals(10f, rtl.topRight)
        assertEquals(40f, rtl.bottomRight)
        assertEquals(30f, rtl.bottomLeft)
    }

    @Test
    fun continuousRoundedRectangleCreatesGenericPathOutline() {
        val outline = RoundedRectangle(
            cornerRadius = 20f.dp,
            style = RoundedCornerStyle.Continuous
        ).createOutline(size, LayoutDirection.Ltr, density)

        assertTrue(outline is Outline.Generic)
    }

    @Test
    fun lerpKeepsEndpointsAndInterpolatesCorners() {
        val start = RoundedRectangle(0f.dp)
        val stop = RoundedRectangle(40f.dp)

        assertSame(start, lerp(start, stop, 0f))
        assertSame(stop, lerp(start, stop, 1f))

        val midpoint = lerp(start, stop, 0.5f)
        val corners = midpoint.corners(size, LayoutDirection.Ltr, density)
        assertEquals(20f, corners.topLeft)
        assertEquals(20f, corners.topRight)
        assertEquals(20f, corners.bottomRight)
        assertEquals(20f, corners.bottomLeft)
    }

    @Test
    fun copyKeepsValuesAndAllowsStyleReplacement() {
        val original = RoundedRectangle(18f.dp, RoundedCornerStyle.Continuous)
        val copy = original.copy()
        val rounded = original.copy(style = RoundedCornerStyle.Circular)

        assertEquals(original, copy)
        assertEquals(18f.dp, rounded.cornerRadius)
        assertEquals(RoundedCornerStyle.Circular, rounded.style)
    }
}
