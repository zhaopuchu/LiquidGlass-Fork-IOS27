package com.linrjk.liquid.effects

import androidx.compose.ui.graphics.RenderEffect
import com.linrjk.liquid.BackdropEffectScope
import com.linrjk.liquid.RuntimeShader
import com.linrjk.liquid.internal.RuntimeShaderEffect
import com.linrjk.liquid.internal.chain
import com.linrjk.liquid.isRenderEffectSupported
import com.linrjk.liquid.isRuntimeShaderSupported
import org.intellij.lang.annotations.Language
import kotlin.contracts.ExperimentalContracts

fun BackdropEffectScope.effect(effect: RenderEffect) {
    if (!isRenderEffectSupported()) return

    renderEffect = renderEffect.chain(effect)
}

@OptIn(ExperimentalContracts::class)
fun BackdropEffectScope.runtimeShaderEffect(
    key: String,
    @Language("AGSL") shaderString: String,
    uniformShaderName: String,
    block: RuntimeShader.() -> Unit
) {
    if (!isRuntimeShaderSupported()) return

    val effect =
        RuntimeShaderEffect(
            runtimeShader = obtainRuntimeShader(key, shaderString).apply(block),
            uniformShaderName = uniformShaderName
        )
    renderEffect = renderEffect.chain(effect)
}
