package com.linrjk.liquid.internal

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path

/**
 * 把轮廓整体向内收缩 [inset]，用于绘制不依赖裁剪的等宽描边。
 * 路径裁剪没有抗锯齿，先内缩再描边可以避免边缘出现锯齿与断线。
 * 返回 `null` 表示该轮廓无法安全内缩，调用方需要退回裁剪方案。
 */
internal fun insetOutline(outline: Outline, inset: Float): Outline? {
    if (inset <= 0f) return outline
    return when (outline) {
        is Outline.Rectangle -> {
            val rect = outline.rect
            if (rect.width <= inset * 2f || rect.height <= inset * 2f) null
            else Outline.Rectangle(rect.deflate(inset))
        }

        is Outline.Rounded -> {
            val roundRect = outline.roundRect
            if (roundRect.width <= inset * 2f || roundRect.height <= inset * 2f) {
                null
            } else {
                Outline.Rounded(
                    RoundRect(
                        left = roundRect.left + inset,
                        top = roundRect.top + inset,
                        right = roundRect.right - inset,
                        bottom = roundRect.bottom - inset,
                        topLeftCornerRadius = roundRect.topLeftCornerRadius.deflate(inset),
                        topRightCornerRadius = roundRect.topRightCornerRadius.deflate(inset),
                        bottomRightCornerRadius = roundRect.bottomRightCornerRadius.deflate(inset),
                        bottomLeftCornerRadius = roundRect.bottomLeftCornerRadius.deflate(inset)
                    )
                )
            }
        }

        is Outline.Generic -> null
    }
}

private fun CornerRadius.deflate(inset: Float): CornerRadius {
    return CornerRadius(
        (x - inset).coerceAtLeast(0f),
        (y - inset).coerceAtLeast(0f)
    )
}

internal fun Canvas.clipOutline(outline: Outline, path: Path?) {
    when (outline) {
        is Outline.Rectangle -> clipRect(outline.rect)
        is Outline.Rounded -> {
            path!!.rewind()
            path.addRoundRect(outline.roundRect)
            clipPath(path)
        }

        is Outline.Generic -> clipPath(outline.path)
    }
}
