package com.linrjk.liquid

import androidx.compose.ui.graphics.Shape
import com.linrjk.liquid.effects.blur
import com.linrjk.liquid.shapes.Rectangle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class BackdropEffectScopeTest {

    private class TestScope : BackdropEffectScopeImpl() {
        override val shape: Shape = Rectangle
    }

    @Test
    fun applyResetsPreviousEffectStateBeforeRunningBlock() {
        val scope = TestScope()
        scope.padding = 42f

        scope.apply {
            assertEquals(0f, padding)
            assertNull(renderEffect)
            padding = 7f
        }

        assertEquals(7f, scope.padding)
    }

    @Test
    fun resetReturnsScopeToInitialState() {
        val scope = TestScope()
        scope.padding = 42f
        scope.density = 3f

        scope.reset()

        assertEquals(0f, scope.padding)
        assertEquals(1f, scope.density)
        assertNull(scope.renderEffect)
    }

    @Test
    fun renderEffectCapabilityAndBlurAreNoOpOnLocalJvmStub() {
        val scope = TestScope()

        assertFalse(isRenderEffectSupported())
        assertFalse(isRuntimeShaderSupported())
        scope.blur(16f)

        assertEquals(0f, scope.padding)
        assertNull(scope.renderEffect)
    }
}
