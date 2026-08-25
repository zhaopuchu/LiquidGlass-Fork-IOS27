package com.linrjk.liquid.sample.destinations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.drawBackdrop
import com.linrjk.liquid.sample.BackdropDemoScaffold
import com.linrjk.liquid.sample.components.GlassDebugOverlay
import com.linrjk.liquid.sample.components.rememberGlassDebugState
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlassPlaygroundContent() {
    val animationScope = rememberCoroutineScope()
    val offsetAnimation = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val zoomAnimation = remember { Animatable(1f) }
    val rotationAnimation = remember { Animatable(0f) }
    val glass = rememberGlassDebugState("GlassPlayground")
    glass.cornerRadiusFrac
    glass.blurRadiusDp
    glass.refractionHeightFrac
    glass.refractionAmountFrac
    glass.chromaticAberration
    glass.edgeDarkening

    BackdropDemoScaffold { backdrop ->
        GlassDebugOverlay(
            state = glass,
            backdrop = backdrop,
            onReset = {
                animationScope.launch {
                    launch { offsetAnimation.animateTo(Offset.Zero) }
                    launch { zoomAnimation.animateTo(1f) }
                    launch { rotationAnimation.animateTo(0f) }
                }
                glass.reset()
            }
        ) {
            Box(
                Modifier
                    .padding(top = 48f.dp)
                    .statusBarsPadding()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { glass.roundedRectangle(128f.dp) },
                        effects = { glass.applyEffects(this) },
                        highlight = { glass.highlight() },
                        layerBlock = {
                            val offset = offsetAnimation.value
                            val zoom = zoomAnimation.value
                            val rotation = rotationAnimation.value
                            translationX = offset.x
                            translationY = offset.y
                            scaleX = zoom
                            scaleY = zoom
                            rotationZ = rotation
                        }
                    )
                    .pointerInput(animationScope) {
                        fun Offset.rotateBy(angle: Float): Offset {
                            val angleInRadians = angle * (PI / 180)
                            val cos = cos(angleInRadians)
                            val sin = sin(angleInRadians)
                            return Offset(
                                (x * cos - y * sin).toFloat(),
                                (x * sin + y * cos).toFloat()
                            )
                        }

                        detectTransformGestures { _, pan, gestureZoom, gestureRotate ->
                            val offset = offsetAnimation.value
                            val zoom = zoomAnimation.value
                            val rotation = rotationAnimation.value

                            val targetZoom = zoom * gestureZoom
                            val targetRotation = rotation + gestureRotate
                            val targetOffset = offset + pan.rotateBy(targetRotation) * targetZoom

                            animationScope.launch {
                                offsetAnimation.snapTo(targetOffset)
                                zoomAnimation.snapTo(targetZoom)
                                rotationAnimation.snapTo(targetRotation)
                            }
                        }
                    }
                    .size(256f.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}
