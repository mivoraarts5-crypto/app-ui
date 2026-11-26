package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.formatDurationMs
import com.example.ui.MusicLayer
import com.example.ui.theme.IconIgPink
import com.example.ui.theme.IconIgPurple
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconNotesBack
import com.example.ui.theme.IconTwitterCyan
import com.example.ui.theme.IconWhatsAppGreen
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalPalette
import com.example.ui.theme.TropicalWhite
import kotlin.math.abs
import kotlin.math.sin

@Composable
private fun MusicAccent.timelineColor(palette: TropicalPalette): Color = when (this) {
    MusicAccent.ORANGE -> palette.sunOrange
    MusicAccent.BLUE -> palette.waveBlue
    MusicAccent.RED -> IconMusicRed
    MusicAccent.GREEN -> IconWhatsAppGreen
    MusicAccent.PURPLE -> IconIgPurple
    MusicAccent.PINK -> IconIgPink
    MusicAccent.CYAN -> IconTwitterCyan
    MusicAccent.AMBER -> IconNotesBack
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
    modifier: Modifier = Modifier,
) {
    if (layers.isEmpty()) return

    val palette = LocalTropicalPalette.current
    val activeLayer = layers.firstOrNull { it.layerId == activeLayerId } ?: layers.first()
    val activeAccent = activeLayer.track.accent.timelineColor(palette)
    val totalTimelineMs = layers.maxOf { it.track.durationMs }.coerceAtLeast(1_000L)
    val progress = (positionMs.toFloat() / totalTimelineMs.toFloat()).coerceIn(0f, 1f)

    val playDesc = stringResource(if (isPlaying) R.string.pause_music_desc else R.string.play_music_desc)
    val removeDesc = stringResource(R.string.remove_music_desc)
    val rulerDesc = stringResource(R.string.time_ruler_desc)
    val layerDesc = stringResource(R.string.music_layer_desc)

    val textMeasurer = rememberTextMeasurer()
    val rulerLabelStyle = TextStyle(
        color = palette.mutedText,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
    )

    val panelShape = RoundedCornerShape(18.dp)

    Surface(
        color = palette.glassDarkSurface,
        shape = panelShape,
        shadowElevation = 6.dp,
        modifier = modifier
            .border(1.dp, palette.glassDarkBorder, panelShape)
            .testTag("music_timeline_panel"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Header row: Layer count + shared time readout + Play/Pause for active layer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Surface(
                        color = activeAccent.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(999.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Layers,
                                contentDescription = null,
                                tint = activeAccent,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = stringResource(R.string.music_layers_count_fmt, layers.size),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = TropicalWhite,
                            )
                        }
                    }

                    Text(
                        text = "${formatDurationMs(positionMs)} / ${formatDurationMs(totalTimelineMs)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = palette.mutedText,
                        modifier = Modifier.testTag("timeline_time_readout"),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(activeAccent)
                        .clickable(role = Role.Button, onClick = onTogglePlay)
                        .semantics { contentDescription = playDesc }
                        .testTag("timeline_play_pause"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = playDesc,
                        tint = TropicalWhite,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            // Single Shared Time Ruler + Multi-Layer Track Stack with one continuous Playhead Needle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    },
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ONE Single Time Ruler pinned at top
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .semantics { contentDescription = rulerDesc }
                            .testTag("music_time_ruler"),
                    ) {
                        val w = size.width
                        val h = size.height
                        val totalTicks = 24
                        val majorEvery = 6

                        drawLine(
                            color = Color.White.copy(alpha = 0.18f),
                            start = Offset(0f, h - 1.dp.toPx()),
                            end = Offset(w, h - 1.dp.toPx()),
                            strokeWidth = 1.dp.toPx(),
                        )

                        for (i in 0..totalTicks) {
                            val frac = i.toFloat() / totalTicks.toFloat()
                            val x = frac * w
                            val isMajor = i % majorEvery == 0
                            val tickHeight = if (isMajor) 7.dp.toPx() else 3.5.dp.toPx()

                            drawLine(
                                color = if (isMajor) Color.White.copy(alpha = 0.58f) else Color.White.copy(alpha = 0.25f),
                                start = Offset(x, h - tickHeight),
                                end = Offset(x, h - 1.dp.toPx()),
                                strokeWidth = if (isMajor) 1.3.dp.toPx() else 1.dp.toPx(),
                            )

                            if (isMajor) {
                                val tickMs = (frac * totalTimelineMs).toLong()
                                val label = formatDurationMs(tickMs)
                                val measured = textMeasurer.measure(text = label, style = rulerLabelStyle)
                                val labelX = when (i) {
                                    0 -> 0f
                                    totalTicks -> (w - measured.size.width).coerceAtLeast(0f)
                                    else -> (x - measured.size.width / 2f).coerceIn(0f, (w - measured.size.width).coerceAtLeast(0f))
                                }
                                drawText(
                                    textLayoutResult = measured,
                                    topLeft = Offset(labelX, 0f),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Multiple stacked Music Layers under the single Time Ruler
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 122.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        layers.forEachIndexed { index, layer ->
                            val isSelected = layer.layerId == activeLayer.layerId
                            val layerAccent = layer.track.accent.timelineColor(palette)
                            val widthRatio = (layer.track.durationMs.toFloat() / totalTimelineMs.toFloat())
                                .coerceIn(0.28f, 1f)
                            val layerProgress = (positionMs.toFloat() / layer.track.durationMs.coerceAtLeast(1L).toFloat())
                                .coerceIn(0f, 1f)

                            val waveformHeights = remember(layer.track.id) {
                                val seed = (layer.track.id.hashCode() and 0x7FFF) / 1000f
                                List(48) { i ->
                                    val wave = abs(sin(i * 0.42f + seed)) * 0.55f +
                                        abs(sin(i * 0.91f + seed * 1.7f)) * 0.30f + 0.15f
                                    wave.coerceIn(0.18f, 1f)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .semantics { contentDescription = layerDesc }
                                    .testTag("music_timeline_layer_${layer.layerId}"),
                            ) {
                                // Clip bar proportional to track duration under the shared ruler
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(widthRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) layerAccent.copy(alpha = 0.24f)
                                            else layerAccent.copy(alpha = 0.12f),
                                        )
                                        .border(
                                            width = if (isSelected) 1.4.dp else 0.9.dp,
                                            color = if (isSelected) layerAccent else layerAccent.copy(alpha = 0.48f),
                                            shape = RoundedCornerShape(10.dp),
                                        )
                                        .clickable(role = Role.Button) { onSelectLayer(layer.layerId) },
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height
                                        val count = waveformHeights.size
                                        val slotW = w / count.toFloat()
                                        val barW = (slotW * 0.52f).coerceAtLeast(1.4.dp.toPx())

                                        waveformHeights.forEachIndexed { idx, amp ->
                                            val barFrac = (idx + 0.5f) / count.toFloat()
                                            val barH = (h * 0.64f * amp).coerceAtLeast(3.5.dp.toPx())
                                            val topY = (h - barH) / 2f
                                            val x = idx * slotW + (slotW - barW) / 2f
                                            val barColor = if (barFrac <= layerProgress) {
                                                layerAccent
                                            } else {
                                                Color.White.copy(alpha = 0.26f)
                                            }
                                            drawRoundRect(
                                                color = barColor,
                                                topLeft = Offset(x, topY),
                                                size = Size(barW, barH),
                                                cornerRadius = CornerRadius(barW / 2f, barW / 2f),
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(layerAccent)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.layer_short_fmt, index + 1),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    ),
                                                    color = TropicalWhite,
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .background(Color.Black.copy(alpha = 0.55f))
                                                    .padding(horizontal = 6.dp, vertical = 1.5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected && isPlaying) {
                                                        Icons.Rounded.GraphicEq
                                                    } else {
                                                        Icons.Rounded.MusicNote
                                                    },
                                                    contentDescription = null,
                                                    tint = layerAccent,
                                                    modifier = Modifier.size(10.dp),
                                                )
                                                Text(
                                                    text = "${layer.track.title} • ${layer.track.duration}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    ),
                                                    color = TropicalWhite,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .clickable(role = Role.Button) { onRemoveLayer(layer.layerId) }
                                                .semantics { contentDescription = removeDesc }
                                                .testTag("timeline_remove_layer_${layer.layerId}"),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = removeDesc,
                                                tint = TropicalWhite,
                                                modifier = Modifier.size(11.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Continuous Playhead Needle spanning the single Ruler + ALL Music Layers
                Canvas(modifier = Modifier.matchParentSize()) {
                    val x = (progress * size.width).coerceIn(3.dp.toPx(), size.width - 3.dp.toPx())
                    drawLine(
                        color = TropicalWhite,
                        start = Offset(x, 12.dp.toPx()),
                        end = Offset(x, size.height),
                        strokeWidth = 2.dp.toPx(),
                    )
                    drawCircle(
                        color = palette.sunOrange,
                        radius = 4.5.dp.toPx(),
                        center = Offset(x, 13.dp.toPx()),
                    )
                    drawCircle(
                        color = TropicalWhite,
                        radius = 2.2.dp.toPx(),
                        center = Offset(x, 13.dp.toPx()),
                    )
                }
            }
        }
    }
}
