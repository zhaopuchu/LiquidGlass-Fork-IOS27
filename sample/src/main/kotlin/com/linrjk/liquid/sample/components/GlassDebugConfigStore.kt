package com.linrjk.liquid.sample.components

import android.content.Context

internal data class GlassDebugConfig(
    val cornerRadiusFrac: Float = 0.5f,
    val componentSizeDp: Float = 96f,
    val componentWidthDp: Float = 240f,
    val componentHeightDp: Float = 48f,
    val iconSizeDp: Float = 32f,
    val titleSizeSp: Float = 20f,
    val blurRadiusDp: Float = 0f,
    val refractionHeightFrac: Float = 0.2f,
    val refractionAmountFrac: Float = 0.2f,
    val chromaticAberration: Float = 0f,
    val edgeDarkening: Float = 0.18f,
    val surfaceAlpha: Float = 0.6f,
    val backgroundDim: Float = 0.23f,
    val brightness: Float = 0.2f,
    val saturation: Float = 1.5f,
    val tintIndex: Int = 0,
    val presetIndex: Int = 1,
    // 高光预设的可调参数，键取自 AllHighlightParams；缺项表示沿用参数定义的默认值。
    val highlightParams: Map<String, Float> = emptyMap()
)

internal class GlassDebugConfigStore(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun load(
        pageKey: String,
        defaults: GlassDebugConfig = GlassDebugConfig()
    ): GlassDebugConfig? {
        if (!preferences.getBoolean(key(pageKey, SAVED), false)) return null

        return GlassDebugConfig(
            cornerRadiusFrac =
                preferences.getFloat(key(pageKey, CORNER_RADIUS), defaults.cornerRadiusFrac),
            componentSizeDp =
                preferences.getFloat(key(pageKey, COMPONENT_SIZE), defaults.componentSizeDp),
            componentWidthDp =
                preferences.getFloat(key(pageKey, COMPONENT_WIDTH), defaults.componentWidthDp),
            componentHeightDp =
                preferences.getFloat(key(pageKey, COMPONENT_HEIGHT), defaults.componentHeightDp),
            iconSizeDp =
                preferences.getFloat(key(pageKey, ICON_SIZE), defaults.iconSizeDp),
            titleSizeSp =
                preferences.getFloat(key(pageKey, TITLE_SIZE), defaults.titleSizeSp),
            blurRadiusDp =
                preferences.getFloat(key(pageKey, BLUR_RADIUS), defaults.blurRadiusDp),
            refractionHeightFrac =
                preferences.getFloat(
                    key(pageKey, REFRACTION_HEIGHT),
                    defaults.refractionHeightFrac
                ),
            refractionAmountFrac =
                preferences.getFloat(
                    key(pageKey, REFRACTION_AMOUNT),
                    defaults.refractionAmountFrac
                ),
            chromaticAberration =
                preferences.getFloat(
                    key(pageKey, CHROMATIC_ABERRATION),
                    defaults.chromaticAberration
                ),
            edgeDarkening =
                preferences.getFloat(key(pageKey, EDGE_DARKENING), defaults.edgeDarkening),
            surfaceAlpha =
                preferences.getFloat(key(pageKey, SURFACE_ALPHA), defaults.surfaceAlpha),
            backgroundDim =
                preferences.getFloat(key(pageKey, BACKGROUND_DIM), defaults.backgroundDim),
            brightness =
                preferences.getFloat(key(pageKey, BRIGHTNESS), defaults.brightness),
            saturation =
                preferences.getFloat(key(pageKey, SATURATION), defaults.saturation),
            tintIndex =
                preferences.getInt(key(pageKey, TINT_INDEX), defaults.tintIndex).coerceIn(0, 2),
            presetIndex =
                preferences.getInt(key(pageKey, PRESET_INDEX), defaults.presetIndex).coerceIn(0, 1),
            highlightParams =
                AllHighlightParams.associate { param ->
                    val storeKey = key(pageKey, HIGHLIGHT_PARAM_PREFIX + param.key)
                    val fallback = defaults.highlightParams[param.key] ?: param.default
                    param.key to preferences.getFloat(storeKey, fallback)
                }
        )
    }

    fun save(pageKey: String, config: GlassDebugConfig) {
        val editor = preferences.edit()
        AllHighlightParams.forEach { param ->
            editor.putFloat(
                key(pageKey, HIGHLIGHT_PARAM_PREFIX + param.key),
                config.highlightParams[param.key] ?: param.default
            )
        }
        editor
            .putFloat(key(pageKey, CORNER_RADIUS), config.cornerRadiusFrac)
            .putFloat(key(pageKey, COMPONENT_SIZE), config.componentSizeDp)
            .putFloat(key(pageKey, COMPONENT_WIDTH), config.componentWidthDp)
            .putFloat(key(pageKey, COMPONENT_HEIGHT), config.componentHeightDp)
            .putFloat(key(pageKey, ICON_SIZE), config.iconSizeDp)
            .putFloat(key(pageKey, TITLE_SIZE), config.titleSizeSp)
            .putFloat(key(pageKey, BLUR_RADIUS), config.blurRadiusDp)
            .putFloat(key(pageKey, REFRACTION_HEIGHT), config.refractionHeightFrac)
            .putFloat(key(pageKey, REFRACTION_AMOUNT), config.refractionAmountFrac)
            .putFloat(key(pageKey, CHROMATIC_ABERRATION), config.chromaticAberration)
            .putFloat(key(pageKey, EDGE_DARKENING), config.edgeDarkening)
            .putFloat(key(pageKey, SURFACE_ALPHA), config.surfaceAlpha)
            .putFloat(key(pageKey, BACKGROUND_DIM), config.backgroundDim)
            .putFloat(key(pageKey, BRIGHTNESS), config.brightness)
            .putFloat(key(pageKey, SATURATION), config.saturation)
            .putInt(key(pageKey, TINT_INDEX), config.tintIndex.coerceIn(0, 2))
            .putInt(key(pageKey, PRESET_INDEX), config.presetIndex.coerceIn(0, 1))
            .putBoolean(key(pageKey, SAVED), true)
            .apply()
    }

    private fun key(pageKey: String, property: String): String {
        return "$pageKey.$property"
    }

    private companion object {
        const val PREFERENCES_NAME = "glass_debug_configs"
        const val SAVED = "saved"
        const val CORNER_RADIUS = "corner_radius"
        const val COMPONENT_SIZE = "component_size"
        const val COMPONENT_WIDTH = "component_width"
        const val COMPONENT_HEIGHT = "component_height"
        const val ICON_SIZE = "icon_size"
        const val TITLE_SIZE = "title_size"
        const val BLUR_RADIUS = "blur_radius"
        const val REFRACTION_HEIGHT = "refraction_height"
        const val REFRACTION_AMOUNT = "refraction_amount"
        const val CHROMATIC_ABERRATION = "chromatic_aberration"
        const val EDGE_DARKENING = "edge_darkening"
        const val SURFACE_ALPHA = "surface_alpha"
        const val BACKGROUND_DIM = "background_dim"
        const val BRIGHTNESS = "brightness"
        const val SATURATION = "saturation"
        const val TINT_INDEX = "tint_index"
        const val PRESET_INDEX = "preset_index"
        const val HIGHLIGHT_PARAM_PREFIX = "highlight_param."
    }
}
