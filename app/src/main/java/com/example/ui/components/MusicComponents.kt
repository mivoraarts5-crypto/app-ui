package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.IconIgPink
import com.example.ui.theme.IconIgPurple
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconNotesBack
import com.example.ui.theme.IconTwitterCyan
import com.example.ui.theme.IconWhatsAppGreen
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalPalette
import com.example.ui.theme.TropicalWhite

enum class MusicAccent {
    ORANGE,
    BLUE,
    RED,
    GREEN,
    PURPLE,
    PINK,
    CYAN,
    AMBER,
}

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val durationMs: Long,
    val accent: MusicAccent,
    val baseFreqHz: Float,
    val tempoBpm: Int,
    val contentUriString: String? = null,
    val isFromDevice: Boolean = false,
)

val DefaultMusicLibrary: List<MusicTrack> = listOf(
    MusicTrack("sunset_drift", "Sunset Drift", "Coral Waves", "2:48", 168_000L, MusicAccent.ORANGE, 261.63f, 92),
    MusicTrack("ocean_pulse", "Ocean Pulse", "Blue Horizon", "3:14", 194_000L, MusicAccent.BLUE, 293.66f, 108),
    MusicTrack("palm_breeze", "Palm Breeze", "Tropicana", "2:35", 155_000L, MusicAccent.GREEN, 329.63f, 88),
    MusicTrack("midnight_tide", "Midnight Tide", "Luna Bay", "3:42", 222_000L, MusicAccent.PURPLE, 220.00f, 84),
    MusicTrack("golden_shore", "Golden Shore", "Solstice", "2:56", 176_000L, MusicAccent.AMBER, 349.23f, 100),
    MusicTrack("crimson_sky", "Crimson Sky", "Velvet Sun", "3:08", 188_000L, MusicAccent.RED, 246.94f, 114),
    MusicTrack("neon_lagoon", "Neon Lagoon", "Aura Synth", "2:24", 144_000L, MusicAccent.CYAN, 392.00f, 120),
    MusicTrack("island_lofi", "Island Lo-Fi", "Coco Beats", "2:19", 139_000L, MusicAccent.PINK, 277.18f, 78),
    MusicTrack("starlight_surf", "Starlight Surf", "Mariner", "3:30", 210_000L, MusicAccent.BLUE, 311.13f, 96),
    MusicTrack("mango_groove", "Mango Groove", "Sunkissed", "2:41", 161_000L, MusicAccent.ORANGE, 369.99f, 110),
    MusicTrack("monsoon_rain", "Monsoon Rain", "Driftwood", "4:05", 245_000L, MusicAccent.CYAN, 196.00f, 74),
    MusicTrack("electric_reef", "Electric Reef", "Tidal Club", "3:12", 192_000L, MusicAccent.PURPLE, 440.00f, 124),
)

@Composable
private fun MusicAccent.resolveColor(palette: TropicalPalette): Color = when (this) {
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
fun MusicPickerTray(
    tracks: List<MusicTrack>,
    addedTrackId: String?,
    onSelectTrack: (MusicTrack) -> Unit,
    onPickFromDevice: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val trayShape = RoundedCornerShape(20.dp)
    val closeDesc = stringResource(R.string.close_music_picker_desc)
    val pickDeviceDesc = stringResource(R.string.pick_from_device_desc)

    Surface(
        color = palette.glassDarkSurface,
        shape = trayShape,
        shadowElevation = 8.dp,
        modifier = modifier
            .border(1.dp, palette.glassDarkBorder, trayShape)
            .testTag("music_picker_tray"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(IconMusicRed),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = TropicalWhite,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    Text(
                        text = stringResource(R.string.music_library_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        ),
                        color = TropicalWhite,
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(999.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.music_tracks_count_fmt, tracks.size),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = palette.mutedText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Surface(
                        color = palette.searchButtonBlue,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable(role = Role.Button, onClick = onPickFromDevice)
                            .semantics { contentDescription = pickDeviceDesc }
                            .testTag("pick_device_music_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FolderOpen,
                                contentDescription = null,
                                tint = TropicalWhite,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = stringResource(R.string.pick_from_device),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = TropicalWhite,
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_music_picker_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = closeDesc,
                            tint = TropicalWhite,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (addedTrackId != null) 118.dp else 160.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(tracks, key = { it.id }) { track ->
                    val isAdded = track.id == addedTrackId
                    val accentColor = track.accent.resolveColor(palette)
                    val rowBg by animateColorAsState(
                        targetValue = if (isAdded) {
                            accentColor.copy(alpha = 0.22f)
                        } else {
                            Color.White.copy(alpha = 0.06f)
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "track_row_bg",
                    )
                    val rowBorder by animateColorAsState(
                        targetValue = if (isAdded) accentColor else Color.White.copy(alpha = 0.08f),
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "track_row_border",
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(rowBg)
                            .border(1.dp, rowBorder, RoundedCornerShape(14.dp))
                            .clickable(role = Role.Button) { onSelectTrack(track) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("music_track_${track.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isAdded) Icons.Rounded.GraphicEq else Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = TropicalWhite,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = TropicalWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false),
                                )
                                if (track.isFromDevice) {
                                    Surface(
                                        color = palette.searchButtonBlue.copy(alpha = 0.28f),
                                        shape = RoundedCornerShape(999.dp),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.device_track_badge),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            color = TropicalWhite,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${track.artist} • ${track.duration}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = palette.mutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Surface(
                            color = if (isAdded) accentColor else Color.White.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(999.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = if (isAdded) Icons.Rounded.Check else Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = TropicalWhite,
                                    modifier = Modifier.size(13.dp),
                                )
                                Text(
                                    text = stringResource(
                                        if (isAdded) R.string.added_music_label else R.string.add_music_label,
                                    ),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = TropicalWhite,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CanvasMusicSticker(
    track: MusicTrack,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onRemove: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val accentColor = track.accent.resolveColor(palette)
    val playDesc = stringResource(if (isPlaying) R.string.pause_music_desc else R.string.play_music_desc)
    val removeDesc = stringResource(R.string.remove_music_desc)

    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "disc_angle",
    )
    val pulseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "sticker_pulse",
    )

    val cardShape = RoundedCornerShape(18.dp)

    Surface(
        color = palette.glassDarkSurface,
        shape = cardShape,
        modifier = modifier
            .widthIn(min = 190.dp, max = 260.dp)
            .scale(pulseScale)
            .shadow(10.dp, cardShape, ambientColor = Color.Black.copy(alpha = 0.4f))
            .border(1.2.dp, accentColor.copy(alpha = 0.85f), cardShape)
            .pointerInput(track.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
            .testTag("canvas_music_sticker"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .rotate(if (isPlaying) spinAngle else 0f)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.GraphicEq else Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = TropicalWhite,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TropicalWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${track.artist} • ${track.duration}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = palette.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .semantics { contentDescription = playDesc }
                    .clickable(role = Role.Button, onClick = onTogglePlay)
                    .testTag("canvas_music_play_pause"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = playDesc,
                    tint = TropicalWhite,
                    modifier = Modifier.size(18.dp),
                )
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .semantics { contentDescription = removeDesc }
                    .clickable(role = Role.Button, onClick = onRemove)
                    .testTag("canvas_music_remove"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = removeDesc,
                    tint = TropicalWhite,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
