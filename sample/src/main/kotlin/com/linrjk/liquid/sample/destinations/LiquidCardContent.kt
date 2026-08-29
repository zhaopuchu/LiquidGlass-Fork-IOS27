package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.LiquidCard
import com.linrjk.liquid.sample.components.rememberGlassDebugState

@Composable
fun LiquidCardContent() {
    val isLightTheme = !isSystemInDarkTheme()
    BackdropDemoScaffold { backdrop ->
        val glass =
            rememberGlassDebugState(
                pageKey = "LiquidCard",
                defaultComponentWidthDp = 256f,
                defaultComponentHeightDp = 256f,
                defaultSurfaceAlpha = 0f,
                defaultBrightness = if (isLightTheme) 0.2f else 0f,
                defaultSaturation = 1.5f
            )
        val cardWidth = glass.componentWidthDp.dp
        val cardHeight = glass.componentHeightDp.dp
        val maxCornerRadius = minOf(cardWidth, cardHeight) / 2f

        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            showComponentDimensions = true,
            showColorControls = true,
            showSurfaceAlpha = true,
            autoSave = true,
            showReset = false
        ) {
            LiquidCard(
                backdrop = backdrop,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 160f.dp)
                        .size(cardWidth, cardHeight),
                glass = glass,
                applyAppearance = true,
                maxCornerRadius = maxCornerRadius
            )
        }
    }
}
