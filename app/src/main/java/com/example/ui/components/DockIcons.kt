package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DockApp
import com.example.ui.theme.IconBrowserBlue
import com.example.ui.theme.IconBrowserLightBlue
import com.example.ui.theme.IconIgOrange
import com.example.ui.theme.IconIgPink
import com.example.ui.theme.IconIgPurple
import com.example.ui.theme.IconIgYellow
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconNotesBack
import com.example.ui.theme.IconNotesFront
import com.example.ui.theme.IconPhoneBg
import com.example.ui.theme.IconPhoneGreen
import com.example.ui.theme.IconTelegramBlue
import com.example.ui.theme.IconTwitterCyan
import com.example.ui.theme.IconWhatsAppGreen
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite

@Composable
fun DockAppButton(
    app: DockApp,
    isExpanded: Boolean,
    isNeighborNudged: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 34.dp,
) {
    val palette = LocalTropicalPalette.current
    val label = stringResource(app.labelRes)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val fluidDpSpec = spring<Dp>(dampingRatio = 0.64f, stiffness = Spring.StiffnessMediumLow)
    val fluidFloatSpec = spring<Float>(dampingRatio = 0.62f, stiffness = Spring.StiffnessMediumLow)

    // ColorOS Parallel Motion: active icon lifts upward & morphs from circle -> frosted glass capsule
    val offsetY by animateDpAsState(
        targetValue = when {
            isExpanded -> (-6).dp
            isNeighborNudged -> (-2).dp
            else -> 0.dp
        },
        animationSpec = fluidDpSpec,
        label = "dock_offset_y",
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 18.dp else 24.dp,
        animationSpec = fluidDpSpec,
        label = "dock_corner",
    )
    val containerHeight by animateDpAsState(
        targetValue = if (isExpanded) 48.dp else 46.dp,
        animationSpec = fluidDpSpec,
        label = "dock_height",
    )
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.88f
            isExpanded -> 1.05f
            isNeighborNudged -> 0.96f
            else -> 1f
        },
        animationSpec = fluidFloatSpec,
        label = "dock_scale",
    )
    val pillColor by animateColorAsState(
        targetValue = if (isExpanded) palette.glassDarkSurface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "dock_pill_bg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isExpanded) palette.glassDarkBorder else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "dock_pill_border",
    )

    val shape = RoundedCornerShape(cornerRadius.coerceAtLeast(0.dp))

    Row(
        modifier = modifier
            .offset(y = offsetY)
            .height(containerHeight.coerceAtLeast(0.dp))
            .scale(scale)
            .then(
                if (isExpanded) {
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.35f),
                        spotColor = Color.Black.copy(alpha = 0.35f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(pillColor)
            .border(width = if (isExpanded) 1.dp else 0.dp, color = borderColor, shape = shape)
            .testTag(app.testTag)
            .semantics { contentDescription = label }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = if (isExpanded) 10.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        DockAppGlyph(app = app, modifier = Modifier.size(iconSize))

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) +
                expandHorizontally(spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
                shrinkHorizontally(spring(stiffness = Spring.StiffnessMedium)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TropicalWhite,
                    maxLines = 1,
                    softWrap = false,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(palette.sunOrange),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun DockAppGlyph(
    app: DockApp,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val s = size.minDimension / 40f

        when (app) {
            DockApp.MUSIC -> {
                drawCircle(color = IconMusicRed, radius = 18f * s, center = Offset(20f * s, 20f * s))
                drawCircle(color = TropicalWhite, radius = 4.2f * s, center = Offset(17.5f * s, 24.5f * s))
                val stem = Path().apply {
                    moveTo(20f * s, 24.5f * s)
                    lineTo(20f * s, 12.2f * s)
                    lineTo(28.5f * s, 10f * s)
                    lineTo(28.5f * s, 14.4f * s)
                    lineTo(22.6f * s, 16f * s)
                    lineTo(22.6f * s, 24.5f * s)
                    close()
                }
                drawPath(stem, color = TropicalWhite)
            }

            DockApp.BROWSER -> {
                drawCircle(color = IconBrowserBlue, radius = 18f * s, center = Offset(20f * s, 20f * s))
                val upperWave = Path().apply {
                    moveTo(9f * s, 19f * s)
                    cubicTo(13f * s, 13f * s, 23f * s, 11f * s, 31f * s, 16f * s)
                    cubicTo(26f * s, 17f * s, 20f * s, 20f * s, 16f * s, 25f * s)
                    cubicTo(13f * s, 28f * s, 11f * s, 29f * s, 9f * s, 19f * s)
                    close()
                }
                drawPath(upperWave, color = TropicalWhite.copy(alpha = 0.92f))

                val lowerWave = Path().apply {
                    moveTo(12f * s, 29f * s)
                    cubicTo(19f * s, 32f * s, 27f * s, 29f * s, 31f * s, 23f * s)
                    cubicTo(27f * s, 24f * s, 21f * s, 22f * s, 17f * s, 20f * s)
                    cubicTo(13f * s, 18f * s, 10f * s, 23f * s, 12f * s, 29f * s)
                    close()
                }
                drawPath(lowerWave, color = IconBrowserLightBlue)
            }

            DockApp.INSTAGRAM -> {
                val igBrush = Brush.linearGradient(
                    colors = listOf(IconIgYellow, IconIgOrange, IconIgPink, IconIgPurple),
                    start = Offset(4f * s, 36f * s),
                    end = Offset(36f * s, 4f * s),
                )
                drawRoundRect(
                    brush = igBrush,
                    topLeft = Offset(4f * s, 4f * s),
                    size = Size(32f * s, 32f * s),
                    cornerRadius = CornerRadius(9f * s, 9f * s),
                )
                drawRoundRect(
                    color = TropicalWhite,
                    topLeft = Offset(10f * s, 10f * s),
                    size = Size(20f * s, 20f * s),
                    cornerRadius = CornerRadius(5.5f * s, 5.5f * s),
                    style = Stroke(width = 2.4f * s),
                )
                drawCircle(
                    color = TropicalWhite,
                    radius = 4.6f * s,
                    center = Offset(20f * s, 20f * s),
                    style = Stroke(width = 2.4f * s),
                )
                drawCircle(
                    color = TropicalWhite,
                    radius = 1.4f * s,
                    center = Offset(26.2f * s, 13.8f * s),
                )
            }

            DockApp.TELEGRAM -> {
                drawCircle(color = IconTelegramBlue, radius = 18f * s, center = Offset(20f * s, 20f * s))
                val plane = Path().apply {
                    moveTo(28.8f * s, 12.4f * s)
                    lineTo(9.8f * s, 19.7f * s)
                    lineTo(15.2f * s, 21.6f * s)
                    lineTo(25.8f * s, 15.0f * s)
                    lineTo(17.5f * s, 22.8f * s)
                    lineTo(17.1f * s, 27.6f * s)
                    lineTo(20.0f * s, 24.9f * s)
                    lineTo(25.2f * s, 28.7f * s)
                    close()
                }
                drawPath(plane, color = TropicalWhite)
            }

            DockApp.WHATSAPP -> {
                drawCircle(color = IconWhatsAppGreen, radius = 18f * s, center = Offset(20f * s, 20f * s))
                val bubble = Path().apply {
                    moveTo(20f * s, 10.5f * s)
                    cubicTo(25.25f * s, 10.5f * s, 29.5f * s, 14.75f * s, 29.5f * s, 20f * s)
                    cubicTo(29.5f * s, 25.25f * s, 25.25f * s, 29.5f * s, 20f * s, 29.5f * s)
                    cubicTo(18.3f * s, 29.5f * s, 16.7f * s, 29.0f * s, 15.4f * s, 28.2f * s)
                    lineTo(10.5f * s, 29.5f * s)
                    lineTo(11.8f * s, 24.8f * s)
                    cubicTo(10.9f * s, 23.4f * s, 10.5f * s, 21.8f * s, 10.5f * s, 20f * s)
                    cubicTo(10.5f * s, 14.75f * s, 14.75f * s, 10.5f * s, 20f * s, 10.5f * s)
                    close()
                }
                drawPath(
                    path = bubble,
                    color = TropicalWhite,
                    style = Stroke(width = 2.3f * s, join = StrokeJoin.Round),
                )
                drawPhoneHandset(s = s, color = TropicalWhite, scaleFactor = 0.62f, offsetX = 7.6f, offsetY = 7.6f)
            }

            DockApp.TWITTER -> {
                val bird = Path().apply {
                    moveTo(33f * s, 13f * s)
                    cubicTo(32f * s, 13.5f * s, 30.9f * s, 13.8f * s, 29.8f * s, 14f * s)
                    cubicTo(31f * s, 13.3f * s, 31.8f * s, 12.2f * s, 32.2f * s, 10.9f * s)
                    cubicTo(31.1f * s, 11.5f * s, 29.9f * s, 12f * s, 28.6f * s, 12.3f * s)
                    cubicTo(27.6f * s, 11.2f * s, 26.1f * s, 10.5f * s, 24.5f * s, 10.5f * s)
                    cubicTo(21.4f * s, 10.5f * s, 18.9f * s, 13f * s, 18.9f * s, 16.1f * s)
                    cubicTo(18.9f * s, 16.5f * s, 18.9f * s, 17f * s, 19f * s, 17.4f * s)
                    cubicTo(14.3f * s, 17.2f * s, 10.2f * s, 14.9f * s, 7.4f * s, 11.5f * s)
                    cubicTo(6.9f * s, 12.3f * s, 6.6f * s, 13.3f * s, 6.6f * s, 14.3f * s)
                    cubicTo(6.6f * s, 16.2f * s, 7.6f * s, 18f * s, 9.1f * s, 19f * s)
                    cubicTo(8.2f * s, 19f * s, 7.3f * s, 18.7f * s, 6.6f * s, 18.3f * s)
                    lineTo(6.6f * s, 18.4f * s)
                    cubicTo(6.6f * s, 21.1f * s, 8.5f * s, 23.4f * s, 11.1f * s, 23.9f * s)
                    cubicTo(10.6f * s, 24f * s, 10.1f * s, 24.1f * s, 9.6f * s, 24.1f * s)
                    cubicTo(9.2f * s, 24.1f * s, 8.9f * s, 24.1f * s, 8.5f * s, 24f * s)
                    cubicTo(9.2f * s, 26.2f * s, 11.3f * s, 27.9f * s, 13.8f * s, 27.9f * s)
                    cubicTo(11.9f * s, 29.4f * s, 9.4f * s, 30.3f * s, 6.8f * s, 30.3f * s)
                    cubicTo(6.3f * s, 30.3f * s, 5.9f * s, 30.3f * s, 5.5f * s, 30.2f * s)
                    cubicTo(8f * s, 31.8f * s, 11f * s, 32.7f * s, 14.1f * s, 32.7f * s)
                    cubicTo(24.5f * s, 32.7f * s, 30.1f * s, 24.1f * s, 30.1f * s, 16.7f * s)
                    lineTo(30.1f * s, 16f * s)
                    cubicTo(31.2f * s, 15.2f * s, 32.2f * s, 14.2f * s, 33f * s, 13f * s)
                    close()
                }
                drawPath(bird, color = IconTwitterCyan)
            }

            DockApp.NOTES -> {
                drawRoundRect(
                    color = IconNotesBack,
                    topLeft = Offset(7f * s, 7f * s),
                    size = Size(24f * s, 26f * s),
                    cornerRadius = CornerRadius(4f * s, 4f * s),
                )
                drawRoundRect(
                    color = IconNotesFront,
                    topLeft = Offset(11f * s, 10f * s),
                    size = Size(22f * s, 24f * s),
                    cornerRadius = CornerRadius(4f * s, 4f * s),
                )
                drawLine(
                    color = TropicalWhite,
                    start = Offset(16f * s, 17f * s),
                    end = Offset(27f * s, 17f * s),
                    strokeWidth = 2.4f * s,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = TropicalWhite,
                    start = Offset(16f * s, 22f * s),
                    end = Offset(27f * s, 22f * s),
                    strokeWidth = 2.4f * s,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = TropicalWhite,
                    start = Offset(16f * s, 27f * s),
                    end = Offset(23f * s, 27f * s),
                    strokeWidth = 2.4f * s,
                    cap = StrokeCap.Round,
                )
            }

            DockApp.PHONE -> {
                drawCircle(color = IconPhoneBg, radius = 18f * s, center = Offset(20f * s, 20f * s))
                drawPhoneHandset(s = s, color = IconPhoneGreen, scaleFactor = 1f, offsetX = 0f, offsetY = 0f)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPhoneHandset(
    s: Float,
    color: Color,
    scaleFactor: Float,
    offsetX: Float,
    offsetY: Float,
) {
    fun tx(x: Float) = (x * scaleFactor + offsetX) * s
    fun ty(y: Float) = (y * scaleFactor + offsetY) * s

    val handset = Path().apply {
        moveTo(tx(13.5f), ty(11.5f))
        cubicTo(tx(14.6f), ty(10.7f), tx(16.0f), ty(11.0f), tx(16.8f), ty(12.1f))
        lineTo(tx(18.5f), ty(14.5f))
        cubicTo(tx(19.2f), ty(15.5f), tx(19.0f), ty(16.8f), tx(18.1f), ty(17.5f))
        lineTo(tx(17.0f), ty(18.4f))
        cubicTo(tx(17.9f), ty(20.2f), tx(19.3f), ty(21.6f), tx(21.1f), ty(22.5f))
        lineTo(tx(22.0f), ty(21.4f))
        cubicTo(tx(22.7f), ty(20.5f), tx(24.0f), ty(20.3f), tx(25.0f), ty(21.0f))
        lineTo(tx(27.4f), ty(22.7f))
        cubicTo(tx(28.5f), ty(23.5f), tx(28.8f), ty(24.9f), tx(28.0f), ty(26.0f))
        lineTo(tx(26.9f), ty(27.4f))
        cubicTo(tx(25.9f), ty(28.6f), tx(24.2f), ty(29.1f), tx(22.6f), ty(28.6f))
        cubicTo(tx(17.8f), ty(27.0f), tx(12.8f), ty(22.0f), tx(11.2f), ty(17.2f))
        cubicTo(tx(10.7f), ty(15.6f), tx(11.2f), ty(13.9f), tx(12.4f), ty(12.9f))
        close()
    }
    drawPath(handset, color = color)
}

@Composable
fun SearchPersonGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.minDimension / 24f
        drawCircle(
            color = TropicalWhite,
            radius = 6.5f * s,
            center = Offset(10.5f * s, 10.5f * s),
            style = Stroke(width = 2.2f * s),
        )
        drawLine(
            color = TropicalWhite,
            start = Offset(15.5f * s, 15.5f * s),
            end = Offset(20f * s, 20f * s),
            strokeWidth = 2.2f * s,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = TropicalWhite,
            radius = 1.8f * s,
            center = Offset(10.5f * s, 9f * s),
        )
        val shoulders = Path().apply {
            moveTo(7.8f * s, 13.5f * s)
            cubicTo(8.4f * s, 12.3f * s, 9.4f * s, 11.7f * s, 10.5f * s, 11.7f * s)
            cubicTo(11.6f * s, 11.7f * s, 12.6f * s, 12.3f * s, 13.2f * s, 13.5f * s)
        }
        drawPath(
            path = shoulders,
            color = TropicalWhite,
            style = Stroke(width = 1.6f * s, cap = StrokeCap.Round),
        )
    }
}
