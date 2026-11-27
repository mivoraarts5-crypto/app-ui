package com.example.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a crisp, unblurred flat-2D offset drop shadow behind any Compose Shape,
 * matching Bauhaus / Neo-Brutalist graphic illustration aesthetics.
 */
fun Modifier.hard2dShadow(
    shape: Shape,
    shadowColor: Color,
    offsetX: Dp = 3.dp,
    offsetY: Dp = 3.dp,
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(left = offsetX.toPx(), top = offsetY.toPx()) {
        drawOutline(outline = outline, color = shadowColor)
    }
}
