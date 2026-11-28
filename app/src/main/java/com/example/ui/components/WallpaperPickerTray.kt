package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FolderOpen
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.UploadedWallpaper
import com.example.ui.WallpaperPreset
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite

@Composable
fun WallpaperPickerTray(
    selectedPreset: WallpaperPreset,
    uploadedWallpapers: List<UploadedWallpaper>,
    activeUploadedWallpaperId: String?,
    onSelectPreset: (WallpaperPreset) -> Unit,
    onSelectUploadedWallpaper: (String) -> Unit,
    onRemoveUploadedWallpaper: (String) -> Unit,
    onPickFromGallery: () -> Unit,
    onPickFromDeviceFiles: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val trayShape = RoundedCornerShape(18.dp)
    val closeDesc = stringResource(R.string.close_wallpaper_picker_desc)
    val deviceDesc = stringResource(R.string.pick_wallpaper_photo_desc)
    val uploadDesc = stringResource(R.string.upload_wallpaper_card_desc)
    val removeUploadedDesc = stringResource(R.string.remove_uploaded_wallpaper_desc)

    val presets = WallpaperPreset.MinimalBuiltIns
    val totalOptionsCount = presets.size + uploadedWallpapers.size

    Box(
        modifier = modifier
            .shadow(elevation = 3.dp, shape = trayShape, clip = false)
            .clip(trayShape)
            .background(palette.playSurfaceWhite)
            .border(1.dp, palette.playChipBorder, trayShape)
            .testTag("wallpaper_picker_tray"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.playChipActiveBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Wallpaper,
                            contentDescription = null,
                            tint = palette.playBlueAccent,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(
                        text = stringResource(R.string.wallpaper_picker_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = palette.playTextPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.playSurfaceSubtle)
                            .border(1.dp, palette.playDivider, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.wallpaper_presets_count_fmt, totalOptionsCount),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = palette.playTextSecondary,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val deviceBtnShape = RoundedCornerShape(999.dp)
                    Row(
                        modifier = Modifier
                            .clip(deviceBtnShape)
                            .background(palette.playVioletCta)
                            .clickable(role = Role.Button, onClick = onPickFromDeviceFiles)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .semantics { contentDescription = deviceDesc }
                            .testTag("pick_wallpaper_photo_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            tint = TropicalWhite,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.pick_wallpaper_photo),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
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
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(palette.playSurfaceSubtle)
                                .border(1.dp, palette.playDivider, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = closeDesc,
                                tint = palette.playTextPrimary,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.playDivider),
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Dedicated first-position Upload tile for instant gallery wallpaper upload
                item(key = "upload_from_gallery_tile") {
                    val tileShape = RoundedCornerShape(16.dp)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(74.dp)
                            .clickable(role = Role.Button, onClick = onPickFromGallery)
                            .semantics { contentDescription = uploadDesc }
                            .testTag("upload_wallpaper_tile"),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 74.dp, height = 94.dp)
                                .clip(tileShape)
                                .background(palette.playSurfaceLavender)
                                .border(1.5.dp, palette.playVioletCta, tileShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(palette.playVioletCta),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AddPhotoAlternate,
                                        contentDescription = uploadDesc,
                                        tint = TropicalWhite,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.upload_wallpaper_card_label),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = palette.playVioletCta,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = stringResource(R.string.wallpaper_custom_photo),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = palette.playTextPrimary,
                            maxLines = 1,
                        )
                    }
                }

                // Uploaded device wallpaper thumbnails
                itemsIndexed(uploadedWallpapers, key = { _, item -> item.id }) { idx, item ->
                    val isSelected = selectedPreset == WallpaperPreset.CUSTOM_PHOTO &&
                        (activeUploadedWallpaperId == item.id || (activeUploadedWallpaperId == null && idx == 0))
                    UploadedWallpaperTile(
                        item = item,
                        label = "${stringResource(R.string.wallpaper_custom_photo)} ${idx + 1}",
                        isSelected = isSelected,
                        removeDesc = removeUploadedDesc,
                        onSelect = { onSelectUploadedWallpaper(item.id) },
                        onRemove = { onRemoveUploadedWallpaper(item.id) },
                    )
                }

                // Curated Minimal Aesthetic 2D wallpapers
                items(presets, key = { it.name }) { preset ->
                    WallpaperPresetTile(
                        preset = preset,
                        isSelected = preset == selectedPreset,
                        onClick = { onSelectPreset(preset) },
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadedWallpaperTile(
    item: UploadedWallpaper,
    label: String,
    isSelected: Boolean,
    removeDesc: String,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalTropicalPalette.current
    val tileShape = RoundedCornerShape(16.dp)

    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "uploaded_tile_scale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) palette.playBlueAccent else palette.playChipBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "uploaded_tile_border",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(74.dp)
            .scale(cardScale.coerceAtLeast(0.95f))
            .clickable(role = Role.RadioButton, onClick = onSelect)
            .testTag("uploaded_wallpaper_${item.id}"),
    ) {
        Box(
            modifier = Modifier
                .size(width = 74.dp, height = 94.dp)
                .clip(tileShape)
                .border(
                    width = if (isSelected) 2.2.dp else 1.dp,
                    color = borderColor,
                    shape = tileShape,
                ),
        ) {
            Image(
                bitmap = item.bitmap,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Remove uploaded wallpaper button
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(palette.playTextPrimary.copy(alpha = 0.82f))
                    .clickable(role = Role.Button, onClick = onRemove)
                    .semantics { contentDescription = removeDesc }
                    .testTag("remove_uploaded_wallpaper_${item.id}"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = removeDesc,
                    tint = TropicalWhite,
                    modifier = Modifier.size(11.dp),
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(palette.playBlueAccent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = TropicalWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            ),
            color = if (isSelected) palette.playNavActiveLabel else palette.playTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WallpaperPresetTile(
    preset: WallpaperPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalTropicalPalette.current
    val label = stringResource(preset.labelRes)
    val tileShape = RoundedCornerShape(16.dp)

    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "wallpaper_tile_scale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) palette.playBlueAccent else palette.playChipBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "wallpaper_tile_border",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(74.dp)
            .scale(cardScale.coerceAtLeast(0.95f))
            .clickable(role = Role.RadioButton, onClick = onClick)
            .testTag("wallpaper_preset_${preset.name.lowercase()}"),
    ) {
        Box(
            modifier = Modifier
                .size(width = 74.dp, height = 94.dp)
                .clip(tileShape)
                .border(
                    width = if (isSelected) 2.2.dp else 1.dp,
                    color = borderColor,
                    shape = tileShape,
                ),
        ) {
            MinimalWallpaperPreviewCanvas(
                preset = preset,
                customBitmap = null,
                modifier = Modifier.fillMaxSize(),
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(palette.playBlueAccent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = TropicalWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            ),
            color = if (isSelected) palette.playNavActiveLabel else palette.playTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
