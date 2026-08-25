package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.LiquidButton
import com.linrjk.liquid.sample.components.rememberGlassDebugState

@Composable
fun ButtonsContent() {
    BackdropDemoScaffold { backdrop ->
        val glass = rememberGlassDebugState()
        GlassDebugOverlay(glass, backdrop) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16f.dp)
        ) {
            LiquidButton(
                {},
                backdrop,
                glass = glass
            ) {
                BasicText(
                    "Transparent Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                surfaceColor = Color.White.copy(0.3f),
                glass = glass
            ) {
                BasicText(
                    "Surface Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                tint = Color(0xFF0088FF),
                glass = glass
            ) {
                BasicText(
                    "Tinted Liquid Button",
                    style = TextStyle(Color.White, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                tint = Color(0xFFFF8D28),
                glass = glass
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
