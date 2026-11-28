package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.CanvasRatio
import com.example.ui.DockApp
import com.example.ui.TimeMode
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite

enum class PlayNavTab {
    CANVAS,
    APPS,
    SEARCH,
    STUDIO,
}

@Composable
fun PlayStoreHeroHeader(
    clockText: String,
    secondsText: String,
    dayText: String,
    dateText: String,
    timeMode: TimeMode,
    isClockMorphed: Boolean,
    isDateMorphed: Boolean,
    layerCount: Int,
    isWallpaperPickerVisible: Boolean,
    isDockVisible: Boolean,
    onClockClick: () -> Unit,
    onDateClick: () -> Unit,
    onToggleWallpaperPicker: () -> Unit,
    onToggleDockVisibility: () -> Unit,
    onLayerBadgeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val wallpaperDesc = stringResource(R.string.toggle_wallpaper_picker_desc)
    val dockDesc = stringResource(R.string.toggle_app_dock_desc)
    val clockDesc = stringResource(R.string.clock_area_desc)
    val dateDesc = stringResource(R.string.date_area_desc)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.playHeroDeep,
                        palette.playHeroMid,
                        palette.playHeroBright,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // Top Play Store Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PlayPrismGlyph(modifier = Modifier.size(26.dp))

                    // Interactive Clock Pill (preserves clock_display)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (isClockMorphed) Color.White.copy(alpha = 0.24f)
                                else Color.White.copy(alpha = 0.14f),
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = if (isClockMorphed) 0.55f else 0.24f),
                                shape = RoundedCornerShape(999.dp),
                            )
                            .clickable(role = Role.Button, onClick = onClockClick)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .semantics { contentDescription = clockDesc }
                            .testTag("clock_display"),
                    ) {
                        val clockDisplay = if (isClockMorphed) {
                            "$clockText:$secondsText • ${if (timeMode == TimeMode.LIVE) "LIVE" else "19:19"}"
                        } else {
                            clockText
                        }
                        Text(
                            text = clockDisplay,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            ),
                            color = TropicalWhite,
                        )
                    }

                    // Interactive Date Chip (preserves date_widget)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (isDateMorphed) Color.White.copy(alpha = 0.24f)
                                else Color.Transparent,
                            )
                            .clickable(role = Role.Button, onClick = onDateClick)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .semantics { contentDescription = dateDesc }
                            .testTag("date_widget"),
                    ) {
                        Text(
                            text = "$dayText $dateText",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = TropicalWhite.copy(alpha = 0.9f),
                            modifier = Modifier.testTag("date_text"),
                        )
                    }
                }

                // Right Play Store Header Icons (Check-badge, Wallpaper, Avatar)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Checkmark + active layer count badge
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onLayerBadgeClick)
                            .testTag("header_layers_badge"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.music_layer_desc),
                            tint = TropicalWhite,
                            modifier = Modifier.size(22.dp),
                        )
                        if (layerCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(TropicalWhite),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = layerCount.coerceAtMost(9).toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    ),
                                    color = palette.playHeroMid,
                                )
                            }
                        }
                    }

                    // Wallpaper picker icon button
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isWallpaperPickerVisible) Color.White.copy(alpha = 0.24f)
                                else Color.Transparent,
                            )
                            .clickable(role = Role.Button, onClick = onToggleWallpaperPicker)
                            .semantics { contentDescription = wallpaperDesc }
                            .testTag("header_wallpaper_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Wallpaper,
                            contentDescription = wallpaperDesc,
                            tint = TropicalWhite,
                            modifier = Modifier.size(21.dp),
                        )
                    }

                    // Circular purple profile/apps avatar ("T")
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.playAvatarPurple)
                            .border(
                                width = if (isDockVisible) 2.dp else 1.dp,
                                color = if (isDockVisible) TropicalWhite else Color.White.copy(alpha = 0.45f),
                                shape = CircleShape,
                            )
                            .clickable(role = Role.Button, onClick = onToggleDockVisibility)
                            .semantics { contentDescription = dockDesc }
                            .testTag("header_plus_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.play_avatar_letter),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = TropicalWhite,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Compact Hero Illustration + Bold White Headline (matching Play Store Cyber Creative Banner)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.play_hero_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 19.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = TropicalWhite,
                        maxLines = 2,
                    )
                    Text(
                        text = if (layerCount > 0) {
                            stringResource(R.string.music_layers_count_fmt, layerCount)
                        } else {
                            stringResource(R.string.canvas_hint)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = TropicalWhite.copy(alpha = 0.82f),
                        maxLines = 1,
                    )
                }

                PlayCreativeArtwork(
                    modifier = Modifier.size(width = 136.dp, height = 74.dp),
                )
            }
        }
    }
}

@Composable
fun PlayFeaturedAppStrip(
    selectedRatio: CanvasRatio,
    strokesCount: Int,
    isToolbarVisible: Boolean,
    onToggleToolbar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val ratioLabel = stringResource(selectedRatio.labelRes)
    val strokesLabel = stringResource(R.string.play_strokes_count_fmt, strokesCount)
    val toolsDesc = stringResource(R.string.toggle_canvas_toolbar_desc)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.playSurfaceLavender)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Rounded-square vibrant coral-orange studio app icon (matching InShot-style featured icon)
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF2E63), Color(0xFFFF6E30)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(26.dp)) {
                val s = size.minDimension / 24f
                drawRoundRect(
                    color = TropicalWhite,
                    topLeft = Offset(3f * s, 3f * s),
                    size = Size(18f * s, 18f * s),
                    cornerRadius = CornerRadius(4f * s, 4f * s),
                    style = Stroke(width = 2.2f * s),
                )
                drawCircle(
                    color = TropicalWhite,
                    radius = 4f * s,
                    center = Offset(12f * s, 12f * s),
                    style = Stroke(width = 2f * s),
                )
                drawCircle(
                    color = TropicalWhite,
                    radius = 1.4f * s,
                    center = Offset(17.2f * s, 6.8f * s),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(R.string.play_featured_app_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = palette.playTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = palette.playHeroMid,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "${stringResource(R.string.play_featured_status_ready)} • $ratioLabel • $strokesLabel",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = palette.playTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        val ctaBg by animateColorAsState(
            targetValue = if (isToolbarVisible) palette.playNavActiveInk else palette.playVioletCta,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "featured_cta_bg",
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(ctaBg)
                .clickable(role = Role.Button, onClick = onToggleToolbar)
                .semantics { contentDescription = toolsDesc }
                .testTag("header_settings_button")
                .padding(horizontal = 20.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(
                    if (isToolbarVisible) R.string.play_featured_cta_done
                    else R.string.play_featured_cta_draw,
                ),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = TropicalWhite,
            )
        }
    }
}

@Composable
fun PlayFilterChipsRow(
    isToolbarVisible: Boolean,
    isMusicPickerVisible: Boolean,
    isWallpaperPickerVisible: Boolean,
    timeMode: TimeMode,
    hasAnyMorph: Boolean,
    onToggleToolbar: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleWallpapers: () -> Unit,
    onSelectTimeMode: (TimeMode) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayChip(
            label = stringResource(R.string.play_chip_tools),
            icon = Icons.Outlined.Brush,
            selected = isToolbarVisible,
            onClick = onToggleToolbar,
            testTag = "chip_tools",
        )
        PlayChip(
            label = stringResource(R.string.play_chip_music),
            icon = Icons.Outlined.MusicNote,
            selected = isMusicPickerVisible,
            onClick = onToggleMusic,
            testTag = "chip_music",
        )
        PlayChip(
            label = stringResource(R.string.play_chip_wallpapers),
            icon = Icons.Outlined.Wallpaper,
            selected = isWallpaperPickerVisible,
            onClick = onToggleWallpapers,
            testTag = "chip_wallpapers",
        )

        val nextMode = if (timeMode == TimeMode.MOCKUP) TimeMode.LIVE else TimeMode.MOCKUP
        PlayChip(
            label = stringResource(
                if (timeMode == TimeMode.LIVE) R.string.mode_live else R.string.mode_mockup,
            ),
            icon = Icons.Outlined.Schedule,
            selected = timeMode == TimeMode.LIVE,
            onClick = { onSelectTimeMode(nextMode) },
            testTag = if (timeMode == TimeMode.LIVE) "mode_live_button" else "mode_mockup_button",
        )

        AnimatedVisibility(
            visible = hasAnyMorph,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f),
        ) {
            PlayChip(
                label = stringResource(R.string.play_chip_reset),
                icon = Icons.Rounded.Refresh,
                selected = false,
                onClick = onResetAll,
                tintOverride = palette.playVioletCta,
                testTag = "reset_morphs_button",
            )
        }
    }
}

@Composable
private fun PlayChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    tintOverride: Color? = null,
) {
    val palette = LocalTropicalPalette.current
    val bg by animateColorAsState(
        targetValue = if (selected) palette.playChipActiveBg else palette.playChipBg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "play_chip_bg",
    )
    val border by animateColorAsState(
        targetValue = if (selected) palette.playBlueAccent else palette.playChipBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "play_chip_border",
    )
    val chipShape = RoundedCornerShape(10.dp)

    Row(
        modifier = Modifier
            .clip(chipShape)
            .background(bg)
            .border(1.dp, border, chipShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintOverride ?: if (selected) palette.playNavActiveLabel else palette.playBlueAccent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = if (selected) palette.playNavActiveInk else palette.playTextPrimary,
            maxLines = 1,
        )
    }
}

@Composable
fun PlaySuggestedAppsSection(
    apps: List<DockApp>,
    activeDockApp: DockApp?,
    onAppClick: (DockApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.play_section_suggested),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = palette.playTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_dock_row"),
        ) {
            items(apps, key = { it.name }) { app ->
                val isSelected = activeDockApp == app
                val label = stringResource(app.labelRes)
                val rating = when (app) {
                    DockApp.MUSIC -> "4.8"
                    DockApp.BROWSER -> "4.5"
                    DockApp.INSTAGRAM -> "4.6"
                    DockApp.TELEGRAM -> "4.7"
                    DockApp.WHATSAPP -> "4.4"
                    DockApp.TWITTER -> "4.3"
                    DockApp.NOTES -> "4.9"
                    DockApp.PHONE -> "4.6"
                }

                Column(
                    modifier = Modifier
                        .width(86.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button) { onAppClick(app) }
                        .padding(vertical = 4.dp)
                        .testTag(app.testTag),
                    horizontalAlignment = Alignment.Start,
                ) {
                    val cardShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(cardShape)
                            .background(
                                if (isSelected) palette.playChipActiveBg else palette.playSurfaceSubtle,
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) palette.playBlueAccent else palette.playDivider,
                                shape = cardShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        DockAppGlyph(
                            app = app,
                            modifier = Modifier.size(54.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            letterSpacing = 0.sp,
                        ),
                        color = palette.playTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = rating,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                            color = palette.playTextSecondary,
                        )
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = if (isSelected) palette.playBlueAccent else palette.playTextSecondary,
                            modifier = Modifier.size(11.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayBottomNavigationBar(
    activeTab: PlayNavTab,
    onSelectTab: (PlayNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.playNavBarBg)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(palette.playDivider),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayNavItem(
                label = stringResource(R.string.play_nav_canvas),
                selectedIcon = Icons.Rounded.Brush,
                unselectedIcon = Icons.Outlined.Brush,
                selected = activeTab == PlayNavTab.CANVAS,
                onClick = { onSelectTab(PlayNavTab.CANVAS) },
                testTag = "play_nav_canvas",
                modifier = Modifier.weight(1f),
            )
            PlayNavItem(
                label = stringResource(R.string.play_nav_apps),
                selectedIcon = Icons.Rounded.GridView,
                unselectedIcon = Icons.Outlined.GridView,
                selected = activeTab == PlayNavTab.APPS,
                onClick = { onSelectTab(PlayNavTab.APPS) },
                testTag = "play_nav_apps",
                modifier = Modifier.weight(1f),
            )
            PlayNavItem(
                label = stringResource(R.string.play_nav_search),
                selectedIcon = Icons.Rounded.Search,
                unselectedIcon = Icons.Outlined.Search,
                selected = activeTab == PlayNavTab.SEARCH,
                onClick = { onSelectTab(PlayNavTab.SEARCH) },
                testTag = "search_circle_button",
                modifier = Modifier.weight(1f),
            )
            PlayNavItem(
                label = stringResource(R.string.play_nav_wallpapers),
                selectedIcon = Icons.Rounded.Palette,
                unselectedIcon = Icons.Outlined.Palette,
                selected = activeTab == PlayNavTab.STUDIO,
                onClick = { onSelectTab(PlayNavTab.STUDIO) },
                testTag = "play_nav_studio",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlayNavItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val pillBg by animateColorAsState(
        targetValue = if (selected) palette.playNavPillBlue else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_pill_bg",
    )

    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 58.dp, height = 30.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(pillBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (selected) palette.playNavActiveInk else palette.playNavInactive,
                modifier = Modifier.size(21.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = if (selected) palette.playNavActiveLabel else palette.playNavInactive,
            maxLines = 1,
        )
    }
}

@Composable
private fun PlayPrismGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = w * 0.08f
        val left = pad
        val top = pad
        val bottom = h - pad
        val right = w - pad
        val midY = h * 0.5f

        val topFacet = Path().apply {
            moveTo(left, top)
            lineTo(w * 0.62f, midY)
            lineTo(left + w * 0.14f, midY)
            close()
        }
        drawPath(topFacet, color = Color(0xFF00D67D))

        val leftFacet = Path().apply {
            moveTo(left, top)
            lineTo(w * 0.52f, midY)
            lineTo(left, bottom)
            close()
        }
        drawPath(leftFacet, color = Color(0xFF2EA4FF))

        val rightFacet = Path().apply {
            moveTo(w * 0.52f, midY)
            lineTo(right, midY)
            lineTo(w * 0.62f, midY + h * 0.14f)
            lineTo(w * 0.62f, midY - h * 0.14f)
            close()
        }
        drawPath(rightFacet, color = Color(0xFFFFC107))

        val bottomFacet = Path().apply {
            moveTo(left, bottom)
            lineTo(w * 0.62f, midY)
            lineTo(w * 0.44f, bottom - h * 0.10f)
            close()
        }
        drawPath(bottomFacet, color = Color(0xFFFF3D57))

        val tipFacet = Path().apply {
            moveTo(w * 0.56f, midY - h * 0.18f)
            lineTo(right, midY)
            lineTo(w * 0.56f, midY + h * 0.18f)
            close()
        }
        drawPath(tipFacet, color = Color(0xFFFFCA28))
    }
}

@Composable
private fun PlayCreativeArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Subtle outer bounding frame with corner handles (matching screenshot graphic)
        rotate(degrees = -5f, pivot = Offset(w * 0.48f, h * 0.52f)) {
            drawRect(
                color = Color(0xFFFF4EDB).copy(alpha = 0.62f),
                topLeft = Offset(w * 0.14f, h * 0.12f),
                size = Size(w * 0.62f, h * 0.72f),
                style = Stroke(width = 1.4.dp.toPx()),
            )
            val handleR = 2.2.dp.toPx()
            listOf(
                Offset(w * 0.14f, h * 0.12f),
                Offset(w * 0.76f, h * 0.12f),
                Offset(w * 0.14f, h * 0.84f),
                Offset(w * 0.76f, h * 0.84f),
            ).forEach { corner ->
                drawRect(
                    color = Color(0xFFFF6BE8),
                    topLeft = Offset(corner.x - handleR, corner.y - handleR),
                    size = Size(handleR * 2, handleR * 2),
                )
            }
        }

        // Translucent checkered pastel card
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF9DE2), Color(0xFF8CF5F2)),
            ),
            topLeft = Offset(w * 0.22f, h * 0.34f),
            size = Size(w * 0.48f, h * 0.36f),
            cornerRadius = CornerRadius(10.dp.toPx()),
        )

        // Coral filmstrip ribbon
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF4C8B), Color(0xFFFF8C42)),
            ),
            topLeft = Offset(w * 0.46f, h * 0.42f),
            size = Size(w * 0.42f, h * 0.34f),
            cornerRadius = CornerRadius(8.dp.toPx()),
        )

        // Play circle badge bottom-left
        val playCenter = Offset(w * 0.15f, h * 0.68f)
        drawCircle(
            brush = Brush.linearGradient(listOf(Color(0xFFFF5E9A), Color(0xFFFFA84C))),
            radius = 10.dp.toPx(),
            center = playCenter,
        )
        val playTri = Path().apply {
            moveTo(playCenter.x - 2.5.dp.toPx(), playCenter.y - 4.dp.toPx())
            lineTo(playCenter.x + 4.5.dp.toPx(), playCenter.y)
            lineTo(playCenter.x - 2.5.dp.toPx(), playCenter.y + 4.dp.toPx())
            close()
        }
        drawPath(playTri, color = TropicalWhite)

        // Glowing star-eyes badge
        val badgeCenter = Offset(w * 0.34f, h * 0.36f)
        drawCircle(
            brush = Brush.linearGradient(listOf(Color(0xFFFF3F8E), Color(0xFFFF7D6B))),
            radius = 13.dp.toPx(),
            center = badgeCenter,
        )
        drawCircle(color = Color(0xFFFFEB3B), radius = 2.6.dp.toPx(), center = Offset(badgeCenter.x - 4.dp.toPx(), badgeCenter.y - 2.dp.toPx()))
        drawCircle(color = Color(0xFFFFEB3B), radius = 2.6.dp.toPx(), center = Offset(badgeCenter.x + 4.dp.toPx(), badgeCenter.y - 2.dp.toPx()))
        drawArc(
            color = TropicalWhite,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = true,
            topLeft = Offset(badgeCenter.x - 6.dp.toPx(), badgeCenter.y),
            size = Size(12.dp.toPx(), 8.dp.toPx()),
        )

        // Vibrant double musical note top-right
        val noteX = w * 0.61f
        val noteY = h * 0.48f
        drawCircle(color = Color(0xFFFF4D8D), radius = 5.5.dp.toPx(), center = Offset(noteX - 6.dp.toPx(), noteY))
        drawCircle(color = Color(0xFFFF884D), radius = 5.5.dp.toPx(), center = Offset(noteX + 10.dp.toPx(), noteY - 3.dp.toPx()))
        val noteStem = Path().apply {
            moveTo(noteX - 2.dp.toPx(), noteY)
            lineTo(noteX - 2.dp.toPx(), noteY - 21.dp.toPx())
            lineTo(noteX + 14.dp.toPx(), noteY - 25.dp.toPx())
            lineTo(noteX + 14.dp.toPx(), noteY - 3.dp.toPx())
        }
        drawPath(
            path = noteStem,
            brush = Brush.linearGradient(listOf(Color(0xFFFF4D8D), Color(0xFFFFC145))),
            style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Bright lime-yellow creative sparkle pill bottom-right
        drawRoundRect(
            color = Color(0xFFE6FF3D),
            topLeft = Offset(w * 0.72f, h * 0.56f),
            size = Size(22.dp.toPx(), 16.dp.toPx()),
            cornerRadius = CornerRadius(5.dp.toPx()),
        )
        drawCircle(
            color = Color(0xFF1A0959),
            radius = 2.2.dp.toPx(),
            center = Offset(w * 0.72f + 7.dp.toPx(), h * 0.56f + 6.dp.toPx()),
        )
        drawCircle(
            color = Color(0xFF1A0959),
            radius = 2.2.dp.toPx(),
            center = Offset(w * 0.72f + 15.dp.toPx(), h * 0.56f + 10.dp.toPx()),
        )
    }
}
