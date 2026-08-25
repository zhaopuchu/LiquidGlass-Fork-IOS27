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
    iconSizeDp: Float = 32f,
    blurRadiusDp: Float = 0f,
    refractionHeightFrac: Float = 0.2f,
    refractionAmountFrac: Float = 0.2f,
    chromaticAberration: Float = 0f,
    edgeDarkening: Float = 0.18f
) {
    var cornerRadiusFrac by mutableFloatStateOf(cornerRadiusFrac)
    var componentSizeDp by mutableFloatStateOf(componentSizeDp)
    var iconSizeDp by mutableFloatStateOf(iconSizeDp)
    var blurRadiusDp by mutableFloatStateOf(blurRadiusDp)
    var refractionHeightFrac by mutableFloatStateOf(refractionHeightFrac)
    var refractionAmountFrac by mutableFloatStateOf(refractionAmountFrac)
    var chromaticAberration by mutableFloatStateOf(chromaticAberration)
    var edgeDarkening by mutableFloatStateOf(edgeDarkening)

    fun reset() {
        applyConfig(GlassDebugConfig())
    }

    internal fun toConfig(): GlassDebugConfig {
        return GlassDebugConfig(
            cornerRadiusFrac = cornerRadiusFrac,
            componentSizeDp = componentSizeDp,
            iconSizeDp = iconSizeDp,
            blurRadiusDp = blurRadiusDp,
            refractionHeightFrac = refractionHeightFrac,
            refractionAmountFrac = refractionAmountFrac,
            chromaticAberration = chromaticAberration,
            edgeDarkening = edgeDarkening
        )
    }

    internal fun applyConfig(config: GlassDebugConfig) {
        cornerRadiusFrac = config.cornerRadiusFrac
        componentSizeDp = config.componentSizeDp
        iconSizeDp = config.iconSizeDp
        blurRadiusDp = config.blurRadiusDp
        refractionHeightFrac = config.refractionHeightFrac
        refractionAmountFrac = config.refractionAmountFrac
        chromaticAberration = config.chromaticAberration
        edgeDarkening = config.edgeDarkening
    }

    fun roundedRectangle(maxCornerRadius: Dp): RoundedRectangle {
        return RoundedRectangle(maxCornerRadius * cornerRadiusFrac)
    }

    fun highlight(): Highlight {
        return Highlight.IOS27.copy(
            darkEdge = DarkEdge.Default.copy(alpha = edgeDarkening)
        )
    }

    fun applyEffects(scope: BackdropEffectScope) {
        with(scope) {
            vibrancy()
            blur(blurRadiusDp.dp.toPx())
            val minDimension = size.minDimension
            lens(
                refractionHeight = refractionHeightFrac * minDimension * 0.5f,
                refractionAmount = refractionAmountFrac * minDimension,
                depthEffect = true,
                chromaticAberration = chromaticAberration > 0f
            )
        }
    }
}

@Composable
fun rememberGlassDebugState(
    pageKey: String,
    fixedCornerRadiusFrac: Float? = null
): GlassDebugState {
    val applicationContext = LocalContext.current.applicationContext
    return remember(pageKey, fixedCornerRadiusFrac, applicationContext) {
        GlassDebugState(pageKey).apply {
            GlassDebugConfigStore(applicationContext).load(pageKey)?.let(::applyConfig)
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
    showIconSize: Boolean = false,
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

    if (showComponentSize || showIconSize) {
        LaunchedEffect(showComponentSize, showIconSize) {
            if (showComponentSize) {
                state.componentSizeDp = state.componentSizeDp.roundToInt().toFloat()
            }
            if (showIconSize) {
                state.iconSizeDp = state.iconSizeDp.roundToInt().toFloat()
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
                                48f..200f,
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
                        } else if (showCornerRadius) {
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
