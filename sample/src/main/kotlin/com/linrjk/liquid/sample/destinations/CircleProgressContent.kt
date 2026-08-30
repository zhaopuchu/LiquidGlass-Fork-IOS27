package com.linrjk.liquid.sample.destinations

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.CircleLiquidProgress
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.rememberGlassDebugState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private const val CircleProgressCountdownSeconds = 3
private const val CircleProgressLoadingMillis = 1800L
private const val CircleProgressStepMillis = 1_000L
private const val CircleProgressMorphFallbackMillis = 2_500L

private enum class CircleProgressPhase {
    Loading,
    Morphing,
    Counting
}

@Composable
fun CircleProgressContent() {
    val isLightTheme = !isSystemInDarkTheme()
    val backgroundColor =
        if (isLightTheme) Color(0xFFF2F1F6)
        else Color(0xFF121212)
    val textColor = if (isLightTheme) Color.Black else Color.White
    val glass =
        rememberGlassDebugState(
            pageKey = "CircleProgress",
            fixedCornerRadiusFrac = 1f,
            defaultComponentSizeDp = 48f,
            defaultTitleSizeSp = 14f,
            defaultSurfaceAlpha = 0f,
            defaultBrightness = 0f,
            defaultSaturation = 1f,
            defaultEdgeDarkening = 0.18f
        )
    var cycle by rememberSaveable { mutableIntStateOf(0) }
    var phase by rememberSaveable { mutableStateOf(CircleProgressPhase.Loading) }
    var remaining by rememberSaveable { mutableIntStateOf(CircleProgressCountdownSeconds) }
    var progress by rememberSaveable { mutableFloatStateOf(1f) }
    var skipped by remember { mutableStateOf(false) }
    val morphSettled = remember(cycle) { CompletableDeferred<Unit>() }

    LaunchedEffect(cycle) {
        skipped = false
        phase = CircleProgressPhase.Loading
        remaining = CircleProgressCountdownSeconds
        progress = 1f
        delay(CircleProgressLoadingMillis)
        if (skipped) {
            cycle++
            return@LaunchedEffect
        }

        // 和启动页一致：切确定态时先满环，中间显示首秒，等 morph 弹簧落位后再往下减。
        phase = CircleProgressPhase.Morphing
        remaining = CircleProgressCountdownSeconds
        progress = 1f
        val fallback = launch {
            delay(CircleProgressMorphFallbackMillis)
            morphSettled.complete(Unit)
        }
        val fillStartMs = System.nanoTime()
        morphSettled.await()
        fallback.cancel()
        if (skipped) {
            cycle++
            return@LaunchedEffect
        }
        val fillElapsedMs = (System.nanoTime() - fillStartMs) / 1_000_000L
        val fillRemainMs = max(0L, CircleProgressStepMillis - fillElapsedMs)
        if (fillRemainMs > 0L) delay(fillRemainMs)
        if (skipped) {
            cycle++
            return@LaunchedEffect
        }

        for (next in (CircleProgressCountdownSeconds - 1) downTo 0) {
            phase = CircleProgressPhase.Counting
            remaining = next
            progress = next / CircleProgressCountdownSeconds.toFloat()
            delay(CircleProgressStepMillis)
            if (skipped) break
        }
        delay(600)
        cycle++
    }

    val counting =
        phase == CircleProgressPhase.Morphing || phase == CircleProgressPhase.Counting

    BackdropDemoScaffold(fallbackColor = backgroundColor) { backdrop ->
        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            showCornerRadius = false,
            showComponentSize = true,
            showTitleSize = true,
            showColorControls = true,
            showSurfaceAlpha = true,
            showTintPicker = true,
            showPresetPicker = true,
            showHighlightParams = true,
            autoSave = true,
            showReset = false,
            title = "Circle Progress"
        ) {
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(top = 48f.dp),
                verticalArrangement = Arrangement.spacedBy(16f.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircleLiquidProgress(
                    progress = progress,
                    backdrop = backdrop,
                    size = glass.componentSizeDp.dp,
                    indeterminate = phase == CircleProgressPhase.Loading,
                    centerText = if (counting) remaining.toString() else "",
                    enabled = counting,
                    onClick = {
                        skipped = true
                        morphSettled.complete(Unit)
                    },
                    onMorphSettled = { morphSettled.complete(Unit) },
                    glass = glass,
                    applyAppearance = true,
                    tint = glass.tintColor,
                    textColor = textColor
                )
                BasicText(
                    when (phase) {
                        CircleProgressPhase.Loading -> "Loading"
                        CircleProgressPhase.Morphing -> "Filling"
                        CircleProgressPhase.Counting ->
                            if (remaining > 0) "Tap to skip" else "Restarting"
                    },
                    style = TextStyle(textColor.copy(alpha = 0.55f), 14f.sp)
                )
            }
        }
    }
}
