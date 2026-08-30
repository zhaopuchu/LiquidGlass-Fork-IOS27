package com.linrjk.liquid.sample.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.linrjk.liquid.highlight.HighlightStyle
import com.linrjk.liquid.sample.Block
import com.linrjk.liquid.shapes.RoundedRectangle
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

internal val SampleAccentBlue = Color(0xFF0088FF)
internal val SampleAccentOrange = Color(0xFFFF8D28)

private const val SampleTintMaxIndex = 2

internal fun sampleAccentColor(index: Int): Color {
    return when (index) {
        1 -> SampleAccentOrange
        2 -> Color.Unspecified
        else -> SampleAccentBlue
    }
}

internal fun sampleAccentLabel(index: Int): String {
    return when (index) {
        1 -> "Orange"
        2 -> "Clear"
        else -> "Blue"
    }
}

private const val SamplePresetMaxIndex = 1
private const val HighlightPresetDefault = 0
private const val HighlightPresetIos27 = 1

internal val HighlightPresetLabels = listOf("Highlight", "iOS 27")

internal fun highlightPresetLabel(index: Int): String {
    return HighlightPresetLabels.getOrElse(index.coerceIn(0, SamplePresetMaxIndex)) {
        HighlightPresetLabels[HighlightPresetIos27]
    }
}

/**
 * 高光预设的一个可调参数。滑块、默认值、持久化都以这里的定义为准，
 * 想再暴露一个参数只需要在下面的列表里加一行。
 */
internal class HighlightParam(
    val key: String,
    val label: String,
    val range: ClosedFloatingPointRange<Float>,
    val default: Float,
    val step: Float = 0.001f,
    val decimalPlaces: Int = 2
)

// Highlight（Default）预设：默认值对齐 Highlight.Default 与 HighlightStyle.Default。
private val DefaultHighlightWidth = HighlightParam("d_width", "Width (dp)", 0f..4f, 0.5f, 0.01f)
private val DefaultHighlightBlur = HighlightParam("d_blur", "Blur (dp)", 0f..4f, 0.25f, 0.01f)
private val DefaultHighlightAlpha = HighlightParam("d_alpha", "Layer alpha", 0f..1f, 1f)
private val DefaultHighlightColorAlpha = HighlightParam("d_color_alpha", "Color alpha", 0f..1f, 0.5f)
private val DefaultHighlightAngle =
    HighlightParam("d_angle", "Light angle", 0f..360f, 45f, 1f, decimalPlaces = 0)
private val DefaultHighlightFalloff = HighlightParam("d_falloff", "Falloff", 0f..8f, 1f, 0.01f)
private val DefaultHighlightAmbient = HighlightParam("d_ambient", "Ambient", 0f..1f, 0f)
private val DefaultHighlightEdgeBlend = HighlightParam("d_edge_blend", "Edge blend", 0f..1f, 0f)

// iOS 27 预设：默认值对齐 Highlight.IOS27 与 HighlightStyle.IOS27。
// 该预设下 Highlight.blurRadius 恒为 0、描边宽度取自暗边，所以只暴露一个 Stroke width。
private val Ios27StrokeWidth = HighlightParam("i_width", "Stroke width (dp)", 0f..4f, 0.5f, 0.01f)
private val Ios27HighlightAlpha = HighlightParam("i_alpha", "Layer alpha", 0f..1f, 1f)
private val Ios27HighlightColorAlpha = HighlightParam("i_color_alpha", "Color alpha", 0f..1f, 1f)
private val Ios27Falloff = HighlightParam("i_falloff", "Falloff", 0f..16f, 16f, 0.01f)
private val Ios27Gain = HighlightParam("i_gain", "Gain", 0f..8f, 2.5f, 0.01f)
private val Ios27DarkEdgeFade = HighlightParam("i_dark_fade", "Dark edge fade", 0f..1f, 0.5f)

private val DefaultPresetParams =
    listOf(
        DefaultHighlightWidth,
        DefaultHighlightBlur,
        DefaultHighlightAlpha,
        DefaultHighlightColorAlpha,
        DefaultHighlightAngle,
        DefaultHighlightFalloff,
        DefaultHighlightAmbient,
        DefaultHighlightEdgeBlend
    )

private val Ios27PresetParams =
    listOf(
        Ios27StrokeWidth,
        Ios27HighlightAlpha,
        Ios27HighlightColorAlpha,
        Ios27Falloff,
        Ios27Gain,
        Ios27DarkEdgeFade
    )

internal val AllHighlightParams = DefaultPresetParams + Ios27PresetParams

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
    saturation: Float = 1.5f,
    tintIndex: Int = 0,
    presetIndex: Int = HighlightPresetIos27
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
    var tintIndex by mutableIntStateOf(tintIndex.coerceIn(0, SampleTintMaxIndex))
    var presetIndex by mutableIntStateOf(presetIndex.coerceIn(0, SamplePresetMaxIndex))

    // 只存放被改过的高光参数，没有条目就回落到参数定义里的默认值。
    private val highlightParamValues = mutableStateMapOf<String, Float>()

    val tintColor: Color
        get() = sampleAccentColor(tintIndex)

    internal fun highlightParam(param: HighlightParam): Float {
        return highlightParamValues[param.key] ?: param.default
    }

    internal fun setHighlightParam(param: HighlightParam, value: Float) {
        highlightParamValues[param.key] =
            value.coerceIn(param.range.start, param.range.endInclusive)
    }

    internal fun presetParams(): List<HighlightParam> {
        return if (presetIndex == HighlightPresetDefault) DefaultPresetParams
        else Ios27PresetParams
    }

    /**
     * 供玻璃组件在组合期调用。
     * drawBackdrop 的 highlight lambda 只在绘制期读状态，本身不会触发失效，
     * 必须在组合期把这些参数读一遍，改动才能引起重组 → 重新下发 lambda → 重绘。
     */
    internal fun readHighlightParams() {
        presetParams().forEach { highlightParam(it) }
    }

    fun applyPreset(index: Int) {
        presetIndex = index.coerceIn(0, SamplePresetMaxIndex)
    }

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
            saturation = saturation,
            tintIndex = tintIndex,
            presetIndex = presetIndex,
            highlightParams = highlightParamValues.toMap()
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
        tintIndex = config.tintIndex.coerceIn(0, SampleTintMaxIndex)
        presetIndex = config.presetIndex.coerceIn(0, SamplePresetMaxIndex)
        highlightParamValues.clear()
        highlightParamValues.putAll(config.highlightParams)
    }

    fun roundedRectangle(maxCornerRadius: Dp): RoundedRectangle {
        return RoundedRectangle(maxCornerRadius * cornerRadiusFrac)
    }

    @Suppress("UNUSED_PARAMETER")
    fun highlight(
        edgeDarkening: Float = this.edgeDarkening,
        spread: Dp = DarkEdge.Default.spread
    ): Highlight {
        return when (presetIndex) {
            HighlightPresetDefault ->
                Highlight(
                    width = highlightParam(DefaultHighlightWidth).dp,
                    blurRadius = highlightParam(DefaultHighlightBlur).dp,
                    alpha = highlightParam(DefaultHighlightAlpha),
                    style =
                        HighlightStyle.Default(
                            color =
                                Color.White.copy(
                                    alpha = highlightParam(DefaultHighlightColorAlpha)
                                ),
                            angle = highlightParam(DefaultHighlightAngle),
                            falloff = highlightParam(DefaultHighlightFalloff),
                            ambient = highlightParam(DefaultHighlightAmbient),
                            edgeBlend = highlightParam(DefaultHighlightEdgeBlend)
                        )
                )

            else -> {
                val strokeWidth = highlightParam(Ios27StrokeWidth).dp
                Highlight(
                    width = strokeWidth,
                    alpha = highlightParam(Ios27HighlightAlpha),
                    style =
                        HighlightStyle.IOS27(
                            color =
                                Color.White.copy(alpha = highlightParam(Ios27HighlightColorAlpha)),
                            falloff = highlightParam(Ios27Falloff),
                            gain = highlightParam(Ios27Gain),
                            darkEdgeFade = highlightParam(Ios27DarkEdgeFade)
                        ),
                    // 沿用预设自带的暗边（spread/blur/directionality 均为 0），
                    // 只覆盖面板能调的宽度和浓度。
                    darkEdge =
                        Highlight.IOS27.darkEdge?.copy(
                            width = strokeWidth,
                            alpha = edgeDarkening
                        )
                )
            }
        }
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
    defaultSaturation: Float = 1.5f,
    defaultTintIndex: Int = 0,
    defaultPresetIndex: Int = HighlightPresetIos27
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
        defaultTintIndex,
        defaultPresetIndex,
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
                saturation = defaultSaturation,
                tintIndex = defaultTintIndex,
                presetIndex = defaultPresetIndex
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
            saturation = defaultSaturation,
            tintIndex = defaultTintIndex,
            presetIndex = defaultPresetIndex
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
    showTintPicker: Boolean = false,
    showPresetPicker: Boolean = false,
    showHighlightParams: Boolean = false,
    autoSave: Boolean = false,
    showReset: Boolean = true,
    onReset: () -> Unit = { state.reset() },
    title: String? = null,
    header: (@Composable ColumnScope.(Backdrop) -> Unit)? = null,
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
        DisposableEffect(state) {
            onDispose {
                configStore.save(state.pageKey, state.toConfig())
            }
        }
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
                    // 参数变多后面板会超出屏幕，限高并让内容滚动。
                    .heightIn(max = 480f.dp)
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
                    .padding(16f.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12f.dp)
            ) {
                if (title != null) {
                    BasicText(
                        title,
                        Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(Color.Black, 16f.sp, FontWeight.Medium)
                    )
                }
                header?.invoke(this, sheetBackdrop)
                if (showPresetPicker) {
                    GlassDebugPresetPicker(
                        selectedIndex = state.presetIndex,
                        onSelect = { state.applyPreset(it) },
                        backdrop = sheetBackdrop
                    )
                }
                if (showTintPicker) {
                    GlassDebugTintPicker(
                        selectedIndex = state.tintIndex,
                        onSelect = { state.tintIndex = it },
                        backdrop = sheetBackdrop
                    )
                }
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
                if (showHighlightParams) {
                    GlassDebugHighlightParams(state, sheetBackdrop)
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

/** 当前高光预设的全部可调参数，两列平铺。 */
@Composable
private fun GlassDebugHighlightParams(
    state: GlassDebugState,
    backdrop: Backdrop
) {
    val params = state.presetParams()
    BasicText(
        "${highlightPresetLabel(state.presetIndex)} parameters",
        style = TextStyle(Color.Black, 15f.sp, FontWeight.Medium)
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12f.dp)
    ) {
        for (column in 0..1) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12f.dp)
            ) {
                params
                    .filterIndexed { index, _ -> index % 2 == column }
                    .forEach { param ->
                        GlassDebugSlider(
                            param.label,
                            { state.highlightParam(param) },
                            param.range,
                            param.step,
                            backdrop,
                            decimalPlaces = param.decimalPlaces
                        ) {
                            state.setHighlightParam(param, it)
                        }
                    }
            }
        }
    }
}

@Composable
private fun GlassDebugPresetPicker(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop
) {
    GlassDebugChoiceRow(
        title = "Highlight  ${highlightPresetLabel(selectedIndex)}",
        selectedIndex = selectedIndex,
        labels = HighlightPresetLabels,
        tints = List(HighlightPresetLabels.size) { Color.Unspecified },
        onSelect = onSelect,
        backdrop = backdrop
    )
}

@Composable
private fun GlassDebugTintPicker(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop
) {
    GlassDebugChoiceRow(
        title = "Tint  ${sampleAccentLabel(selectedIndex)}",
        selectedIndex = selectedIndex,
        labels = listOf("Blue", "Orange", "Clear"),
        tints = listOf(SampleAccentBlue, SampleAccentOrange, Color.Unspecified),
        onSelect = onSelect,
        backdrop = backdrop
    )
}

@Composable
private fun GlassDebugChoiceRow(
    title: String,
    selectedIndex: Int,
    labels: List<String>,
    tints: List<Color>,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop
) {
    Column(verticalArrangement = Arrangement.spacedBy(8f.dp)) {
        BasicText(title)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8f.dp)
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val tint = tints.getOrElse(index) { Color.Unspecified }
                val buttonTint =
                    if (tint.isSpecified) tint
                    else if (selected) SampleAccentBlue
                    else Color.Unspecified
                LiquidButton(
                    onClick = { onSelect(index) },
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f),
                    isInteractive = false,
                    tint = buttonTint,
                    height = 40f.dp
                ) {
                    BasicText(
                        if (selected) "$label  ✓" else label,
                        style =
                            TextStyle(
                                color = if (buttonTint.isSpecified) Color.White else Color.Black,
                                fontSize = 15f.sp
                            )
                    )
                }
            }
        }
    }
}
