package com.example.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.DockAppGlyph
import com.example.ui.components.ExternalCanvasToolbar
import com.example.ui.components.FreehandCanvasCard
import com.example.ui.components.MusicPickerTray
import com.example.ui.components.MusicTimelinePanel
import com.example.ui.components.MusicTrack
import com.example.ui.components.PlayBottomNavigationBar
import com.example.ui.components.PlayFeaturedAppStrip
import com.example.ui.components.PlayFilterChipsRow
import com.example.ui.components.PlayNavTab
import com.example.ui.components.PlayStoreHeroHeader
import com.example.ui.components.PlaySuggestedAppsSection
import com.example.ui.components.TropicalBackground
import com.example.ui.components.WallpaperPickerTray
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite
import kotlinx.coroutines.launch

private fun <T> playSpring() = spring<T>(
    dampingRatio = 0.72f,
    stiffness = Spring.StiffnessMediumLow,
)

@Composable
fun TropicalHomeScreen(
    viewModel: TropicalViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addDeviceAudioUris(uris)
        }
    }

    val wallpaperGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addCustomWallpaperUris(uris)
        }
    }

    val wallpaperDeviceFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addCustomWallpaperUris(uris)
        }
    }

    TropicalHomeContent(
        uiState = uiState,
        onTimeModeSelected = viewModel::setTimeMode,
        onDockAppClick = viewModel::onDockAppClicked,
        onSearchResultAppClick = viewModel::onSearchResultAppClicked,
        onSunClick = viewModel::toggleSunMorph,
        onWaveClick = viewModel::toggleWaveMorph,
        onIslandClick = viewModel::toggleIslandMorph,
        onPalmClick = viewModel::togglePalmMorph,
        onClockClick = viewModel::toggleClockMorph,
        onDateClick = viewModel::toggleDateMorph,
        onOpenFullSearch = viewModel::openFullSearch,
        onCloseSearch = viewModel::closeSearch,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSubmitSearch = viewModel::submitSearch,
        onResetAll = viewModel::resetAllMorphs,
        onStartStroke = viewModel::startStroke,
        onAppendStrokePoint = viewModel::appendStrokePoint,
        onFinishStroke = viewModel::finishStroke,
        onSelectBrushColor = viewModel::selectBrushColor,
        onCycleBrushSize = viewModel::cycleBrushSize,
        onSelectCanvasBgColor = viewModel::selectCanvasBgColor,
        onSelectCanvasRatio = viewModel::selectCanvasRatio,
        onToggleCanvasToolbar = viewModel::toggleCanvasToolbar,
        onToggleDockVisibility = viewModel::toggleDockVisibility,
        onToggleWallpaperPicker = viewModel::toggleWallpaperPicker,
        onCloseWallpaperPicker = viewModel::closeWallpaperPicker,
        onSelectWallpaperPreset = viewModel::selectWallpaperPreset,
        onSelectUploadedWallpaper = viewModel::selectUploadedWallpaper,
        onRemoveUploadedWallpaper = viewModel::removeUploadedWallpaper,
        onPickWallpaperFromGallery = {
            wallpaperGalleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onPickWallpaperFromDeviceFiles = {
            wallpaperDeviceFileLauncher.launch(arrayOf("image/*"))
        },
        onSelectMusicTrack = viewModel::addMusicToCanvas,
        onPickMusicFromDevice = { audioPickerLauncher.launch(arrayOf("audio/*")) },
        onCloseMusicPicker = viewModel::closeMusicPicker,
        onSelectMusicLayer = viewModel::selectMusicLayer,
        onToggleMusicPlay = viewModel::toggleCanvasMusicPlayback,
        onSeekMusicToMs = viewModel::seekMusicToMs,
        onRemoveMusicLayer = viewModel::removeMusicLayer,
        onMoveMusicLayerSticker = viewModel::moveMusicLayerSticker,
        onToggleMusicLayerVisibility = viewModel::toggleMusicLayerVisibility,
        onUndoStroke = viewModel::undoStroke,
        onClearStrokes = viewModel::clearStrokes,
        modifier = modifier,
    )
}

@Composable
fun TropicalHomeContent(
    uiState: TropicalUiState,
    onTimeModeSelected: (TimeMode) -> Unit,
    onDockAppClick: (DockApp) -> Unit,
    onSearchResultAppClick: (DockApp) -> Unit,
    onSunClick: () -> Unit,
    onWaveClick: () -> Unit,
    onIslandClick: () -> Unit,
    onPalmClick: () -> Unit,
    onClockClick: () -> Unit,
    onDateClick: () -> Unit,
    onOpenFullSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    onResetAll: () -> Unit,
    onStartStroke: (Offset) -> Unit,
    onAppendStrokePoint: (Offset) -> Unit,
    onFinishStroke: () -> Unit,
    onSelectBrushColor: (Int) -> Unit,
    onCycleBrushSize: () -> Unit,
    onSelectCanvasBgColor: (Int) -> Unit,
    onSelectCanvasRatio: (CanvasRatio) -> Unit,
    onToggleCanvasToolbar: () -> Unit,
    onToggleDockVisibility: () -> Unit,
    onToggleWallpaperPicker: () -> Unit,
    onCloseWallpaperPicker: () -> Unit,
    onSelectWallpaperPreset: (WallpaperPreset) -> Unit,
    onSelectUploadedWallpaper: (String) -> Unit,
    onRemoveUploadedWallpaper: (String) -> Unit,
    onPickWallpaperFromGallery: () -> Unit,
    onPickWallpaperFromDeviceFiles: () -> Unit,
    onSelectMusicTrack: (MusicTrack) -> Unit,
    onPickMusicFromDevice: () -> Unit,
    onCloseMusicPicker: () -> Unit,
    onSelectMusicLayer: (Long) -> Unit,
    onToggleMusicPlay: () -> Unit,
    onSeekMusicToMs: (Long) -> Unit,
    onRemoveMusicLayer: (Long) -> Unit,
    onMoveMusicLayerSticker: (Long, Offset) -> Unit,
    onToggleMusicLayerVisibility: (Long) -> Unit,
    onUndoStroke: () -> Unit,
    onClearStrokes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(
        enabled = !uiState.isSearchOpen &&
            (uiState.isWallpaperPickerVisible || uiState.isMusicPickerVisible || uiState.isCanvasToolbarVisible),
    ) {
        when {
            uiState.isWallpaperPickerVisible -> onCloseWallpaperPicker()
            uiState.isMusicPickerVisible -> onCloseMusicPicker()
            uiState.isCanvasToolbarVisible -> onToggleCanvasToolbar()
        }
    }

    val activeNavTab = when {
        uiState.isSearchOpen -> PlayNavTab.SEARCH
        uiState.isWallpaperPickerVisible -> PlayNavTab.STUDIO
        uiState.isMusicPickerVisible -> PlayNavTab.APPS
        else -> PlayNavTab.CANVAS
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.playSurfaceWhite),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- 1. PLAY STORE PURPLE HERO BANNER + TOP BAR ---
            PlayStoreHeroHeader(
                clockText = uiState.clockText,
                secondsText = uiState.secondsText,
                dayText = uiState.dayText,
                dateText = uiState.dateText,
                timeMode = uiState.timeMode,
                isClockMorphed = uiState.isClockMorphed,
                isDateMorphed = uiState.isDateMorphed,
                layerCount = uiState.musicLayers.size,
                isWallpaperPickerVisible = uiState.isWallpaperPickerVisible,
                isDockVisible = uiState.isDockVisible,
                onClockClick = onClockClick,
                onDateClick = onDateClick,
                onToggleWallpaperPicker = onToggleWallpaperPicker,
                onToggleDockVisibility = onToggleDockVisibility,
                onLayerBadgeClick = { onDockAppClick(DockApp.MUSIC) },
            )

            // --- 2. FEATURED ACTION STRIP ("Freehand Canvas • Ready • Tools") ---
            PlayFeaturedAppStrip(
                selectedRatio = uiState.selectedCanvasRatio,
                strokesCount = uiState.strokes.size,
                isToolbarVisible = uiState.isCanvasToolbarVisible,
                onToggleToolbar = onToggleCanvasToolbar,
            )

            // --- 3. SCROLLABLE PLAY STOREFRONT + STUDIO TOOLS ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {
                // Quick Filter / Tool Chips ("Brush & Ratio | Music | Wallpapers | 19:19")
                PlayFilterChipsRow(
                    isToolbarVisible = uiState.isCanvasToolbarVisible,
                    isMusicPickerVisible = uiState.isMusicPickerVisible,
                    isWallpaperPickerVisible = uiState.isWallpaperPickerVisible,
                    timeMode = uiState.timeMode,
                    hasAnyMorph = uiState.hasAnyMorph,
                    onToggleToolbar = onToggleCanvasToolbar,
                    onToggleMusic = { onDockAppClick(DockApp.MUSIC) },
                    onToggleWallpapers = onToggleWallpaperPicker,
                    onSelectTimeMode = onTimeModeSelected,
                    onResetAll = onResetAll,
                )

                // External Canvas Toolbar (Brush, Size, Ratio, Canvas BG, Undo, Clear)
                AnimatedVisibility(
                    visible = uiState.isCanvasToolbarVisible,
                    enter = fadeIn(tween(180)) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = playSpring(),
                    ),
                    exit = fadeOut(tween(140)) + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(160),
                    ),
                ) {
                    ExternalCanvasToolbar(
                        hasStrokes = uiState.strokes.isNotEmpty(),
                        selectedColorIndex = uiState.selectedColorIndex,
                        selectedBrushSizeIndex = uiState.selectedBrushSizeIndex,
                        selectedCanvasBgIndex = uiState.selectedCanvasBgIndex,
                        selectedCanvasRatio = uiState.selectedCanvasRatio,
                        onSelectColor = onSelectBrushColor,
                        onCycleBrushSize = onCycleBrushSize,
                        onSelectCanvasBgColor = onSelectCanvasBgColor,
                        onSelectCanvasRatio = onSelectCanvasRatio,
                        onUndo = onUndoStroke,
                        onClear = onClearStrokes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }

                // Wallpaper Picker Tray (Upload from Gallery, Device Files, 7 Minimal Presets)
                AnimatedVisibility(
                    visible = uiState.isWallpaperPickerVisible,
                    enter = fadeIn(tween(180)) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = playSpring(),
                    ),
                    exit = fadeOut(tween(140)) + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(160),
                    ),
                ) {
                    WallpaperPickerTray(
                        selectedPreset = uiState.wallpaperPreset,
                        uploadedWallpapers = uiState.uploadedWallpapers,
                        activeUploadedWallpaperId = uiState.activeUploadedWallpaperId,
                        onSelectPreset = onSelectWallpaperPreset,
                        onSelectUploadedWallpaper = onSelectUploadedWallpaper,
                        onRemoveUploadedWallpaper = onRemoveUploadedWallpaper,
                        onPickFromGallery = onPickWallpaperFromGallery,
                        onPickFromDeviceFiles = onPickWallpaperFromDeviceFiles,
                        onClose = onCloseWallpaperPicker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }

                // Music Library & Device Audio Picker Tray
                AnimatedVisibility(
                    visible = uiState.isMusicPickerVisible,
                    enter = fadeIn(tween(180)) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = playSpring(),
                    ),
                    exit = fadeOut(tween(140)) + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(160),
                    ),
                ) {
                    MusicPickerTray(
                        tracks = uiState.musicTracks,
                        addedTrackIds = uiState.musicLayers.map { it.track.id }.toSet(),
                        onSelectTrack = onSelectMusicTrack,
                        onPickFromDevice = onPickMusicFromDevice,
                        onClose = onCloseMusicPicker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }

                // --- STUDIO CANVAS SECTION (Wallpaper Backdrop + Freehand Canvas + Music Stickers) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.play_section_studio),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = palette.playTextPrimary,
                    )
                    Text(
                        text = stringResource(uiState.wallpaperPreset.labelRes),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = palette.playBlueAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.playChipActiveBg)
                            .clickable(role = Role.Button, onClick = onToggleWallpaperPicker)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                val stageShape = RoundedCornerShape(22.dp)
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(274.dp)
                        .clip(stageShape)
                        .border(1.dp, palette.playDivider, stageShape),
                    contentAlignment = Alignment.Center,
                ) {
                    TropicalBackground(
                        wallpaperPreset = uiState.wallpaperPreset,
                        customWallpaperBitmap = uiState.activeCustomWallpaperBitmap,
                        isSunMorphed = uiState.isSunMorphed,
                        isWaveMorphed = uiState.isWaveMorphed,
                        isIslandMorphed = uiState.isIslandMorphed,
                        isPalmMorphed = uiState.isPalmMorphed,
                        onSunClick = onSunClick,
                        onWaveClick = onWaveClick,
                        onIslandClick = onIslandClick,
                        onPalmClick = onPalmClick,
                        modifier = Modifier.fillMaxSize(),
                    )

                    FreehandCanvasCard(
                        availableWidth = maxWidth - 20.dp,
                        availableHeight = maxHeight - 20.dp,
                        strokes = uiState.strokes,
                        activeStroke = uiState.activeStroke,
                        selectedCanvasBgIndex = uiState.selectedCanvasBgIndex,
                        selectedCanvasRatio = uiState.selectedCanvasRatio,
                        musicLayers = uiState.musicLayers,
                        activeLayerId = uiState.activeLayerId,
                        isMusicPlaying = uiState.isMusicPlaying,
                        onStartStroke = onStartStroke,
                        onAppendStrokePoint = onAppendStrokePoint,
                        onFinishStroke = onFinishStroke,
                        onToggleLayerPlay = onSelectMusicLayer,
                        onRemoveLayer = onRemoveMusicLayer,
                        onMoveLayerSticker = onMoveMusicLayerSticker,
                    )
                }

                // --- MULTI-LAYER MUSIC TIMELINE + SINGLE SHARED TIME RULER ---
                AnimatedVisibility(
                    visible = uiState.musicLayers.isNotEmpty(),
                    enter = fadeIn(tween(180)) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = playSpring(),
                    ),
                    exit = fadeOut(tween(140)) + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(160),
                    ),
                ) {
                    val timelineShape = RoundedCornerShape(16.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(timelineShape)
                            .border(1.dp, palette.playChipBorder, timelineShape),
                    ) {
                        MusicTimelinePanel(
                            layers = uiState.musicLayers,
                            activeLayerId = uiState.activeLayerId,
                            positionMs = uiState.musicPositionMs,
                            isPlaying = uiState.isMusicPlaying,
                            onSelectLayer = onSelectMusicLayer,
                            onTogglePlay = onToggleMusicPlay,
                            onSeekToMs = onSeekMusicToMs,
                            onRemoveLayer = onRemoveMusicLayer,
                            onToggleLayerVisibility = onToggleMusicLayerVisibility,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // --- "SUGGESTED FOR YOU" APPS ROW (exact Play Store horizontal card layout) ---
                PlaySuggestedAppsSection(
                    apps = uiState.dockApps,
                    activeDockApp = uiState.activeDockApp,
                    onAppClick = onDockAppClick,
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- 4. GOOGLE PLAY STORE BOTTOM NAVIGATION BAR ---
            PlayBottomNavigationBar(
                activeTab = activeNavTab,
                onSelectTab = { tab ->
                    when (tab) {
                        PlayNavTab.CANVAS -> {
                            if (uiState.isWallpaperPickerVisible) onCloseWallpaperPicker()
                            if (uiState.isMusicPickerVisible) onCloseMusicPicker()
                            coroutineScope.launch { scrollState.animateScrollTo(0) }
                        }
                        PlayNavTab.APPS -> {
                            onDockAppClick(DockApp.MUSIC)
                            coroutineScope.launch { scrollState.animateScrollTo(0) }
                        }
                        PlayNavTab.SEARCH -> {
                            onOpenFullSearch()
                        }
                        PlayNavTab.STUDIO -> {
                            onToggleWallpaperPicker()
                            coroutineScope.launch { scrollState.animateScrollTo(0) }
                        }
                    }
                },
            )
        }

        // Play Store Toast Pill anchored cleanly above bottom navigation bar
        AnimatedVisibility(
            visible = uiState.feedback != null,
            enter = fadeIn(tween(180)) + slideInVertically(tween(200)) { it / 2 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(160)) { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 78.dp),
        ) {
            uiState.feedback?.let { feedback ->
                val msg = when (feedback) {
                    is FeedbackMessage.OpeningApp ->
                        stringResource(R.string.opening_app_fmt, stringResource(feedback.appNameRes))
                    is FeedbackMessage.SearchingQuery ->
                        stringResource(R.string.searching_for_fmt, feedback.query)
                    is FeedbackMessage.AddedMusicToCanvas ->
                        stringResource(R.string.added_music_to_canvas_fmt, feedback.trackTitle)
                    is FeedbackMessage.ChangedWallpaper ->
                        stringResource(R.string.wallpaper_selected_fmt, stringResource(feedback.nameRes))
                    is FeedbackMessage.UploadedWallpapers ->
                        stringResource(R.string.uploaded_wallpaper_toast_fmt, feedback.count)
                }
                val toastShape = RoundedCornerShape(999.dp)
                Box(
                    modifier = Modifier
                        .shadow(elevation = 4.dp, shape = toastShape, clip = false)
                        .clip(toastShape)
                        .background(palette.playTextPrimary)
                        .testTag("feedback_toast"),
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = TropicalWhite,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                    )
                }
            }
        }

        // Full-Screen Play Store Search Overlay
        AnimatedVisibility(
            visible = uiState.isSearchOpen,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(160)),
        ) {
            SearchOverlay(
                query = uiState.searchQuery,
                dockApps = uiState.dockApps,
                onQueryChange = onSearchQueryChange,
                onSubmit = onSubmitSearch,
                onClose = onCloseSearch,
                onAppClick = onSearchResultAppClick,
            )
        }
    }
}

@Composable
private fun SearchOverlay(
    query: String,
    dockApps: List<DockApp>,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    onAppClick: (DockApp) -> Unit,
) {
    BackHandler(onBack = onClose)

    val palette = LocalTropicalPalette.current
    val focusRequester = remember { FocusRequester() }
    val closeDesc = stringResource(R.string.close_search_desc)
    val placeholderText = stringResource(R.string.search_placeholder)
    val hintText = stringResource(R.string.search_hint)

    val resolvedAppLabels = dockApps.associateWith { stringResource(it.labelRes) }
    val matchingApps by remember(query, dockApps) {
        derivedStateOf {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                dockApps
            } else {
                dockApps.filter { app ->
                    val label = resolvedAppLabels[app].orEmpty()
                    label.contains(trimmed, ignoreCase = true) ||
                        app.searchKeywords.contains(trimmed, ignoreCase = true)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.playSurfaceWhite)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.playNavBarBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = palette.playTextSecondary,
                    modifier = Modifier.size(20.dp),
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                            color = palette.playTextSecondary,
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.playTextPrimary,
                        ),
                        cursorBrush = SolidColor(palette.playBlueAccent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .testTag("search_input"),
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("close_search_button"),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(palette.playSurfaceWhite)
                            .border(1.dp, palette.playChipBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = closeDesc,
                            tint = palette.playTextPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = hintText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = palette.playTextSecondary,
                modifier = Modifier.padding(horizontal = 6.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(matchingApps, key = { it.name }) { app ->
                    val appName = stringResource(app.labelRes)
                    val resultShape = RoundedCornerShape(14.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(resultShape)
                            .background(palette.playSurfaceSubtle)
                            .border(1.dp, palette.playDivider, resultShape)
                            .clickable(role = Role.Button) { onAppClick(app) }
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                            .testTag("search_result_${app.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        DockAppGlyph(app = app, modifier = Modifier.size(32.dp))
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = palette.playTextPrimary,
                        )
                    }
                }
            }
        }
    }
}
