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
    val saturation: Float = 1.5f
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
                preferences.getFloat(key(pageKey, SATURATION), defaults.saturation)
        )
    }

    fun save(pageKey: String, config: GlassDebugConfig) {
        preferences
            .edit()
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
    }
}
