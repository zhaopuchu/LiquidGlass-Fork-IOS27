package com.linrjk.liquid.sample.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.BackdropEffectScope
import com.linrjk.liquid.backdrops.rememberLayerBackdrop
import com.linrjk.liquid.drawBackdrop
import com.linrjk.liquid.effects.blur
import com.linrjk.liquid.effects.lens
import com.linrjk.liquid.effects.vibrancy
import com.linrjk.liquid.highlight.DarkEdge
import com.linrjk.liquid.highlight.Highlight
import com.linrjk.liquid.sample.Block
import com.linrjk.liquid.shapes.RoundedRectangle
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class GlassDebugState internal constructor(
    internal val pageKey: String,
    cornerRadiusFrac: Float = 0.5f,
    componentSizeDp: Float = 96f,
    componentWidthDp: Float = 240f,
    componentHeightDp: Float = 48f,
    iconSizeDp: Float = 32f,
    titleSizeSp: Float = 20f,
    blurRadiusDp: Float = 0f,
    refractionHeightFrac: Float = 0.2f,
    refractionAmountFrac: Float = 0.2f,
    chromaticAberration: Float = 0f,
    edgeDarkening: Float = 0.18f,
    surfaceAlpha: Float = 0.6f,
    backgroundDim: Float = 0.23f,
    brightness: Float = 0.2f,
    saturation: Float = 1.5f
) {
    var cornerRadiusFrac by mutableFloatStateOf(cornerRadiusFrac)
    var componentSizeDp by mutableFloatStateOf(componentSizeDp)
    var componentWidthDp by mutableFloatStateOf(componentWidthDp)
    var componentHeightDp by mutableFloatStateOf(componentHeightDp)
    var iconSizeDp by mutableFloatStateOf(iconSizeDp)
    var titleSizeSp by mutableFloatStateOf(titleSizeSp)
    var blurRadiusDp by mutableFloatStateOf(blurRadiusDp)
    var refractionHeightFrac by mutableFloatStateOf(refractionHeightFrac)
    var refractionAmountFrac by mutableFloatStateOf(refractionAmountFrac)
    var chromaticAberration by mutableFloatStateOf(chromaticAberration)
    var edgeDarkening by mutableFloatStateOf(edgeDarkening)
    var surfaceAlpha by mutableFloatStateOf(surfaceAlpha)
    var backgroundDim by mutableFloatStateOf(backgroundDim)
    var brightness by mutableFloatStateOf(brightness)
    var saturation by mutableFloatStateOf(saturation)

    fun reset() {
        applyConfig(GlassDebugConfig())
    }

    internal fun toConfig(): GlassDebugConfig {
        return GlassDebugConfig(
            cornerRadiusFrac = cornerRadiusFrac,
            componentSizeDp = componentSizeDp,
            componentWidthDp = componentWidthDp,
            componentHeightDp = componentHeightDp,
            iconSizeDp = iconSizeDp,
            titleSizeSp = titleSizeSp,
            blurRadiusDp = blurRadiusDp,
            refractionHeightFrac = refractionHeightFrac,
            refractionAmountFrac = refractionAmountFrac,
            chromaticAberration = chromaticAberration,
            edgeDarkening = edgeDarkening,
            surfaceAlpha = surfaceAlpha,
            backgroundDim = backgroundDim,
            brightness = brightness,
            saturation = saturation
        )
    }

    internal fun applyConfig(config: GlassDebugConfig) {
        cornerRadiusFrac = config.cornerRadiusFrac
        componentSizeDp = config.componentSizeDp
        componentWidthDp = config.componentWidthDp
        componentHeightDp = config.componentHeightDp
        iconSizeDp = config.iconSizeDp
        titleSizeSp = config.titleSizeSp
        blurRadiusDp = config.blurRadiusDp
        refractionHeightFrac = config.refractionHeightFrac
        refractionAmountFrac = config.refractionAmountFrac
        chromaticAberration = config.chromaticAberration
        edgeDarkening = config.edgeDarkening
        surfaceAlpha = config.surfaceAlpha
        backgroundDim = config.backgroundDim
        brightness = config.brightness
        saturation = config.saturation
    }

    fun roundedRectangle(maxCornerRadius: Dp): RoundedRectangle {
        return RoundedRectangle(maxCornerRadius * cornerRadiusFrac)
    }

    fun highlight(
        edgeDarkening: Float = this.edgeDarkening,
        spread: Dp = DarkEdge.Default.spread
    ): Highlight {
        return Highlight.IOS27.copy(
            darkEdge = DarkEdge.Default.copy(
                alpha = edgeDarkening,
                spread = spread
            )
        )
    }

    fun applyEffects(scope: BackdropEffectScope, intensity: Float = 1f) {
        with(scope) {
            vibrancy()
            blur(blurRadiusDp.dp.toPx())
            val amount = intensity.coerceIn(0f, 1f)
            val minDimension = size.minDimension
            lens(
                refractionHeight = refractionHeightFrac * minDimension * 0.5f * amount,
                refractionAmount = refractionAmountFrac * minDimension * amount,
                depthEffect = true,
                chromaticAberration = chromaticAberration > 0f
            )
        }
    }
}

@Composable
fun rememberGlassDebugState(
    pageKey: String,
    fixedCornerRadiusFrac: Float? = null,
    defaultComponentSizeDp: Float = 96f,
    defaultComponentWidthDp: Float = 240f,
    defaultComponentHeightDp: Float = 48f,
    defaultIconSizeDp: Float = 32f,
    defaultTitleSizeSp: Float = 20f,
    defaultSurfaceAlpha: Float = 0.6f,
    defaultBackgroundDim: Float = 0.23f,
    defaultBrightness: Float = 0.2f,
    defaultSaturation: Float = 1.5f
): GlassDebugState {
    val applicationContext = LocalContext.current.applicationContext
    return remember(
        pageKey,
        fixedCornerRadiusFrac,
        defaultComponentSizeDp,
        defaultComponentWidthDp,
        defaultComponentHeightDp,
        defaultIconSizeDp,
        defaultTitleSizeSp,
        defaultSurfaceAlpha,
        defaultBackgroundDim,
        defaultBrightness,
        defaultSaturation,
        applicationContext
    ) {
        val defaults =
            GlassDebugConfig(
                componentSizeDp = defaultComponentSizeDp,
                componentWidthDp = defaultComponentWidthDp,
                componentHeightDp = defaultComponentHeightDp,
                iconSizeDp = defaultIconSizeDp,
                titleSizeSp = defaultTitleSizeSp,
                surfaceAlpha = defaultSurfaceAlpha,
                backgroundDim = defaultBackgroundDim,
                brightness = defaultBrightness,
                saturation = defaultSaturation
            )
        GlassDebugState(
            pageKey = pageKey,
            componentSizeDp = defaultComponentSizeDp,
            componentWidthDp = defaultComponentWidthDp,
            componentHeightDp = defaultComponentHeightDp,
            iconSizeDp = defaultIconSizeDp,
            titleSizeSp = defaultTitleSizeSp,
            surfaceAlpha = defaultSurfaceAlpha,
            backgroundDim = defaultBackgroundDim,
            brightness = defaultBrightness,
            saturation = defaultSaturation
        ).apply {
            GlassDebugConfigStore(applicationContext)
                .load(pageKey, defaults)
                ?.let(::applyConfig)
            if (fixedCornerRadiusFrac != null) {
                cornerRadiusFrac = fixedCornerRadiusFrac
            }
        }
    }
}

@Composable
fun BoxScope.GlassDebugOverlay(
    state: GlassDebugState,
    backdrop: Backdrop,
    showCornerRadius: Boolean = true,
    showComponentSize: Boolean = false,
    showComponentDimensions: Boolean = false,
    showAppearanceControls: Boolean = false,
    showColorControls: Boolean = false,
    showSurfaceAlpha: Boolean = false,
    showIconSize: Boolean = false,
    showTitleSize: Boolean = false,
    autoSave: Boolean = false,
    showReset: Boolean = true,
    onReset: () -> Unit = { state.reset() },
    content: @Composable BoxScope.() -> Unit
) {
    var isSheetExpanded by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val configStore =
        remember(context.applicationContext) {
            GlassDebugConfigStore(context.applicationContext)
        }
    val currentConfig = state.toConfig()

    if (autoSave) {
        LaunchedEffect(currentConfig) {
            delay(300)
            configStore.save(state.pageKey, currentConfig)
        }
    }

    if (showComponentSize || showComponentDimensions || showIconSize || showTitleSize) {
        LaunchedEffect(showComponentSize, showComponentDimensions, showIconSize, showTitleSize) {
            if (showComponentSize) {
                state.componentSizeDp = state.componentSizeDp.roundToInt().toFloat()
            }
            if (showComponentDimensions) {
                state.componentWidthDp = state.componentWidthDp.roundToInt().toFloat()
                state.componentHeightDp = state.componentHeightDp.roundToInt().toFloat()
            }
            if (showIconSize) {
                state.iconSizeDp = state.iconSizeDp.roundToInt().toFloat()
            }
            if (showTitleSize) {
                state.titleSizeSp = state.titleSizeSp.roundToInt().toFloat()
            }
        }
    }

    content()

    Block {
        if (isSheetExpanded) {
            val sheetBackdrop = rememberLayerBackdrop()
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16f.dp)
                    .padding(bottom = 72f.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedRectangle(32f.dp) },
                        effects = {
                            vibrancy()
                            blur(4f.dp.toPx())
                            lens(16f.dp.toPx(), 32f.dp.toPx())
                        },
                        highlight = { Highlight.Plain },
                        exportedBackdrop = sheetBackdrop,
                        onDrawSurface = { drawRect(Color.White.copy(alpha = 0.5f)) }
                    )
                    .padding(16f.dp),
                verticalArrangement = Arrangement.spacedBy(12f.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12f.dp)
                ) {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12f.dp)
                    ) {
                        if (showComponentSize) {
                            GlassDebugSlider(
                                "Width / height",
                                { state.componentSizeDp },
                                12f..200f,
                                1f,
                                sheetBackdrop,
                                decimalPlaces = 0
                            ) {
                                state.componentSizeDp = it.roundToInt().toFloat()
                            }
                            if (showIconSize) {
                                GlassDebugSlider(
                                    "Icon size",
                                    { state.iconSizeDp },
                                    0f..200f,
                                    1f,
                                    sheetBackdrop,
                                    decimalPlaces = 0
                                ) {
                                    state.iconSizeDp = it.roundToInt().toFloat()
                                }
                            }
                            if (showTitleSize) {
                                GlassDebugSlider(
                                    "Title size",
                                    { state.titleSizeSp },
                                    8f..48f,
                                    1f,
                                    sheetBackdrop,
                                    decimalPlaces = 0
                                ) {
                                    state.titleSizeSp = it.roundToInt().toFloat()
                                }
                            }
                        }
                        if (showComponentDimensions) {
                            GlassDebugSlider(
                                "Width",
                                { state.componentWidthDp },
                                48f..360f,
                                1f,
                                sheetBackdrop,
                                decimalPlaces = 0
                            ) {
                                state.componentWidthDp = it.roundToInt().toFloat()
                            }
                        }
                        if (showAppearanceControls || showSurfaceAlpha) {
                            GlassDebugSlider(
                                "Surface alpha",
                                { state.surfaceAlpha },
                                0f..1f,
                                0.001f,
                                sheetBackdrop
                            ) {
                                state.surfaceAlpha = it
                            }
                        }
                        if (showAppearanceControls) {
                            GlassDebugSlider(
                                "Background dim",
                                { state.backgroundDim },
                                0f..1f,
                                0.001f,
                                sheetBackdrop
                            ) {
                                state.backgroundDim = it
                            }
                        }
                        if (showCornerRadius) {
                            GlassDebugSlider(
                                "Corner radius",
                                { state.cornerRadiusFrac },
                                0f..1f,
                                0.001f,
                                sheetBackdrop
                            ) {
                                state.cornerRadiusFrac = it
                            }
                        }
                        GlassDebugSlider("Blur radius", { state.blurRadiusDp }, 0f..32f, 0.01f, sheetBackdrop) {
                            state.blurRadiusDp = it
                        }
                        GlassDebugSlider("Refraction height", { state.refractionHeightFrac }, 0f..1f, 0.001f, sheetBackdrop) {
                            state.refractionHeightFrac = it
                        }
                    }
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12f.dp)
                    ) {
                        if (showComponentDimensions) {
                            GlassDebugSlider(
                                "Height",
                                { state.componentHeightDp },
                                32f..400f,
                                1f,
                                sheetBackdrop,
                                decimalPlaces = 0
                            ) {
                                state.componentHeightDp = it.roundToInt().toFloat()
                            }
                        }
                        if (showAppearanceControls || showColorControls) {
                            GlassDebugSlider(
                                "Brightness",
                                { state.brightness },
                                -1f..1f,
                                0.001f,
                                sheetBackdrop
                            ) {
                                state.brightness = it
                            }
                            GlassDebugSlider(
                                "Saturation",
                                { state.saturation },
                                0f..2f,
                                0.001f,
                                sheetBackdrop
                            ) {
                                state.saturation = it
                            }
                        }
                        GlassDebugSlider("Refraction amount", { state.refractionAmountFrac }, 0f..1f, 0.001f, sheetBackdrop) {
                            state.refractionAmountFrac = it
                        }
                        GlassDebugSlider("Chromatic aberration", { state.chromaticAberration }, 0f..1f, 0.001f, sheetBackdrop) {
                            state.chromaticAberration = it
                        }
                        GlassDebugSlider("Dark edge", { state.edgeDarkening }, 0f..1f, 0.001f, sheetBackdrop) {
                            state.edgeDarkening = it
                        }
                    }
                }
                if (autoSave) {
                    BasicText(
                        "Configuration saves automatically",
                        Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(Color.Black.copy(alpha = 0.65f), 14f.sp)
                    )
                } else {
                    LiquidButton(
                        onClick = {
                            configStore.save(state.pageKey, state.toConfig())
                            Toast
                                .makeText(context, "Configuration saved", Toast.LENGTH_SHORT)
                                .show()
                        },
                        backdrop = sheetBackdrop,
                        modifier = Modifier.fillMaxWidth(),
                        isInteractive = false,
                        tint = Color(0xFF0088FF)
                    ) {
                        BasicText(
                            "Save configuration",
                            style = TextStyle(Color.White, 15f.sp)
                        )
                    }
                }
            }
        }
    }

    Block {
        LiquidButton(
            { isSheetExpanded = !isSheetExpanded },
            backdrop,
            Modifier
                .padding(20f.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomStart),
            tint = Color(0xFFFF8D28)
        ) {
            BasicText(
                if (isSheetExpanded) "🔽" else "🔼",
                style = TextStyle(Color.White, 15f.sp)
            )
        }

        if (showReset) {
            LiquidButton(
                onReset,
                backdrop,
                Modifier
                    .padding(20f.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomEnd),
                tint = Color(0xFFFF8D28)
            ) {
                BasicText("Reset", style = TextStyle(Color.White, 15f.sp))
            }
        }
    }
}

@Composable
private fun GlassDebugSlider(
    label: String,
    value: () -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    visibilityThreshold: Float,
    backdrop: Backdrop,
    decimalPlaces: Int = 2,
    onValueChange: (Float) -> Unit
) {
    val current = value()
    Column(verticalArrangement = Arrangement.spacedBy(8f.dp)) {
        val formattedValue =
            if (decimalPlaces == 0) current.roundToInt().toString()
            else "%.${decimalPlaces}f".format(current)
        BasicText("$label  $formattedValue")
        LiquidSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            visibilityThreshold = visibilityThreshold,
            backdrop = backdrop
        )
    }
}
