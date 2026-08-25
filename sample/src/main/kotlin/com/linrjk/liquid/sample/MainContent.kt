package com.linrjk.liquid.sample

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.linrjk.liquid.sample.destinations.AdaptiveLuminanceGlassContent
import com.linrjk.liquid.sample.destinations.BottomTabsContent
import com.linrjk.liquid.sample.destinations.ButtonsContent
import com.linrjk.liquid.sample.destinations.CircleButtonContent
import com.linrjk.liquid.sample.destinations.ControlCenterContent
import com.linrjk.liquid.sample.destinations.DialogContent
import com.linrjk.liquid.sample.destinations.GlassPlaygroundContent
import com.linrjk.liquid.sample.destinations.HomeContent
import com.linrjk.liquid.sample.destinations.LazyScrollContainerContent
import com.linrjk.liquid.sample.destinations.LockScreenContent
import com.linrjk.liquid.sample.destinations.MagnifierContent
import com.linrjk.liquid.sample.destinations.ProgressiveBlurContent
import com.linrjk.liquid.sample.destinations.ScrollContainerContent
import com.linrjk.liquid.sample.destinations.SliderContent
import com.linrjk.liquid.sample.destinations.ToggleContent
import com.linrjk.liquid.sample.utils.BackHandler

@Composable
fun MainContent() {
    val isLightTheme = !isSystemInDarkTheme()

    CompositionLocalProvider(
        LocalIndication provides ripple(color = if (isLightTheme) Color.Black else Color.White)
    ) {
        var destination by rememberSaveable { mutableStateOf(CatalogDestination.Home) }

        when (destination) {
            CatalogDestination.Home -> HomeContent(onNavigate = { destination = it })

            CatalogDestination.CircleButton -> CircleButtonContent()

            CatalogDestination.Buttons -> ButtonsContent()
            CatalogDestination.Toggle -> ToggleContent()
            CatalogDestination.Slider -> SliderContent()
            CatalogDestination.BottomTabs -> BottomTabsContent()
            CatalogDestination.Dialog -> DialogContent()

            CatalogDestination.LockScreen -> LockScreenContent()
            CatalogDestination.ControlCenter -> ControlCenterContent()
            CatalogDestination.Magnifier -> MagnifierContent()

            CatalogDestination.GlassPlayground -> GlassPlaygroundContent()
            CatalogDestination.AdaptiveLuminanceGlass -> AdaptiveLuminanceGlassContent()
            CatalogDestination.ProgressiveBlur -> ProgressiveBlurContent()
            CatalogDestination.ScrollContainer -> ScrollContainerContent()
            CatalogDestination.LazyScrollContainer -> LazyScrollContainerContent()
        }

        BackHandler(destination != CatalogDestination.Home) {
            destination = CatalogDestination.Home
        }
    }
}
