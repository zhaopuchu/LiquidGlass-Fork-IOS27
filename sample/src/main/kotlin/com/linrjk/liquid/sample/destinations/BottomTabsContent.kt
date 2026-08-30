package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.backdrops.LayerBackdrop
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.Block
import com.linrjk.liquid.sample.FlightIcon
import com.linrjk.liquid.sample.components.DefaultHighlightAngle
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.GlassDebugState
import com.linrjk.liquid.sample.components.HighlightPresetDefault
import com.linrjk.liquid.sample.components.LiquidBottomTab
import com.linrjk.liquid.sample.components.LiquidBottomTabs
import com.linrjk.liquid.sample.components.LiquidButton
import com.linrjk.liquid.sample.components.rememberGlassDebugState

private const val BottomTabBarCount = 2
private val BottomTabBarCounts = listOf(3, 4)
private val BottomTabBarTitles = listOf("3 tabs", "4 tabs")

@Composable
fun BottomTabsContent() {
    val isLightTheme = !isSystemInDarkTheme()
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val glasses = rememberBottomTabGlasses()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedGlass = glasses[selectedIndex]

    val airplaneModeIcon = rememberVectorPainter(FlightIcon)
    val iconColorFilter = ColorFilter.tint(contentColor)

    BackdropDemoScaffold { backdrop ->
        GlassDebugOverlay(
            state = selectedGlass,
            backdrop = backdrop,
            showComponentDimensions = true,
            showColorControls = true,
            showPresetPicker = true,
            showHighlightParams = true,
            autoSave = true,
            showReset = false,
            title = BottomTabBarTitles[selectedIndex],
            header = { sheetBackdrop ->
                BottomTabsPanelTabs(
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                    backdrop = sheetBackdrop,
                    isLightTheme = isLightTheme
                )
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(32f.dp)) {
                BottomTabBarDemoItem(
                    tabsCount = BottomTabBarCounts[0],
                    glass = glasses[0],
                    backdrop = backdrop,
                    contentColor = contentColor,
                    iconColorFilter = iconColorFilter,
                    airplaneModeIcon = airplaneModeIcon
                )
                BottomTabBarDemoItem(
                    tabsCount = BottomTabBarCounts[1],
                    glass = glasses[1],
                    backdrop = backdrop,
                    contentColor = contentColor,
                    iconColorFilter = iconColorFilter,
                    airplaneModeIcon = airplaneModeIcon
                )
            }
        }
    }
}

@Composable
private fun rememberBottomTabGlasses(): List<GlassDebugState> {
    return listOf(
        rememberBottomTabGlass(3),
        rememberBottomTabGlass(4)
    )
}

@Composable
private fun rememberBottomTabGlass(tabsCount: Int): GlassDebugState {
    return rememberGlassDebugState(
        pageKey = "BottomTabs_$tabsCount",
        defaultCornerRadiusFrac = 1f,
        defaultComponentWidthDp = 320f,
        defaultComponentHeightDp = 64f,
        defaultBrightness = 0f,
        defaultSaturation = 1f,
        defaultEdgeDarkening = 0f,
        defaultPresetIndex = HighlightPresetDefault,
        defaultHighlightParams = mapOf(DefaultHighlightAngle.key to 60f)
    )
}

@Composable
private fun BottomTabBarDemoItem(
    tabsCount: Int,
    glass: GlassDebugState,
    backdrop: LayerBackdrop,
    contentColor: Color,
    iconColorFilter: ColorFilter,
    airplaneModeIcon: Painter
) {
    val tabsWidth = glass.componentWidthDp.dp
    val tabsHeight = glass.componentHeightDp.dp
    Block {
        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

        LiquidBottomTabs(
            selectedTabIndex = { selectedTabIndex },
            onTabSelected = { selectedTabIndex = it },
            backdrop = backdrop,
            tabsCount = tabsCount,
            modifier = Modifier.size(tabsWidth, tabsHeight),
            glass = glass,
            height = tabsHeight
        ) {
            repeat(tabsCount) { index ->
                LiquidBottomTab({ selectedTabIndex = index }) {
                    Box(
                        Modifier
                            .size(28f.dp)
                            .paint(airplaneModeIcon, colorFilter = iconColorFilter)
                    )
                    BasicText(
                        "Tab ${index + 1}",
                        style = TextStyle(contentColor, 12f.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomTabsPanelTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop,
    isLightTheme: Boolean
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8f.dp)
    ) {
        repeat(BottomTabBarCount) { index ->
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
                    BottomTabBarTitles[index],
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
