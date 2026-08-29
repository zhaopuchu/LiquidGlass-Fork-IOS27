package com.linrjk.liquid.sample.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.drawBackdrop
import com.linrjk.liquid.effects.blur
import com.linrjk.liquid.effects.colorControls
import com.linrjk.liquid.effects.lens
import com.linrjk.liquid.effects.vibrancy
import com.linrjk.liquid.highlight.Highlight
import com.linrjk.liquid.shapes.RoundedRectangle

@Composable
fun LiquidCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    glass: GlassDebugState? = null,
    applyAppearance: Boolean = false,
    maxCornerRadius: Dp = 32f.dp,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    glass?.cornerRadiusFrac
    glass?.blurRadiusDp
    glass?.refractionHeightFrac
    glass?.refractionAmountFrac
    glass?.chromaticAberration
    glass?.edgeDarkening
    if (applyAppearance) {
        glass?.surfaceAlpha
        glass?.brightness
        glass?.saturation
    }

    val surfaceColor =
        if (applyAppearance && glass != null) {
            val baseColor =
                if (isSystemInDarkTheme()) Color(0xFF121212)
                else Color(0xFFFAFAFA)
            baseColor.copy(alpha = glass.surfaceAlpha)
        } else {
            Color.Unspecified
        }

    Column(
        modifier.drawBackdrop(
            backdrop = backdrop,
            shape = {
                if (glass != null) glass.roundedRectangle(maxCornerRadius)
                else RoundedRectangle(maxCornerRadius)
            },
            effects = {
                if (glass != null) {
                    if (applyAppearance) {
                        colorControls(
                            brightness = glass.brightness,
                            saturation = glass.saturation
                        )
                    }
                    glass.applyEffects(this)
                } else {
                    vibrancy()
                    blur(0f)
                    lens(maxCornerRadius.toPx() * 0.2f, maxCornerRadius.toPx() * 0.4f)
                }
            },
            highlight = {
                if (glass != null) glass.highlight()
                else Highlight.IOS27
            },
            onDrawSurface = {
                if (surfaceColor.isSpecified) {
                    drawRect(surfaceColor)
                }
            }
        ),
        content = content
    )
}
