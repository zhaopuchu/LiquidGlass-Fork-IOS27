package com.linrjk.liquid.sample.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.Backdrop

@Composable
fun CircleLiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    size: Dp,
    modifier: Modifier = Modifier,
    glass: GlassDebugState? = null,
    icon: ImageVector = MaterialBackIcon,
    iconSize: Dp = 32f.dp,
    iconTint: Color = Color.Black,
    contentDescription: String? = "Back"
) {
    LiquidButton(
        onClick = onClick,
        backdrop = backdrop,
        modifier = modifier.size(size),
        glass = glass,
        shape = CircleShape,
        height = size,
        horizontalContentPadding = 0f.dp
    ) {
        Image(
            painter = rememberVectorPainter(icon),
            contentDescription = contentDescription,
            modifier = Modifier.requiredSize(iconSize),
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}

private val MaterialBackIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "MaterialArrowBack",
        defaultWidth = 24f.dp,
        defaultHeight = 24f.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 800f)
            lineTo(160f, 480f)
            lineTo(480f, 160f)
            lineTo(537f, 216f)
            lineTo(313f, 440f)
            lineTo(800f, 440f)
            lineTo(800f, 520f)
            lineTo(313f, 520f)
            lineTo(537f, 744f)
            close()
        }
    }.build()
}
