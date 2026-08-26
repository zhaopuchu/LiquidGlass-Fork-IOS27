package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.LiquidTopAppBar
import com.linrjk.liquid.sample.components.rememberGlassDebugState

@Composable
fun LiquidTopAppBarContent(onBackClick: () -> Unit) {
    BackdropDemoScaffold { backdrop ->
        val glass =
            rememberGlassDebugState(
                pageKey = "LiquidTopAppBar",
                fixedCornerRadiusFrac = 1f,
                defaultComponentSizeDp = 48f,
                defaultIconSizeDp = 24f,
                defaultTitleSizeSp = 20f
            )

        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            showCornerRadius = false,
            showComponentSize = true,
            showIconSize = true,
            showTitleSize = true,
            autoSave = true,
            showReset = false
        ) {
            LiquidTopAppBar(
                backdrop = backdrop,
                onBackClick = onBackClick,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 16f.dp),
                glass = glass,
                titleSize = glass.titleSizeSp.sp,
                buttonSize = glass.componentSizeDp.dp,
                iconSize = glass.iconSizeDp.dp
            )
        }
    }
}
