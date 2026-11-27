package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
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
import com.example.ui.WallpaperPreset
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite
import com.example.ui.theme.hard2dShadow

@Composable
fun WallpaperPickerTray(
    selectedPreset: WallpaperPreset,
    customWallpaperBitmap: ImageBitmap?,
    onSelectPreset: (WallpaperPreset) -> Unit,
    onPickCustomPhoto: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val trayShape = RoundedCornerShape(20.dp)
    val closeDesc = stringResource(R.string.close_wallpaper_picker_desc)
    val photoDesc = stringResource(R.string.pick_wallpaper_photo_desc)

    val presets = buildList {
        addAll(WallpaperPreset.MinimalBuiltIns)
        if (customWallpaperBitmap != null) {
            add(WallpaperPreset.CUSTOM_PHOTO)
        }
    }

    Box(
        modifier = modifier
            .padding(end = 3.dp, bottom = 3.dp)
            .hard2dShadow(
                shape = trayShape,
                shadowColor = palette.hardShadowDark,
                offsetX = 4.dp,
                offsetY = 4.dp,
            )
            .clip(trayShape)
            .background(palette.glassDarkSurface)
            .border(1.8.dp, palette.glassDarkBorder, trayShape)
            .testTag("wallpaper_picker_tray"),
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
                            .size(28.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(palette.acidLime)
                            .border(1.4.dp, palette.inkBlack, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Wallpaper,
                            contentDescription = null,
                            tint = palette.inkBlack,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(
                        text = stringResource(R.string.wallpaper_picker_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = TropicalWhite,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.graphicElevated)
                            .border(1.dp, palette.glassDarkBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.wallpaper_presets_count_fmt, presets.size),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = palette.mutedText,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val photoBtnShape = RoundedCornerShape(10.dp)
                    Row(
                        modifier = Modifier
                            .padding(end = 2.dp, bottom = 2.dp)
                            .hard2dShadow(
                                shape = photoBtnShape,
                                shadowColor = palette.inkBlack,
                                offsetX = 2.dp,
                                offsetY = 2.dp,
                            )
                            .clip(photoBtnShape)
                            .background(palette.searchButtonBlue)
                            .border(1.2.dp, TropicalWhite.copy(alpha = 0.85f), photoBtnShape)
                            .clickable(role = Role.Button, onClick = onPickCustomPhoto)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .semantics { contentDescription = photoDesc }
                            .testTag("pick_wallpaper_photo_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = null,
                            tint = TropicalWhite,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.pick_wallpaper_photo),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = TropicalWhite,
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("close_wallpaper_picker_button"),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(palette.graphicElevated)
                                .border(1.2.dp, palette.glassDarkBorder, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = closeDesc,
                                tint = TropicalWhite,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.glassDarkBorder),
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(presets, key = { it.name }) { preset ->
                    WallpaperPresetTile(
                        preset = preset,
                        customBitmap = customWallpaperBitmap,
                        isSelected = preset == selectedPreset,
                        onClick = { onSelectPreset(preset) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WallpaperPresetTile(
    preset: WallpaperPreset,
    customBitmap: ImageBitmap?,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalTropicalPalette.current
    val label = stringResource(preset.labelRes)
    val tileShape = RoundedCornerShape(14.dp)

    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "wallpaper_tile_scale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) palette.acidLime else palette.glassDarkBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "wallpaper_tile_border",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .scale(cardScale.coerceAtLeast(0.95f))
            .clickable(role = Role.RadioButton, onClick = onClick)
            .testTag("wallpaper_preset_${preset.name.lowercase()}"),
    ) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 94.dp)
                .padding(end = 2.5.dp, bottom = 2.5.dp)
                .hard2dShadow(
                    shape = tileShape,
                    shadowColor = if (isSelected) palette.acidLime else palette.hardShadowDeep,
                    offsetX = 2.5.dp,
                    offsetY = 2.5.dp,
                )
                .clip(tileShape)
                .border(
                    width = if (isSelected) 2.dp else 1.4.dp,
                    color = borderColor,
                    shape = tileShape,
                ),
        ) {
            MinimalWallpaperPreviewCanvas(
                preset = preset,
                customBitmap = customBitmap,
                modifier = Modifier.fillMaxSize(),
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(palette.acidLime)
                        .border(1.2.dp, palette.inkBlack, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = palette.inkBlack,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
            ),
            color = if (isSelected) palette.acidLime else palette.dateWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
