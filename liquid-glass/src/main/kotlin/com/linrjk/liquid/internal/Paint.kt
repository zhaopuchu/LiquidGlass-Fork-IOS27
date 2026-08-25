package com.linrjk.liquid.internal

import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.Paint
import com.linrjk.liquid.RuntimeShader
import com.linrjk.liquid.asAndroidRuntimeShader

internal fun Paint.blur(radius: Float) {
    this.asFrameworkPaint().maskFilter =
        if (radius > 0f) BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        else null
}

internal fun Paint.setRuntimeShader(runtimeShader: RuntimeShader?) {
    this.asFrameworkPaint().shader = runtimeShader?.asAndroidRuntimeShader()
}
