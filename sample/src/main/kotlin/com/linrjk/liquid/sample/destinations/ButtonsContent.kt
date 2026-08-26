package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.LiquidButton
import com.linrjk.liquid.sample.components.rememberGlassDebugState

@Composable
fun ButtonContent() {
    BackdropDemoScaffold { backdrop ->
        val glass = rememberGlassDebugState("Buttons")
        val buttonWidth = glass.componentWidthDp.dp
        val buttonHeight = glass.componentHeightDp.dp
        val buttonModifier = Modifier.size(buttonWidth, buttonHeight)

        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            showComponentDimensions = true,
            autoSave = true,
            showReset = false
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16f.dp)
        ) {
            LiquidButton(
                {},
                backdrop,
                modifier = buttonModifier,
                glass = glass,
                height = buttonHeight
            ) {
                BasicText(
                    "Transparent Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                modifier = buttonModifier,
                surfaceColor = Color.White.copy(0.3f),
                glass = glass,
                height = buttonHeight
            ) {
                BasicText(
                    "Surface Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                modifier = buttonModifier,
                tint = Color(0xFF0088FF),
                glass = glass,
                height = buttonHeight
            ) {
                BasicText(
                    "Tinted Liquid Button",
                    style = TextStyle(Color.White, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                modifier = buttonModifier,
                tint = Color(0xFFFF8D28),
                glass = glass,
                height = buttonHeight
            ) {
                BasicText(
                    "Tinted Liquid Button",
                    style = TextStyle(Color.White, 15f.sp)
                )
            }
        }
        }
    }
}
