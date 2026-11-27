package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MusicLayer
import com.example.ui.theme.IconIgPink
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconTwitterCyan
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalPalette
import com.example.ui.theme.TropicalWhite
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
private fun MusicAccent.timelineColor(palette: TropicalPalette): Color = when (this) {
    MusicAccent.ORANGE -> palette.sunOrange
    MusicAccent.BLUE -> palette.timelineTrackBlue
    MusicAccent.RED -> IconMusicRed
    MusicAccent.GREEN -> palette.acidLime
    MusicAccent.PURPLE -> palette.timelineTrackPurple
    MusicAccent.PINK -> IconIgPink
    MusicAccent.CYAN -> IconTwitterCyan
    MusicAccent.AMBER -> palette.timelineTrackAmber
}

private fun MusicAccent.usesDarkInk(): Boolean = when (this) {
    MusicAccent.GREEN, MusicAccent.AMBER, MusicAccent.ORANGE, MusicAccent.CYAN -> true
    MusicAccent.BLUE, MusicAccent.RED, MusicAccent.PURPLE, MusicAccent.PINK -> false
}

private fun formatTimecode(ms: Long): String {
    val safe = ms.coerceAtLeast(0L)
    val totalSeconds = safe / 1000L
    val mins = totalSeconds / 60L
    val secs = totalSeconds % 60L
    val frames = ((safe % 1000L) / 33L).coerceIn(0L, 29L)
    return String.format(Locale.US, "%02d:%02d:%02d", mins, secs, frames)
}

@Composable
fun MusicTimelinePanel(
    layers: List<MusicLayer>,
    activeLayerId: Long?,
    positionMs: Long,
    isPlaying: Boolean,
    onSelectLayer: (Long) -> Unit,
    onTogglePlay: () -> Unit,
    onSeekToMs: (Long) -> Unit,
    onRemoveLayer: (Long) -> Unit,
    onToggleLayerVisibility: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (layers.isEmpty()) return

    val palette = LocalTropicalPalette.current
    val activeLayer = layers.firstOrNull { it.layerId == activeLayerId } ?: layers.first()
    val totalTimelineMs = layers.maxOf { it.track.durationMs }.coerceAtLeast(1_000L)

    val currentPosMs by rememberUpdatedState(positionMs)
    val currentTotalMs by rememberUpdatedState(totalTimelineMs)
    val currentOnSeek by rememberUpdatedState(onSeekToMs)

    val playDesc = stringResource(if (isPlaying) R.string.pause_music_desc else R.string.play_music_desc)
    val removeDesc = stringResource(R.string.remove_music_desc)
    val rulerDesc = stringResource(R.string.time_ruler_desc)
    val layerDesc = stringResource(R.string.music_layer_desc)
    val visibilityDesc = stringResource(R.string.toggle_layer_visibility_desc)

    val density = LocalDensity.current
    val pxPerSecond = with(density) { 48.dp.toPx() }
    val pxPerMs = pxPerSecond / 1000f

    val leftRailWidth = 64.dp
    val railGap = 8.dp
    val rulerHeight = 40.dp
    val rowHeight = 22.dp
    val rowSpacing = 2.5.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.timelinePanelBg)
            .testTag("music_timeline_panel"),
    ) {
        // Subtle 1dp hairline top border separating canvas & timeline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.glassDarkBorder.copy(alpha = 0.7f)),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // --- HEADER ROW: Left Play Button + Right Alight-Style Centered Playhead Ruler ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rulerHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Clean, understated left corner play/pause control
                    Box(
                        modifier = Modifier
                            .width(leftRailWidth)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape)
                                .background(palette.timelinePillBg)
                                .border(1.2.dp, palette.glassDarkBorder, CircleShape)
                                .clickable(role = Role.Button, onClick = onTogglePlay)
                                .semantics { contentDescription = playDesc }
                                .testTag("timeline_play_pause"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = playDesc,
                                tint = if (isPlaying) palette.acidLime else TropicalWhite,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(railGap))

                    // Right Timeline Ruler Viewport (Playhead fixed dead-center; ruler ticks scroll underneath)
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clipToBounds()
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val centerX = size.width / 2f
                                    val targetMs = (currentPosMs + ((offset.x - centerX) / pxPerMs).toLong())
                                        .coerceIn(0L, currentTotalMs)
                                    currentOnSeek(targetMs)
                                }
                            }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaMs = (-dragAmount / pxPerMs).toLong()
                                    val targetMs = (currentPosMs + deltaMs).coerceIn(0L, currentTotalMs)
                                    currentOnSeek(targetMs)
                                }
                            }
                            .semantics { contentDescription = rulerDesc }
                            .testTag("music_time_ruler"),
                    ) {
                        val centerXPx = with(density) { (maxWidth / 2f).toPx() }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val centerX = w / 2f
                            val topOffset = 4.dp.toPx()

                            val visibleLeftMs = floor((positionMs - centerX / pxPerMs) / 100f).toLong() * 100L
                            val visibleRightMs = ceil((positionMs + (w - centerX) / pxPerMs) / 100f).toLong() * 100L

                            var tickMs = visibleLeftMs.coerceAtLeast(0L)
                            while (tickMs <= visibleRightMs && tickMs <= totalTimelineMs + 60_000L) {
                                val x = centerX + (tickMs - positionMs) * pxPerMs
                                if (x in -2f..(w + 2f)) {
                                    val isMajor = tickMs % 1000L == 0L
                                    val isMedium = !isMajor && tickMs % 500L == 0L
                                    val tickH = when {
                                        isMajor -> 11.dp.toPx()
                                        isMedium -> 6.5.dp.toPx()
                                        else -> 3.8.dp.toPx()
                                    }
                                    drawLine(
                                        color = if (isMajor) {
                                            palette.timelineTickColor.copy(alpha = 0.95f)
                                        } else {
                                            palette.timelineTickColor.copy(alpha = 0.65f)
                                        },
                                        start = Offset(x, topOffset),
                                        end = Offset(x, topOffset + tickH),
                                        strokeWidth = if (isMajor) 1.1.dp.toPx() else 0.85.dp.toPx(),
                                    )
                                }
                                tickMs += 100L
                            }

                            // Crisp white vertical playhead line drawn BEHIND the floating timecode pill
                            drawLine(
                                color = TropicalWhite,
                                start = Offset(centerX, topOffset - 1.dp.toPx()),
                                end = Offset(centerX, size.height),
                                strokeWidth = 1.6.dp.toPx(),
                            )
                        }

                        // Floating chamfered timecode pill ("00:00:25") centered right on top of the playhead line
                        val pillWidthDp = 66.dp
                        val pillWidthPx = with(density) { pillWidthDp.toPx() }
                        val pillTopPx = with(density) { 17.dp.toPx() }.roundToInt()
                        val timecodeShape = RoundedCornerShape(
                            topStart = 6.dp,
                            topEnd = 6.dp,
                            bottomStart = 3.dp,
                            bottomEnd = 3.dp,
                        )
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = (centerXPx - pillWidthPx / 2f).roundToInt(),
                                        y = pillTopPx,
                                    )
                                }
                                .width(pillWidthDp)
                                .height(18.dp)
                                .clip(timecodeShape)
                                .background(palette.timelineTimecodeBg)
                                .testTag("timeline_time_readout"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = formatTimecode(positionMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.35.sp,
                                ),
                                color = TropicalWhite,
                                maxLines = 1,
                            )
                        }
                    }
                }

                // --- LAYER STACK: Synchronized Left Pills + Right Scrolling Striped Track Bars + Center Playhead ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 198.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(rowSpacing),
                    ) {
                        layers.forEach { layer ->
                            val isSelected = layer.layerId == activeLayer.layerId
                            val layerAccent = layer.track.accent.timelineColor(palette)
                            val darkInkOnBar = layer.track.accent.usesDarkInk()
                            val barContentColor = if (darkInkOnBar) palette.inkBlack else TropicalWhite
                            val layerAlpha = if (layer.isVisible) 1f else 0.4f

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeight)
                                    .semantics { contentDescription = layerDesc }
                                    .testTag("music_timeline_layer_${layer.layerId}"),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Left Alight-Motion Pill flush against left edge
                                val pillShape = RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp)
                                Row(
                                    modifier = Modifier
                                        .width(leftRailWidth)
                                        .fillMaxHeight()
                                        .clip(pillShape)
                                        .background(
                                            if (isSelected) palette.timelinePillSelectedBg else palette.timelinePillBg,
                                        )
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = TropicalWhite.copy(alpha = 0.35f),
                                                    shape = pillShape,
                                                )
                                            } else {
                                                Modifier
                                            },
                                        )
                                        .padding(start = 8.dp, end = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .clickable(role = Role.Button) {
                                                onToggleLayerVisibility(layer.layerId)
                                            }
                                            .semantics { contentDescription = visibilityDesc }
                                            .testTag("timeline_toggle_visibility_${layer.layerId}"),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = if (layer.isVisible) {
                                                Icons.Outlined.Visibility
                                            } else {
                                                Icons.Outlined.VisibilityOff
                                            },
                                            contentDescription = visibilityDesc,
                                            tint = if (layer.isVisible) {
                                                TropicalWhite.copy(alpha = 0.92f)
                                            } else {
                                                palette.mutedText.copy(alpha = 0.5f)
                                            },
                                            modifier = Modifier.size(13.5.dp),
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isSelected && isPlaying) {
                                            Icons.Rounded.GraphicEq
                                        } else {
                                            Icons.Outlined.MusicNote
                                        },
                                        contentDescription = null,
                                        tint = if (layer.isVisible) {
                                            if (isSelected) layerAccent else TropicalWhite.copy(alpha = 0.85f)
                                        } else {
                                            palette.mutedText.copy(alpha = 0.45f)
                                        },
                                        modifier = Modifier.size(13.dp),
                                    )
                                }

                                Spacer(modifier = Modifier.width(railGap))

                                // Right Track Clip Bar Viewport (Aligned to the Center Playhead!)
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clipToBounds()
                                        .pointerInput(Unit) {
                                            detectHorizontalDragGestures { change, dragAmount ->
                                                change.consume()
                                                val deltaMs = (-dragAmount / pxPerMs).toLong()
                                                val targetMs = (currentPosMs + deltaMs).coerceIn(0L, currentTotalMs)
                                                currentOnSeek(targetMs)
                                            }
                                        },
                                ) {
                                    val viewportW = with(density) { maxWidth.toPx() }
                                    val centerX = viewportW / 2f
                                    val rawStartPx = centerX + (0L - positionMs) * pxPerMs
                                    val rawEndPx = centerX + (layer.track.durationMs - positionMs) * pxPerMs

                                    val visibleLeftPx = rawStartPx.coerceIn(0f, viewportW)
                                    val visibleRightPx = rawEndPx.coerceIn(0f, viewportW)
                                    val visibleWidthPx = (visibleRightPx - visibleLeftPx).coerceAtLeast(0f)

                                    if (visibleWidthPx > 2f) {
                                        val visibleWidthDp = with(density) { visibleWidthPx.toDp() }
                                        val cornerLeft = if (rawStartPx >= 0f) 4.dp else 0.dp
                                        val cornerRight = if (rawEndPx <= viewportW) 4.dp else 0.dp
                                        val barShape = RoundedCornerShape(
                                            topStart = cornerLeft,
                                            bottomStart = cornerLeft,
                                            topEnd = cornerRight,
                                            bottomEnd = cornerRight,
                                        )

                                        Box(
                                            modifier = Modifier
                                                .offset { IntOffset(x = visibleLeftPx.roundToInt(), y = 0) }
                                                .width(visibleWidthDp)
                                                .fillMaxHeight()
                                                .alpha(layerAlpha)
                                                .clip(barShape)
                                                .background(layerAccent)
                                                .then(
                                                    if (isSelected) {
                                                        Modifier.border(
                                                            width = 1.dp,
                                                            color = TropicalWhite.copy(alpha = 0.8f),
                                                            shape = barShape,
                                                        )
                                                    } else {
                                                        Modifier
                                                    },
                                                )
                                                .clickable(role = Role.Button) { onSelectLayer(layer.layerId) },
                                        ) {
                                            // Alight-Motion Alternating Vertical Zebra Stripes + Subtle Centerline
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val stripeW = 6.dp.toPx()
                                                val stripePeriod = stripeW * 2f
                                                val worldOffset = visibleLeftPx - rawStartPx
                                                val firstIdx = floor(worldOffset / stripePeriod).toInt()
                                                val lastIdx = ceil((worldOffset + size.width) / stripePeriod).toInt()

                                                clipRect {
                                                    for (k in firstIdx..lastIdx) {
                                                        val sx = k * stripePeriod - worldOffset
                                                        drawRect(
                                                            color = Color.White.copy(alpha = 0.11f),
                                                            topLeft = Offset(sx, 0f),
                                                            size = Size(stripeW, size.height),
                                                        )
                                                    }
                                                    // Thin audio clip centerline
                                                    drawLine(
                                                        color = barContentColor.copy(alpha = 0.18f),
                                                        start = Offset(0f, size.height / 2f),
                                                        end = Offset(size.width, size.height / 2f),
                                                        strokeWidth = 0.8.dp.toPx(),
                                                    )
                                                }
                                            }

                                            // Track Title + Subtle Right Actions (× + ≡ handle)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = layer.track.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 10.5.sp,
                                                        lineHeight = 12.sp,
                                                        fontWeight = FontWeight.Medium,
                                                    ),
                                                    color = barContentColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .weight(1f, fill = false)
                                                        .padding(end = 4.dp),
                                                )

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .clickable(role = Role.Button) {
                                                                onRemoveLayer(layer.layerId)
                                                            }
                                                            .semantics { contentDescription = removeDesc }
                                                            .testTag("timeline_remove_layer_${layer.layerId}"),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Close,
                                                            contentDescription = removeDesc,
                                                            tint = barContentColor.copy(alpha = 0.65f),
                                                            modifier = Modifier.size(10.5.dp),
                                                        )
                                                    }

                                                    // Clean 3-bar Alight-style handle (≡)
                                                    Canvas(modifier = Modifier.size(width = 9.dp, height = 7.dp)) {
                                                        val lineColor = barContentColor.copy(alpha = 0.62f)
                                                        val stroke = 1.1.dp.toPx()
                                                        listOf(0.15f, 0.5f, 0.85f).forEach { yFrac ->
                                                            val y = size.height * yFrac
                                                            drawLine(
                                                                color = lineColor,
                                                                start = Offset(0f, y),
                                                                end = Offset(size.width, y),
                                                                strokeWidth = stroke,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Continuous White Playhead Line ON TOP of all layer track bars (aligned strictly to center of right area)
                    Row(modifier = Modifier.matchParentSize()) {
                        Spacer(modifier = Modifier.width(leftRailWidth + railGap))
                        Canvas(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            val centerX = size.width / 2f
                            drawLine(
                                color = TropicalWhite,
                                start = Offset(centerX, 0f),
                                end = Offset(centerX, size.height),
                                strokeWidth = 1.6.dp.toPx(),
                            )
                        }
                    }
                }
            }
        }

        // Subtle 1dp hairline bottom border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.glassDarkBorder.copy(alpha = 0.7f)),
        )
    }
}
