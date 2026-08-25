package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.CircleLiquidButton
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.rememberGlassDebugState

@Composable
fun CircleButtonContent() {
    BackdropDemoScaffold { backdrop ->
        val glass =
            rememberGlassDebugState(
                pageKey = "CircleButton",
                fixedCornerRadiusFrac = 1f
            )
        val size = glass.componentSizeDp.dp

        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            showCornerRadius = false,
            showComponentSize = true,
            showIconSize = true,
            autoSave = true,
            showReset = false
        ) {
            CircleLiquidButton(
                onClick = {},
                backdrop = backdrop,
                size = size,
                iconSize = glass.iconSizeDp.dp,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 160f.dp),
                glass = glass
            )
        }
    }
}
