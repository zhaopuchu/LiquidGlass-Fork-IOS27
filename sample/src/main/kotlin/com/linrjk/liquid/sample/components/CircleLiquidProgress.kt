package com.linrjk.liquid.sample.components

import android.view.ContextThemeWrapper
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.dynamicanimation.animation.DynamicAnimation
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.DeterminateDrawable
import com.linrjk.liquid.Backdrop
import com.linrjk.liquid.drawBackdrop
import com.linrjk.liquid.effects.blur
import com.linrjk.liquid.effects.colorControls
import com.linrjk.liquid.effects.lens
import com.linrjk.liquid.effects.vibrancy
import com.linrjk.liquid.highlight.Highlight
import kotlin.math.roundToInt

/**
 * 液态玻璃圆形进度，环本身复用 Material [CircularProgressIndicator]。
 * 不确定态切确定态时走 [CircularProgressIndicator.setProgressCompat]，
 * 会先跑完当前一圈再弹簧落到目标进度，和启动页倒计时环同一套动画。
 */
@Composable
fun CircleLiquidProgress(
    progress: Float,
    backdrop: Backdrop,
    size: Dp,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    centerText: String = "",
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    onMorphSettled: (() -> Unit)? = null,
    glass: GlassDebugState? = null,
    applyAppearance: Boolean = false,
    strokeWidth: Dp = Dp.Unspecified,
    tint: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified
) {
    glass?.cornerRadiusFrac
    glass?.componentSizeDp
    glass?.titleSizeSp
    glass?.blurRadiusDp
    glass?.refractionHeightFrac
    glass?.refractionAmountFrac
    glass?.chromaticAberration
    glass?.edgeDarkening
    glass?.presetIndex
    glass?.readHighlightParams()
    if (applyAppearance) {
        glass?.surfaceAlpha
        glass?.brightness
        glass?.saturation
    }

    val resolvedSize = if (glass != null) glass.componentSizeDp.dp else size
    val resolvedStroke =
        if (strokeWidth != Dp.Unspecified) strokeWidth
        else (resolvedSize.value * 0.125f).dp
    val resolvedTextSize = if (glass != null) glass.titleSizeSp.sp else 14f.sp
    val isLightTheme = !isSystemInDarkTheme()
    val indicatorColor = if (tint.isSpecified) tint else SampleAccentBlue
    val trackColor =
        if (isLightTheme) Color.Black.copy(alpha = 0.12f)
        else Color.White.copy(alpha = 0.18f)
    val resolvedTextColor =
        if (textColor.isSpecified) textColor
        else if (isLightTheme) Color.Black
        else Color.White
    val surfaceColor =
        if (applyAppearance && glass != null) {
            val baseColor =
                if (isLightTheme) Color(0xFFFAFAFA)
                else Color(0xFF121212)
            baseColor.copy(alpha = glass.surfaceAlpha)
        } else {
            Color.Unspecified
        }

    Box(
        modifier
            .requiredSize(resolvedSize)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // lens 只认圆角矩形，玻璃仍用 CircleShape；离屏后再 Clear 挖空圆心。
        Box(
            Modifier
                .requiredSize(resolvedSize)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        if (glass != null) {
                            if (applyAppearance) {
                                colorControls(
                                    brightness = glass.brightness,
                                    saturation = glass.saturation
                                )
                            }
                            glass.applyEffects(this)
                        } else {
                            vibrancy()
                            blur(0f)
                            lens(resolvedSize.toPx() * 0.1f, resolvedSize.toPx() * 0.2f)
                        }
                    },
                    highlight = {
                        if (glass != null) glass.highlight()
                        else Highlight.IOS27
                    },
                    onDrawSurface = {
                        if (surfaceColor.isSpecified) {
                            drawRect(surfaceColor)
                        }
                    },
                    onDrawFront = {
                        val holeRadius =
                            (this.size.minDimension / 2f) - resolvedStroke.toPx()
                        if (holeRadius > 0f) {
                            drawCircle(
                                color = Color.Transparent,
                                radius = holeRadius,
                                blendMode = BlendMode.Clear
                            )
                        }
                    }
                )
        )
        MaterialProgressRing(
            indeterminate = indeterminate,
            progress = progress,
            ringSize = resolvedSize,
            strokeWidth = resolvedStroke,
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            onMorphSettled = onMorphSettled
        )
        if (centerText.isNotEmpty()) {
            BasicText(
                centerText,
                style =
                    TextStyle(
                        color = resolvedTextColor,
                        fontSize = resolvedTextSize,
                        fontWeight = FontWeight.Medium
                    )
            )
        }
    }
}

@Composable
private fun MaterialProgressRing(
    indeterminate: Boolean,
    progress: Float,
    ringSize: Dp,
    strokeWidth: Dp,
    indicatorColor: Color,
    trackColor: Color,
    onMorphSettled: (() -> Unit)?
) {
    val density = LocalDensity.current
    val sizePx = with(density) { ringSize.roundToPx() }
    val strokePx = with(density) { strokeWidth.roundToPx() }
    val progressInt = (progress.coerceIn(0f, 1f) * 100f).roundToInt()
    val indicatorArgb = indicatorColor.toArgb()
    val onMorphSettledState by rememberUpdatedState(onMorphSettled)

    Box(
        Modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        // View 圆形不确定态默认不画轨道；底轨交给 Compose，View 侧轨道透明避免叠色
        Canvas(Modifier.size(ringSize)) {
            val stroke = strokeWidth.toPx()
            drawCircle(
                color = trackColor,
                radius = (this.size.minDimension - stroke) / 2f,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        AndroidView(
            factory = { context ->
                // Material 进度条要求 AppCompat 主题；sample 用的是平台 Theme.Material，
                // 只给这个 View 包一层，避免改整个应用主题。
                val themedContext =
                    ContextThemeWrapper(
                        context,
                        com.google.android.material.R.style.Theme_Material3_DayNight_NoActionBar
                    )
                CircularProgressIndicator(themedContext).apply {
                    this.indicatorSize = sizePx
                    trackThickness = strokePx
                    setIndicatorColor(indicatorArgb)
                    setTrackColor(android.graphics.Color.TRANSPARENT)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    background = null
                    max = 100
                    isIndeterminate = true
                    tag = ProgressRingHostState()
                }
            },
            modifier = Modifier.size(ringSize),
            update = { view ->
                view.indicatorSize = sizePx
                view.trackThickness = strokePx
                view.setIndicatorColor(indicatorArgb)
                view.setTrackColor(android.graphics.Color.TRANSPARENT)

                val host = (view.tag as? ProgressRingHostState) ?: ProgressRingHostState().also {
                    view.tag = it
                }
                val prev = host.applied

                if (indeterminate) {
                    if (!view.isIndeterminate) {
                        view.visibility = View.INVISIBLE
                        view.isIndeterminate = true
                        view.visibility = View.VISIBLE
                    }
                    clearMorphSettleListener(view, host)
                    host.applied =
                        ProgressRingAppliedState(
                            indeterminate = true,
                            progress = progressInt,
                            awaitingMorphSettle = false
                        )
                } else if (prev.awaitingMorphSettle) {
                    // morph 进行中，不重复触发 setProgressCompat
                } else if (prev.indeterminate || view.isIndeterminate) {
                    host.applied =
                        ProgressRingAppliedState(
                            indeterminate = false,
                            progress = progressInt,
                            awaitingMorphSettle = true
                        )
                    attachMorphSettleListener(view, host) {
                        if (host.applied.awaitingMorphSettle) {
                            host.applied = host.applied.copy(awaitingMorphSettle = false)
                            onMorphSettledState?.invoke()
                        }
                    }
                    view.setProgressCompat(progressInt, true)
                } else if (prev.progress != progressInt) {
                    view.setProgressCompat(progressInt, true)
                    host.applied = prev.copy(progress = progressInt)
                } else {
                    host.applied = prev.copy(indeterminate = false)
                }
            }
        )
    }
}

private data class ProgressRingAppliedState(
    val indeterminate: Boolean,
    val progress: Int,
    val awaitingMorphSettle: Boolean
)

private class ProgressRingHostState(
    var applied: ProgressRingAppliedState =
        ProgressRingAppliedState(
            indeterminate = true,
            progress = 0,
            awaitingMorphSettle = false
        ),
    var springListener: DynamicAnimation.OnAnimationEndListener? = null
)

private fun clearMorphSettleListener(
    view: CircularProgressIndicator,
    host: ProgressRingHostState
) {
    val listener = host.springListener ?: return
    (view.progressDrawable as? DeterminateDrawable<*>)
        ?.removeSpringAnimationEndListener(listener)
    host.springListener = null
}

private fun attachMorphSettleListener(
    view: CircularProgressIndicator,
    host: ProgressRingHostState,
    onSettled: () -> Unit
) {
    clearMorphSettleListener(view, host)
    val drawable = view.progressDrawable as? DeterminateDrawable<*> ?: run {
        onSettled()
        return
    }
    val listener = DynamicAnimation.OnAnimationEndListener { _, canceled, _, _ ->
        if (!canceled && !view.isIndeterminate) {
            clearMorphSettleListener(view, host)
            onSettled()
        }
    }
    host.springListener = listener
    drawable.addSpringAnimationEndListener(listener)
}
