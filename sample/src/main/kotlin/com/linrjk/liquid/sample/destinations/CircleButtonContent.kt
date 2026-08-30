package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.backdrops.LayerBackdrop
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.CircleLiquidButton
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.GlassDebugState
import com.linrjk.liquid.sample.components.LiquidButton
import com.linrjk.liquid.sample.components.rememberGlassDebugState

private const val CircleButtonCount = 4

@Composable
fun CircleButtonContent() {
    val isLightTheme = !isSystemInDarkTheme()
    val backgroundColor =
        if (isLightTheme) Color(0xFFF2F1F6)
        else Color(0xFF121212)
    val iconTint = if (isLightTheme) Color.Black else Color.White
    val glasses = rememberCircleButtonGlasses(isLightTheme)
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedGlass = glasses[selectedIndex]

    BackdropDemoScaffold(fallbackColor = backgroundColor) { backdrop ->
        GlassDebugOverlay(
            state = selectedGlass,
            backdrop = backdrop,
            showCornerRadius = false,
            showComponentSize = true,
            showIconSize = true,
            showColorControls = true,
            showSurfaceAlpha = true,
            showTintPicker = true,
            showPresetPicker = true,
            showHighlightParams = true,
            autoSave = true,
            showReset = false,
            title = "Button ${selectedIndex + 1}",
            header = { sheetBackdrop ->
                CircleButtonPanelTabs(
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                    backdrop = sheetBackdrop,
                    isLightTheme = isLightTheme
                )
            }
        ) {
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(top = 24f.dp),
                verticalArrangement = Arrangement.spacedBy(20f.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24f.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleButtonDemoItem(
                        index = 0,
                        selectedIndex = selectedIndex,
                        glass = glasses[0],
                        size = glasses[0].componentSizeDp.dp,
                        iconSize = glasses[0].iconSizeDp.dp,
                        tint = glasses[0].tintColor,
                        backdrop = backdrop,
                        iconTint = iconTint
                    ) {
                        selectedIndex = 0
                    }
                    CircleButtonDemoItem(
                        index = 1,
                        selectedIndex = selectedIndex,
                        glass = glasses[1],
                        size = glasses[1].componentSizeDp.dp,
                        iconSize = glasses[1].iconSizeDp.dp,
                        tint = glasses[1].tintColor,
                        backdrop = backdrop,
                        iconTint = iconTint
                    ) {
                        selectedIndex = 1
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24f.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleButtonDemoItem(
                        index = 2,
                        selectedIndex = selectedIndex,
                        glass = glasses[2],
                        size = glasses[2].componentSizeDp.dp,
                        iconSize = glasses[2].iconSizeDp.dp,
                        tint = glasses[2].tintColor,
                        backdrop = backdrop,
                        iconTint = iconTint
                    ) {
                        selectedIndex = 2
                    }
                    CircleButtonDemoItem(
                        index = 3,
                        selectedIndex = selectedIndex,
                        glass = glasses[3],
                        size = glasses[3].componentSizeDp.dp,
                        iconSize = glasses[3].iconSizeDp.dp,
                        tint = glasses[3].tintColor,
                        backdrop = backdrop,
                        iconTint = iconTint
                    ) {
                        selectedIndex = 3
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberCircleButtonGlasses(isLightTheme: Boolean): List<GlassDebugState> {
    return listOf(
        rememberCircleButtonGlass(1, isLightTheme),
        rememberCircleButtonGlass(2, isLightTheme),
        rememberCircleButtonGlass(3, isLightTheme),
        rememberCircleButtonGlass(4, isLightTheme)
    )
}

@Composable
private fun rememberCircleButtonGlass(index: Int, isLightTheme: Boolean): GlassDebugState {
    return rememberGlassDebugState(
        pageKey = "CircleButton_$index",
        fixedCornerRadiusFrac = 1f,
        defaultSurfaceAlpha = 0f,
        defaultBrightness = if (isLightTheme) 0.2f else 0f,
        defaultSaturation = 1.5f
    )
}

@Composable
private fun CircleButtonDemoItem(
    index: Int,
    selectedIndex: Int,
    glass: GlassDebugState,
    size: Dp,
    iconSize: Dp,
    tint: Color,
    backdrop: LayerBackdrop,
    iconTint: Color,
    onClick: () -> Unit
) {
    val selected = index == selectedIndex
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8f.dp)
    ) {
        Box(
            Modifier
                .size(size + 10f.dp)
                .border(
                    width = 2f.dp,
                    color = if (selected) Color(0xFF0088FF) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CircleLiquidButton(
                onClick = onClick,
                backdrop = backdrop,
                size = size,
                iconSize = iconSize,
                iconTint = iconTint,
                tint = tint,
                glass = glass,
                applyAppearance = true,
                contentDescription = "Button ${index + 1}"
            )
        }
        BasicText(
            "${index + 1}",
            style =
                TextStyle(
                    color = iconTint.copy(alpha = if (selected) 1f else 0.45f),
                    fontSize = 13f.sp
                )
        )
    }
}

@Composable
private fun CircleButtonPanelTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop,
    isLightTheme: Boolean
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8f.dp)
    ) {
        repeat(CircleButtonCount) { index ->
            val selected = index == selectedIndex
            LiquidButton(
                onClick = { onSelect(index) },
                backdrop = backdrop,
                modifier = Modifier.weight(1f),
                isInteractive = false,
                tint = if (selected) Color(0xFF0088FF) else Color.Unspecified,
                height = 40f.dp
            ) {
                BasicText(
                    "${index + 1}",
                    style =
                        TextStyle(
                            color =
                                if (selected) Color.White
                                else if (isLightTheme) Color.Black
                                else Color.White,
                            fontSize = 15f.sp
                        )
                )
            }
        }
    }
}
