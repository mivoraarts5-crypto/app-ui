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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
    track: MusicTrack,
    positionMs: Long,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSeekToMs: (Long) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val accentColor = track.accent.timelineColor(palette)
    val durationMs = track.durationMs.coerceAtLeast(1_000L)
    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    val playDesc = stringResource(if (isPlaying) R.string.pause_music_desc else R.string.play_music_desc)
    val removeDesc = stringResource(R.string.remove_music_desc)
    val rulerDesc = stringResource(R.string.time_ruler_desc)
    val layerDesc = stringResource(R.string.music_layer_desc)

    val waveformHeights = remember(track.id) {
        val seed = (track.id.hashCode() and 0x7FFF) / 1000f
        List(56) { i ->
            val wave = abs(sin(i * 0.42f + seed)) * 0.55f +
                abs(sin(i * 0.91f + seed * 1.7f)) * 0.30f + 0.15f
            wave.coerceIn(0.18f, 1f)
        }
    }

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
            // Top bar: Layer indicator + Time readout + Mini controls
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
                        color = accentColor.copy(alpha = 0.22f),
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
                                tint = accentColor,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = stringResource(R.string.music_layer_label),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = TropicalWhite,
                            )
                        }
                    }

                    Text(
                        text = "${formatDurationMs(positionMs)} / ${formatDurationMs(durationMs)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = palette.mutedText,
                        modifier = Modifier.testTag("timeline_time_readout"),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                            .clickable(role = Role.Button, onClick = onTogglePlay)
                            .semantics { contentDescription = playDesc }
                            .testTag("timeline_play_pause"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = playDesc,
                            tint = TropicalWhite,
                            modifier = Modifier.size(14.dp),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable(role = Role.Button, onClick = onRemove)
                            .semantics { contentDescription = removeDesc }
                            .testTag("timeline_remove_music"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = removeDesc,
                            tint = TropicalWhite,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }

            // Combined Time Ruler + Scrubbable Music Layer Track with Playhead
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .pointerInput(track.id, durationMs) {
                        detectTapGestures { offset ->
                            val frac = (offset.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                            onSeekToMs((frac * durationMs).toLong())
                        }
                    }
                    .pointerInput(track.id, durationMs) {
                        detectHorizontalDragGestures { change, _ ->
                            change.consume()
                            val frac = (change.position.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                            onSeekToMs((frac * durationMs).toLong())
                        }
                    },
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Time Ruler Row
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

                        // Baseline
                        drawLine(
                            color = Color.White.copy(alpha = 0.16f),
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
                                color = if (isMajor) Color.White.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.25f),
                                start = Offset(x, h - tickHeight),
                                end = Offset(x, h - 1.dp.toPx()),
                                strokeWidth = if (isMajor) 1.3.dp.toPx() else 1.dp.toPx(),
                            )

                            if (isMajor) {
                                val tickMs = (frac * durationMs).toLong()
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

                    Spacer(modifier = Modifier.height(3.dp))

                    // Music Layer Track Bar (Clip + Waveform)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(37.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.18f))
                            .border(1.dp, accentColor.copy(alpha = 0.72f), RoundedCornerShape(10.dp))
                            .semantics { contentDescription = layerDesc }
                            .testTag("music_timeline_layer"),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val count = waveformHeights.size
                            val slotW = w / count.toFloat()
                            val barW = (slotW * 0.54f).coerceAtLeast(1.5.dp.toPx())

                            waveformHeights.forEachIndexed { idx, amp ->
                                val barFrac = (idx + 0.5f) / count.toFloat()
                                val barH = (h * 0.68f * amp).coerceAtLeast(4.dp.toPx())
                                val topY = (h - barH) / 2f
                                val x = idx * slotW + (slotW - barW) / 2f
                                val barColor = if (barFrac <= progress) {
                                    accentColor
                                } else {
                                    Color.White.copy(alpha = 0.30f)
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
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.Black.copy(alpha = 0.52f))
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(11.dp),
                            )
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = TropicalWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                // Continuous Playhead Needle spanning Ruler + Music Layer
                Canvas(modifier = Modifier.fillMaxSize()) {
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
