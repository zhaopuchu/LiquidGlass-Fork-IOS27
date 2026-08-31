package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.backdrops.LayerBackdrop
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.GlassDebugState
import com.linrjk.liquid.sample.components.LiquidButton
import com.linrjk.liquid.sample.components.LiquidCard
import com.linrjk.liquid.sample.components.rememberGlassDebugState

private const val LiquidCardCount = 2

@Composable
fun LiquidCardContent() {
    val isLightTheme = !isSystemInDarkTheme()
    val labelColor = if (isLightTheme) Color.Black else Color.White
    val glasses = rememberLiquidCardGlasses(isLightTheme)
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedGlass = glasses[selectedIndex]

    BackdropDemoScaffold { backdrop ->
        GlassDebugOverlay(
            state = selectedGlass,
            backdrop = backdrop,
            showComponentDimensions = true,
            showColorControls = true,
            showSurfaceAlpha = true,
            showPresetPicker = true,
            showHighlightParams = true,
            autoSave = true,
            showReset = false,
            title = "Card ${selectedIndex + 1}",
            header = { sheetBackdrop ->
                LiquidCardPanelTabs(
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                    backdrop = sheetBackdrop,
                    isLightTheme = isLightTheme
                )
            }
        ) {
            Row(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 24f.dp),
                horizontalArrangement = Arrangement.spacedBy(16f.dp),
                verticalAlignment = Alignment.Top
            ) {
                LiquidCardDemoItem(
                    index = 0,
                    selectedIndex = selectedIndex,
                    glass = glasses[0],
                    backdrop = backdrop,
                    labelColor = labelColor
                ) {
                    selectedIndex = 0
                }
                LiquidCardDemoItem(
                    index = 1,
                    selectedIndex = selectedIndex,
                    glass = glasses[1],
                    backdrop = backdrop,
                    labelColor = labelColor
                ) {
                    selectedIndex = 1
                }
            }
        }
    }
}

@Composable
private fun rememberLiquidCardGlasses(isLightTheme: Boolean): List<GlassDebugState> {
    return listOf(
        rememberLiquidCardGlass(1, isLightTheme),
        rememberLiquidCardGlass(2, isLightTheme)
    )
}

@Composable
private fun rememberLiquidCardGlass(index: Int, isLightTheme: Boolean): GlassDebugState {
    return rememberGlassDebugState(
        pageKey = "LiquidCard_$index",
        defaultComponentWidthDp = 148f,
        defaultComponentHeightDp = 180f,
        defaultSurfaceAlpha = 0f,
        defaultBrightness = if (isLightTheme) 0.2f else 0f,
        defaultSaturation = 1.5f
    )
}

@Composable
private fun LiquidCardDemoItem(
    index: Int,
    selectedIndex: Int,
    glass: GlassDebugState,
    backdrop: LayerBackdrop,
    labelColor: Color,
    onClick: () -> Unit
) {
    val selected = index == selectedIndex
    val cardWidth = glass.componentWidthDp.dp
    val cardHeight = glass.componentHeightDp.dp
    val maxCornerRadius = minOf(cardWidth, cardHeight) / 2f
    val cornerRadius = maxCornerRadius * glass.cornerRadiusFrac

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8f.dp)
    ) {
        Box(
            Modifier
                .size(cardWidth + 10f.dp, cardHeight + 10f.dp)
                .border(
                    width = 2f.dp,
                    color = if (selected) Color(0xFF0088FF) else Color.Transparent,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            LiquidCard(
                backdrop = backdrop,
                modifier = Modifier.size(cardWidth, cardHeight),
                glass = glass,
                applyAppearance = true,
                maxCornerRadius = maxCornerRadius
            )
        }
        BasicText(
            "${index + 1}",
            style =
                TextStyle(
                    color = labelColor.copy(alpha = if (selected) 1f else 0.45f),
                    fontSize = 13f.sp
                )
        )
    }
}

@Composable
private fun LiquidCardPanelTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop,
    isLightTheme: Boolean
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8f.dp)
    ) {
        repeat(LiquidCardCount) { index ->
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
