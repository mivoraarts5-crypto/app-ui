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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.ui.theme.IconIgPurple
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconNotesBack
import com.example.ui.theme.IconTwitterCyan
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalPalette
import com.example.ui.theme.TropicalWhite
import com.example.ui.theme.hard2dShadow
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
private fun MusicAccent.timelineColor(palette: TropicalPalette): Color = when (this) {
    MusicAccent.ORANGE -> palette.sunOrange
    MusicAccent.BLUE -> palette.waveBlue
    MusicAccent.RED -> IconMusicRed
    MusicAccent.GREEN -> palette.acidLime
    MusicAccent.PURPLE -> IconIgPurple
    MusicAccent.PINK -> IconIgPink
    MusicAccent.CYAN -> IconTwitterCyan
    MusicAccent.AMBER -> IconNotesBack
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
    val progress = (positionMs.toFloat() / totalTimelineMs.toFloat()).coerceIn(0f, 1f)

    val playDesc = stringResource(if (isPlaying) R.string.pause_music_desc else R.string.play_music_desc)
    val removeDesc = stringResource(R.string.remove_music_desc)
    val rulerDesc = stringResource(R.string.time_ruler_desc)
    val layerDesc = stringResource(R.string.music_layer_desc)
    val visibilityDesc = stringResource(R.string.toggle_layer_visibility_desc)

    val panelShape = RoundedCornerShape(18.dp)
    val leftRailWidth = 74.dp
    val railGap = 8.dp
    val rulerHeight = 36.dp

    Box(
        modifier = modifier
            .padding(end = 3.dp, bottom = 3.dp)
            .hard2dShadow(
                shape = panelShape,
                shadowColor = palette.hardShadowDark,
                offsetX = 4.dp,
                offsetY = 4.dp,
            )
            .clip(panelShape)
            .background(palette.glassDarkSurface)
            .border(1.8.dp, palette.glassDarkBorder, panelShape)
            .testTag("music_timeline_panel"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 8.dp, end = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // --- TOP HEADER ROW: Left Play/Layer Badge + Right High-Density Time Ruler ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rulerHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left control header (Play/Pause + Layers count)
                    Row(
                        modifier = Modifier
                            .width(leftRailWidth)
                            .padding(start = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .hard2dShadow(CircleShape, palette.hardShadowDeep, 2.dp, 2.dp)
                                .clip(CircleShape)
                                .background(palette.acidLime)
                                .border(1.4.dp, palette.inkBlack, CircleShape)
                                .clickable(role = Role.Button, onClick = onTogglePlay)
                                .semantics { contentDescription = playDesc }
                                .testTag("timeline_play_pause"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = playDesc,
                                tint = palette.inkBlack,
                                modifier = Modifier.size(16.dp),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(palette.graphicElevated)
                                .border(1.1.dp, palette.glassDarkBorder, RoundedCornerShape(7.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = "${layers.size}L",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                ),
                                color = palette.acidLime,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(railGap))

                    // Right Pinned Time Ruler (High-density Alight-style top ticks, tap or drag to seek)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .pointerInput(layers.size, totalTimelineMs) {
                                detectTapGestures { offset ->
                                    val frac = (offset.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                                    onSeekToMs((frac * totalTimelineMs).toLong())
                                }
                            }
                            .pointerInput(layers.size, totalTimelineMs) {
                                detectHorizontalDragGestures { change, _ ->
                                    change.consume()
                                    val frac = (change.position.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                                    onSeekToMs((frac * totalTimelineMs).toLong())
                                }
                            }
                            .semantics { contentDescription = rulerDesc }
                            .testTag("music_time_ruler"),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val totalTicks = 60
                            for (i in 0..totalTicks) {
                                val x = (i.toFloat() / totalTicks.toFloat()) * w
                                val isMajor = i % 10 == 0
                                val isMedium = i % 5 == 0 && !isMajor
                                val tickH = when {
                                    isMajor -> 13.dp.toPx()
                                    isMedium -> 8.dp.toPx()
                                    else -> 4.5.dp.toPx()
                                }
                                drawLine(
                                    color = when {
                                        isMajor -> palette.mutedText.copy(alpha = 0.85f)
                                        isMedium -> palette.mutedText.copy(alpha = 0.55f)
                                        else -> palette.glassDarkBorder
                                    },
                                    start = Offset(x, 0f),
                                    end = Offset(x, tickH),
                                    strokeWidth = if (isMajor) 1.3.dp.toPx() else 0.9.dp.toPx(),
                                    cap = StrokeCap.Butt,
                                )
                            }
                        }
                    }
                }

                // --- SCROLLABLE LAYER ROWS (Left Eye-Icon Pill + Right Striped Track Clip Bar) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 148.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    layers.forEachIndexed { index, layer ->
                        val isSelected = layer.layerId == activeLayer.layerId
                        val layerAccent = layer.track.accent.timelineColor(palette)
                        val darkInkOnBar = layer.track.accent.usesDarkInk()
                        val barContentColor = if (darkInkOnBar) palette.inkBlack else TropicalWhite
                        val widthRatio = (layer.track.durationMs.toFloat() / totalTimelineMs.toFloat())
                            .coerceIn(0.42f, 1f)
                        val layerAlpha = if (layer.isVisible) 1f else 0.45f

                        val waveformHeights = remember(layer.track.id) {
                            val seed = (layer.track.id.hashCode() and 0x7FFF) / 1000f
                            List(36) { i ->
                                val wave = abs(sin(i * 0.45f + seed)) * 0.55f +
                                    abs(sin(i * 0.92f + seed * 1.6f)) * 0.30f + 0.15f
                                wave.coerceIn(0.2f, 1f)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .semantics { contentDescription = layerDesc }
                                .testTag("music_timeline_layer_${layer.layerId}"),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Left Alight-Motion Style Rounded-Right Control Pill (Eye Toggle + Audio Icon)
                            val pillShape = RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp)
                            Row(
                                modifier = Modifier
                                    .width(leftRailWidth)
                                    .fillMaxHeight()
                                    .clip(pillShape)
                                    .background(if (isSelected) palette.graphicElevated else palette.islandCharcoal)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.2.dp,
                                        color = if (isSelected) TropicalWhite else palette.glassDarkBorder,
                                        shape = pillShape,
                                    )
                                    .padding(start = 8.dp, end = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
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
                                            Icons.Rounded.Visibility
                                        } else {
                                            Icons.Rounded.VisibilityOff
                                        },
                                        contentDescription = visibilityDesc,
                                        tint = if (layer.isVisible) TropicalWhite else palette.mutedText,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(layerAccent.copy(alpha = 0.22f))
                                        .border(1.dp, layerAccent.copy(alpha = 0.8f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = if (isSelected && isPlaying) {
                                            Icons.Rounded.GraphicEq
                                        } else {
                                            Icons.Rounded.MusicNote
                                        },
                                        contentDescription = null,
                                        tint = layerAccent,
                                        modifier = Modifier.size(13.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(railGap))

                            // Right Track Clip Bar (Solid 2D Accent + Vertical Zebra Stripes + Label + Handle + Remove)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                val clipShape = RoundedCornerShape(7.dp)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(widthRatio)
                                        .fillMaxHeight()
                                        .alpha(layerAlpha)
                                        .clip(clipShape)
                                        .background(layerAccent)
                                        .border(
                                            width = if (isSelected) 1.6.dp else 1.2.dp,
                                            color = if (isSelected) TropicalWhite else palette.inkBlack,
                                            shape = clipShape,
                                        )
                                        .clickable(role = Role.Button) { onSelectLayer(layer.layerId) },
                                ) {
                                    // Vertical Zebra Stripes + Subtle 2D Waveform along bottom
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height
                                        val stripeStep = 10.dp.toPx()
                                        val stripeWidth = 5.dp.toPx()
                                        var sx = 4.dp.toPx()
                                        while (sx < w) {
                                            drawRect(
                                                color = Color.White.copy(alpha = 0.14f),
                                                topLeft = Offset(sx, 0f),
                                                size = Size(stripeWidth, h),
                                            )
                                            sx += stripeStep
                                        }

                                        // Subtle bottom waveform edge
                                        val count = waveformHeights.size
                                        val slotW = w / count.toFloat()
                                        val barW = (slotW * 0.45f).coerceAtLeast(1.5.dp.toPx())
                                        waveformHeights.forEachIndexed { idx, amp ->
                                            val bh = (h * 0.24f * amp).coerceAtLeast(2.dp.toPx())
                                            drawRect(
                                                color = barContentColor.copy(alpha = 0.22f),
                                                topLeft = Offset(idx * slotW, h - bh),
                                                size = Size(barW, bh),
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.layer_short_fmt, index + 1),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                ),
                                                color = barContentColor.copy(alpha = 0.72f),
                                            )
                                            Text(
                                                text = layer.track.title,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                                color = barContentColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(palette.inkBlack.copy(alpha = 0.22f))
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
                                                    tint = barContentColor,
                                                    modifier = Modifier.size(11.dp),
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Rounded.DragHandle,
                                                contentDescription = null,
                                                tint = barContentColor.copy(alpha = 0.72f),
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- CONTINUOUS PLAYHEAD LINE + FLOATING TIMECODE PILL OVER RIGHT TIMELINE AREA ---
            Row(modifier = Modifier.matchParentSize()) {
                Spacer(modifier = Modifier.width(leftRailWidth + railGap))
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    val density = LocalDensity.current
                    val trackWidthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
                    val playheadX = (progress * trackWidthPx).coerceIn(
                        with(density) { 2.dp.toPx() },
                        trackWidthPx - with(density) { 2.dp.toPx() },
                    )
                    val pillWidthDp = 64.dp
                    val pillWidthPx = with(density) { pillWidthDp.toPx() }
                    val pillOffsetX = (playheadX - pillWidthPx / 2f)
                        .coerceIn(0f, (trackWidthPx - pillWidthPx).coerceAtLeast(0f))
                        .roundToInt()

                    // Vertical playhead needle from top of ruler straight down across every layer
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = palette.inkBlack,
                            start = Offset(playheadX, 0f),
                            end = Offset(playheadX, size.height),
                            strokeWidth = 3.5.dp.toPx(),
                        )
                        drawLine(
                            color = TropicalWhite,
                            start = Offset(playheadX, 0f),
                            end = Offset(playheadX, size.height),
                            strokeWidth = 1.8.dp.toPx(),
                        )
                    }

                    // Floating Alight-Motion Style Timecode Pill ("00:00:25") riding the playhead
                    val timecodeShape = RoundedCornerShape(5.dp)
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(x = pillOffsetX, y = with(density) { 15.dp.toPx() }.roundToInt()) }
                            .width(pillWidthDp)
                            .height(19.dp)
                            .hard2dShadow(timecodeShape, palette.inkBlack, 1.5.dp, 1.5.dp)
                            .clip(timecodeShape)
                            .background(palette.graphicElevated)
                            .border(1.2.dp, palette.glassDarkBorder, timecodeShape)
                            .testTag("timeline_time_readout"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = formatTimecode(positionMs),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp,
                            ),
                            color = TropicalWhite,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
