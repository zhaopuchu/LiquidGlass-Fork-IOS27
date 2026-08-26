package com.linrjk.liquid.sample.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linrjk.liquid.Backdrop

@Composable
fun LiquidTopAppBar(
    backdrop: Backdrop,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    glass: GlassDebugState? = null,
    title: String = "这里是标题",
    titleSize: TextUnit = 20f.sp,
    buttonSize: Dp = 48f.dp,
    iconSize: Dp = 24f.dp,
    actionSpacing: Dp = 8f.dp,
    horizontalPadding: Dp = 16f.dp,
    contentColor: Color? = null,
    searchIcon: ImageVector = MaterialSearchIcon,
    addIcon: ImageVector = MaterialAddIcon
) {
    val resolvedContentColor =
        contentColor ?: if (isSystemInDarkTheme()) Color.White else Color.Black
    val actionAreaWidth = buttonSize * 2f + actionSpacing

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(buttonSize)
                .padding(horizontal = horizontalPadding)
    ) {
        CircleLiquidButton(
            onClick = onBackClick,
            backdrop = backdrop,
            size = buttonSize,
            modifier = Modifier.align(Alignment.CenterStart),
            glass = glass,
            iconSize = iconSize,
            iconTint = resolvedContentColor,
            contentDescription = "返回"
        )

        BasicText(
            text = title,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = actionAreaWidth + 8f.dp),
            style =
                TextStyle(
                    color = resolvedContentColor,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(actionSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleLiquidButton(
                onClick = onSearchClick,
                backdrop = backdrop,
                size = buttonSize,
                glass = glass,
                icon = searchIcon,
                iconSize = iconSize,
                iconTint = resolvedContentColor,
                contentDescription = "搜索"
            )
            CircleLiquidButton(
                onClick = onAddClick,
                backdrop = backdrop,
                size = buttonSize,
                glass = glass,
                icon = addIcon,
                iconSize = iconSize,
                iconTint = resolvedContentColor,
                contentDescription = "添加"
            )
        }
    }
}

private val MaterialSearchIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "MaterialSearch",
        defaultWidth = 24f.dp,
        defaultHeight = 24f.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(784f, 840f)
            lineTo(532f, 588f)
            quadTo(502f, 612f, 463f, 626f)
            reflectiveQuadTo(380f, 640f)
            quadTo(271f, 640f, 195.5f, 564.5f)
            reflectiveQuadTo(120f, 380f)
            quadTo(120f, 271f, 195.5f, 195.5f)
            reflectiveQuadTo(380f, 120f)
            quadTo(489f, 120f, 564.5f, 195.5f)
            reflectiveQuadTo(640f, 380f)
            quadTo(640f, 424f, 626f, 463f)
            reflectiveQuadTo(588f, 532f)
            lineTo(840f, 784f)
            close()
            moveTo(380f, 560f)
            quadTo(455f, 560f, 507.5f, 507.5f)
            reflectiveQuadTo(560f, 380f)
            quadTo(560f, 305f, 507.5f, 252.5f)
            reflectiveQuadTo(380f, 200f)
            quadTo(305f, 200f, 252.5f, 252.5f)
            reflectiveQuadTo(200f, 380f)
            quadTo(200f, 455f, 252.5f, 507.5f)
            reflectiveQuadTo(380f, 560f)
            close()
        }
    }.build()
}

private val MaterialAddIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "MaterialAdd",
        defaultWidth = 24f.dp,
        defaultHeight = 24f.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 760f)
            lineTo(440f, 520f)
            lineTo(200f, 520f)
            lineTo(200f, 440f)
            lineTo(440f, 440f)
            lineTo(440f, 200f)
            lineTo(520f, 200f)
            lineTo(520f, 440f)
            lineTo(760f, 440f)
            lineTo(760f, 520f)
            lineTo(520f, 520f)
            lineTo(520f, 760f)
            close()
        }
    }.build()
}
