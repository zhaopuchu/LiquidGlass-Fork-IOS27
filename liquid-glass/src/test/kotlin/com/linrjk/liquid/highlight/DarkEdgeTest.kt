package com.linrjk.liquid.highlight

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DarkEdgeTest {

    @Test
    fun resolvedAlphaLowersPeakDarkness() {
        assertTrue(DarkEdge(alpha = 1f).resolvedAlpha < 1f)
        assertEquals(0f, DarkEdge(alpha = 0f).resolvedAlpha)
    }

    @Test
    fun resolvedFalloffWidensAsAlphaDrops() {
        val lowLevel = DarkEdge(alpha = 0.05f, falloff = 4f).resolvedFalloff
        val highLevel = DarkEdge(alpha = 1f, falloff = 4f).resolvedFalloff

        assertEquals(4f, highLevel)
        assertTrue(lowLevel > highLevel)
    }
}
