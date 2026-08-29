package com.linrjk.liquid

import android.os.Build
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linrjk.liquid.effects.blur
import com.linrjk.liquid.effects.effect
import com.linrjk.liquid.effects.lens
import com.linrjk.liquid.effects.runtimeShaderEffect
import com.linrjk.liquid.internal.DarkEdgeShaderString
import com.linrjk.liquid.internal.DefaultHighlightShaderString
import com.linrjk.liquid.shapes.Rectangle
import com.linrjk.liquid.shapes.RoundedRectangle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlatformBehaviorInstrumentedTest {

    private class TestScope(
        override val shape: Shape = Rectangle
    ) : BackdropEffectScopeImpl() {
    }

    @Test
    fun platformCapabilityGuardsMatchAndroidSdk() {
        assertEquals(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S, isRenderEffectSupported())
        assertEquals(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU, isRuntimeShaderSupported())
    }

    @Test
    fun blurSafelyDoesNothingBelowAndroid12() {
        assumeTrue(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        val scope = TestScope()

        scope.blur(16f)

        assertEquals(0f, scope.padding)
        assertNull(scope.renderEffect)
    }

    @Test
    fun renderEffectsAreChainedOnAndroid12AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        val scope = TestScope()

        scope.effect(BlurEffect(4f, 4f))
        val first = scope.renderEffect
        scope.effect(BlurEffect(8f, 8f))

        assertNotNull(first)
        assertNotNull(scope.renderEffect)
        assertNotSame(first, scope.renderEffect)
    }

    @Test
    fun runtimeShaderCacheReusesKeyAndCanBeClearedOnAndroid13AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        val cache = RuntimeShaderCacheImpl()
        val shader = "half4 main(float2 coord) { return half4(1.0); }"

        val first = cache.obtainRuntimeShader("solid", shader)
        val second = cache.obtainRuntimeShader("solid", shader)
        assertSame(first, second)

        cache.clear()
        val afterClear = cache.obtainRuntimeShader("solid", shader)
        assertNotSame(first, afterClear)
    }

    @Test
    fun runtimeShaderEffectIsAddedOnAndroid13AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        val scope = TestScope()
        val shader =
            "uniform shader content; half4 main(float2 coord) { return content.eval(coord); }"

        scope.runtimeShaderEffect("passthrough", shader, "content") {}

        assertNotNull(scope.renderEffect)
    }

    @Test
    fun highlightShaderCompilesOnAndroid13AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        val radii = floatArrayOf(24f, 24f, 24f, 24f)

        val highlightShader = RuntimeShader(DefaultHighlightShaderString).apply {
            setFloatUniform("size", 256f, 128f)
            setFloatUniform("cornerRadii", radii)
            setColorUniform("color", Color.White)
            setFloatUniform("angle", 0f)
            setFloatUniform("falloff", 4f)
            setFloatUniform("ambient", 0f)
            setFloatUniform("edgeBlend", 1f)
        }

        assertNotNull(highlightShader)
    }

    @Test
    fun darkEdgeShaderCompilesOnAndroid13AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        val radii = floatArrayOf(24f, 24f, 24f, 24f)

        val darkEdgeShader = RuntimeShader(DarkEdgeShaderString).apply {
            setFloatUniform("size", 256f, 128f)
            setFloatUniform("cornerRadii", radii)
            setColorUniform("color", Color.Black)
            setFloatUniform("angle", 0f)
            setFloatUniform("directionality", 0.35f)
            setFloatUniform("falloff", 4f)
        }

        assertNotNull(darkEdgeShader)
    }

    @Test
    fun lensCompilesForPlainAndChromaticShadersOnAndroid13AndLater() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)

        val plainScope = TestScope(RoundedRectangle(24f.dp))
        plainScope.size = Size(256f, 128f)
        plainScope.lens(
            refractionHeight = 16f,
            refractionAmount = 32f
        )
        assertNotNull(plainScope.renderEffect)

        val chromaticScope = TestScope(RoundedRectangle(24f.dp))
        chromaticScope.size = Size(256f, 128f)
        chromaticScope.lens(
            refractionHeight = 16f,
            refractionAmount = 32f,
            chromaticAberration = true
        )
        assertNotNull(chromaticScope.renderEffect)
    }
}
