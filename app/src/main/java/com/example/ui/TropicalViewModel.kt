package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.AmbientMusicPlayer
import com.example.audio.extractDeviceMusicTrack
import com.example.ui.components.DefaultMusicLibrary
import com.example.ui.components.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimeMode {
    MOCKUP,
    LIVE,
}

enum class CanvasRatio(
    @param:StringRes val labelRes: Int,
    val aspect: Float,
) {
    SQUARE(R.string.ratio_1_1, 1f),
    LANDSCAPE_4_3(R.string.ratio_4_3, 4f / 3f),
    PORTRAIT_3_4(R.string.ratio_3_4, 3f / 4f),
    PORTRAIT_9_16(R.string.ratio_9_16, 9f / 16f),
    WIDE_16_9(R.string.ratio_16_9, 16f / 9f),
}

enum class DockApp(
    @param:StringRes val labelRes: Int,
    val searchKeywords: String,
    val testTag: String,
) {
    MUSIC(R.string.app_music, "music audio songs player", "app_icon_music"),
    BROWSER(R.string.app_browser, "browser web internet chrome", "app_icon_browser"),
    INSTAGRAM(R.string.app_instagram, "instagram photos social", "app_icon_instagram"),
    TELEGRAM(R.string.app_telegram, "telegram chat messages", "app_icon_telegram"),
    WHATSAPP(R.string.app_whatsapp, "whatsapp chat call messages", "app_icon_whatsapp"),
    TWITTER(R.string.app_twitter, "twitter x bird social", "app_icon_twitter"),
    NOTES(R.string.app_notes, "notes files memo write", "app_icon_notes"),
    PHONE(R.string.app_phone, "phone call dialer contacts", "app_icon_phone"),
}

data class DrawStroke(
    val id: Long,
    val normalizedPoints: List<Offset>,
    val colorIndex: Int,
    val strokeWidthDp: Float,
)

data class MusicLayer(
    val layerId: Long,
    val track: MusicTrack,
    val offsetNormalized: Offset = Offset.Zero,
)

sealed interface FeedbackMessage {
    data class OpeningApp(@param:StringRes val appNameRes: Int) : FeedbackMessage
    data class SearchingQuery(val query: String) : FeedbackMessage
    data class AddedMusicToCanvas(val trackTitle: String) : FeedbackMessage
}

data class TropicalUiState(
    val timeMode: TimeMode = TimeMode.MOCKUP,
    val clockText: String = "19:19",
    val secondsText: String = "00",
    val dayText: String = "MON",
    val dateText: String = "SEP. 13",
    val isSearchOpen: Boolean = false,
    val isSearchCapsuleExpanded: Boolean = false,
    val searchQuery: String = "",
    val feedback: FeedbackMessage? = null,
    val dockApps: List<DockApp> = DockApp.entries,
    val activeDockApp: DockApp? = null,
    val isSunMorphed: Boolean = false,
    val isClockMorphed: Boolean = false,
    val isWaveMorphed: Boolean = false,
    val isIslandMorphed: Boolean = false,
    val isPalmMorphed: Boolean = false,
    val isDateMorphed: Boolean = false,
    val strokes: List<DrawStroke> = emptyList(),
    val activeStroke: DrawStroke? = null,
    val selectedColorIndex: Int = 0,
    val selectedBrushSizeIndex: Int = 1,
    val selectedCanvasBgIndex: Int = 0,
    val selectedCanvasRatio: CanvasRatio = CanvasRatio.SQUARE,
    val isCanvasToolbarVisible: Boolean = false,
    val isDockVisible: Boolean = false,
    val musicTracks: List<MusicTrack> = DefaultMusicLibrary,
    val isMusicPickerVisible: Boolean = false,
    val musicLayers: List<MusicLayer> = emptyList(),
    val activeLayerId: Long? = null,
    val isMusicPlaying: Boolean = false,
    val musicPositionMs: Long = 0L,
) {
    val activeMusicLayer: MusicLayer?
        get() = musicLayers.firstOrNull { it.layerId == activeLayerId } ?: musicLayers.firstOrNull()

    val hasAnyMorph: Boolean
        get() = activeDockApp != null ||
            isSunMorphed ||
            isClockMorphed ||
            isWaveMorphed ||
            isIslandMorphed ||
            isPalmMorphed ||
            isDateMorphed ||
            isSearchCapsuleExpanded ||
            isMusicPickerVisible ||
            musicLayers.isNotEmpty() ||
            selectedCanvasBgIndex != 0 ||
            selectedCanvasRatio != CanvasRatio.SQUARE
}

class TropicalViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(TropicalUiState())
    val uiState: StateFlow<TropicalUiState> = _uiState.asStateFlow()

    private val ambientMusicPlayer = AmbientMusicPlayer()
    private var feedbackJob: Job? = null
    private var musicProgressJob: Job? = null
    private var playWallStartMs: Long = 0L
    private var playOffsetStartMs: Long = 0L
    private var nextStrokeId = 1L
    private var nextLayerId = 1L

    private val brushWidthsDp = listOf(3.5f, 6.5f, 11f)

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val secondsFormat = SimpleDateFormat("ss", Locale.US)
    private val dayFormat = SimpleDateFormat("EEE", Locale.US)
    private val monthFormat = SimpleDateFormat("MMM", Locale.US)
    private val dayNumFormat = SimpleDateFormat("d", Locale.US)

    init {
        startClockTicker()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (isActive) {
                if (_uiState.value.timeMode == TimeMode.LIVE) {
                    applyLiveTime()
                }
                delay(1000L)
            }
        }
    }

    fun setTimeMode(mode: TimeMode) {
        if (mode == TimeMode.MOCKUP) {
            _uiState.update {
                it.copy(
                    timeMode = TimeMode.MOCKUP,
                    clockText = "19:19",
                    secondsText = "00",
                    dayText = "MON",
                    dateText = "SEP. 13",
                )
            }
        } else {
            _uiState.update { it.copy(timeMode = TimeMode.LIVE) }
            applyLiveTime()
        }
    }

    private fun applyLiveTime() {
        val now = Date()
        val hhMm = timeFormat.format(now)
        val sec = secondsFormat.format(now)
        val day = dayFormat.format(now).uppercase(Locale.US)
        val month = monthFormat.format(now).uppercase(Locale.US)
        val dayNum = dayNumFormat.format(now)
        _uiState.update {
            it.copy(
                clockText = hhMm,
                secondsText = sec,
                dayText = day,
                dateText = "$month. $dayNum",
            )
        }
    }

    fun startStroke(normalizedPoint: Offset) {
        val state = _uiState.value
        val widthDp = brushWidthsDp[state.selectedBrushSizeIndex.coerceIn(brushWidthsDp.indices)]
        val stroke = DrawStroke(
            id = nextStrokeId++,
            normalizedPoints = listOf(normalizedPoint),
            colorIndex = state.selectedColorIndex,
            strokeWidthDp = widthDp,
        )
        _uiState.update { it.copy(activeStroke = stroke) }
    }

    fun appendStrokePoint(normalizedPoint: Offset) {
        _uiState.update { state ->
            val current = state.activeStroke ?: return@update state
            val last = current.normalizedPoints.lastOrNull()
            if (last != null && (normalizedPoint - last).getDistanceSquared() < 0.000015f) {
                return@update state
            }
            state.copy(
                activeStroke = current.copy(
                    normalizedPoints = current.normalizedPoints + normalizedPoint,
                ),
            )
        }
    }

    fun finishStroke() {
        _uiState.update { state ->
            val finished = state.activeStroke ?: return@update state
            state.copy(
                strokes = state.strokes + finished,
                activeStroke = null,
            )
        }
    }

    fun undoStroke() {
        _uiState.update { state ->
            if (state.strokes.isEmpty()) state
            else state.copy(strokes = state.strokes.dropLast(1))
        }
    }

    fun clearStrokes() {
        _uiState.update { it.copy(strokes = emptyList(), activeStroke = null) }
    }

    fun selectBrushColor(index: Int) {
        _uiState.update { it.copy(selectedColorIndex = index) }
    }

    fun cycleBrushSize() {
        _uiState.update { state ->
            state.copy(selectedBrushSizeIndex = (state.selectedBrushSizeIndex + 1) % brushWidthsDp.size)
        }
    }

    fun selectCanvasBgColor(index: Int) {
        _uiState.update { state ->
            val switchingToDark = index == 4 && state.selectedColorIndex == 0
            state.copy(
                selectedCanvasBgIndex = index,
                selectedColorIndex = if (switchingToDark) 5 else state.selectedColorIndex,
            )
        }
    }

    fun selectCanvasRatio(ratio: CanvasRatio) {
        _uiState.update { it.copy(selectedCanvasRatio = ratio) }
    }

    fun toggleCanvasToolbar() {
        _uiState.update { it.copy(isCanvasToolbarVisible = !it.isCanvasToolbarVisible) }
    }

    fun toggleDockVisibility() {
        _uiState.update { it.copy(isDockVisible = !it.isDockVisible) }
    }

    fun closeMusicPicker() {
        _uiState.update {
            it.copy(
                isMusicPickerVisible = false,
                activeDockApp = if (it.activeDockApp == DockApp.MUSIC) null else it.activeDockApp,
            )
        }
    }

    fun addDeviceAudioUris(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val appContext = getApplication<Application>()
        viewModelScope.launch {
            val extracted = withContext(Dispatchers.IO) {
                val existingSize = _uiState.value.musicTracks.size
                uris.mapIndexed { idx, uri ->
                    extractDeviceMusicTrack(
                        context = appContext,
                        uri = uri,
                        accentSeed = existingSize + idx,
                    )
                }
            }
            if (extracted.isEmpty()) return@launch

            val existingCount = _uiState.value.musicLayers.size
            val newLayers = extracted.mapIndexed { idx, track ->
                val slot = (existingCount + idx) % 4
                MusicLayer(
                    layerId = nextLayerId++,
                    track = track,
                    offsetNormalized = Offset(0f, (-0.15f * slot).coerceIn(-0.48f, 0f)),
                )
            }
            val activeLayer = newLayers.first()
            ambientMusicPlayer.play(appContext, activeLayer.track, 0L)
            startMusicProgressTicker(startOffsetMs = 0L)

            _uiState.update { state ->
                val newUris = extracted.mapNotNull { it.contentUriString }.toSet()
                val remaining = state.musicTracks.filterNot { it.contentUriString in newUris }
                state.copy(
                    musicTracks = extracted + remaining,
                    musicLayers = state.musicLayers + newLayers,
                    activeLayerId = activeLayer.layerId,
                    isMusicPlaying = true,
                    musicPositionMs = 0L,
                )
            }
            showFeedback(FeedbackMessage.AddedMusicToCanvas(activeLayer.track.title))
        }
    }

    fun addMusicToCanvas(track: MusicTrack) {
        val currentLayers = _uiState.value.musicLayers
        val slot = currentLayers.size % 4
        val newLayer = MusicLayer(
            layerId = nextLayerId++,
            track = track,
            offsetNormalized = Offset(0f, (-0.15f * slot).coerceIn(-0.48f, 0f)),
        )
        ambientMusicPlayer.play(getApplication(), track, 0L)
        startMusicProgressTicker(startOffsetMs = 0L)
        _uiState.update {
            it.copy(
                musicLayers = it.musicLayers + newLayer,
                activeLayerId = newLayer.layerId,
                isMusicPlaying = true,
                musicPositionMs = 0L,
            )
        }
        showFeedback(FeedbackMessage.AddedMusicToCanvas(track.title))
    }

    fun selectMusicLayer(layerId: Long) {
        val state = _uiState.value
        val targetLayer = state.musicLayers.firstOrNull { it.layerId == layerId } ?: return
        if (state.activeLayerId == layerId) {
            toggleCanvasMusicPlayback()
            return
        }
        val startMs = state.musicPositionMs.coerceIn(0L, targetLayer.track.durationMs.coerceAtLeast(1_000L))
        ambientMusicPlayer.play(getApplication(), targetLayer.track, startMs)
        startMusicProgressTicker(startOffsetMs = startMs)
        _uiState.update {
            it.copy(
                activeLayerId = layerId,
                isMusicPlaying = true,
                musicPositionMs = startMs,
            )
        }
    }

    fun toggleCanvasMusicPlayback() {
        val state = _uiState.value
        val activeLayer = state.activeMusicLayer ?: return
        val nextPlaying = !state.isMusicPlaying
        if (nextPlaying) {
            val startMs = state.musicPositionMs.coerceIn(0L, activeLayer.track.durationMs.coerceAtLeast(1_000L))
            ambientMusicPlayer.play(getApplication(), activeLayer.track, startMs)
            startMusicProgressTicker(startOffsetMs = startMs)
        } else {
            musicProgressJob?.cancel()
            musicProgressJob = null
            ambientMusicPlayer.stop()
        }
        _uiState.update { it.copy(isMusicPlaying = nextPlaying) }
    }

    fun seekMusicToMs(positionMs: Long) {
        val state = _uiState.value
        if (state.musicLayers.isEmpty()) return
        val totalTimelineMs = state.musicLayers.maxOf { it.track.durationMs }.coerceAtLeast(1_000L)
        val clampedTimeline = positionMs.coerceIn(0L, totalTimelineMs)
        val activeTrackDur = state.activeMusicLayer?.track?.durationMs?.coerceAtLeast(1_000L) ?: totalTimelineMs
        ambientMusicPlayer.seekTo(clampedTimeline.coerceIn(0L, activeTrackDur))
        playWallStartMs = System.currentTimeMillis()
        playOffsetStartMs = clampedTimeline
        _uiState.update { it.copy(musicPositionMs = clampedTimeline) }
    }

    fun removeMusicLayer(layerId: Long) {
        val state = _uiState.value
        val updatedLayers = state.musicLayers.filterNot { it.layerId == layerId }
        if (updatedLayers.isEmpty()) {
            musicProgressJob?.cancel()
            musicProgressJob = null
            ambientMusicPlayer.stop()
            _uiState.update {
                it.copy(
                    musicLayers = emptyList(),
                    activeLayerId = null,
                    isMusicPlaying = false,
                    musicPositionMs = 0L,
                )
            }
            return
        }

        val wasActiveRemoved = state.activeLayerId == layerId
        val nextActive = if (wasActiveRemoved) updatedLayers.last() else state.activeMusicLayer
        if (wasActiveRemoved && state.isMusicPlaying && nextActive != null) {
            val startMs = state.musicPositionMs.coerceIn(0L, nextActive.track.durationMs.coerceAtLeast(1_000L))
            ambientMusicPlayer.play(getApplication(), nextActive.track, startMs)
            startMusicProgressTicker(startOffsetMs = startMs)
        }
        _uiState.update {
            it.copy(
                musicLayers = updatedLayers,
                activeLayerId = nextActive?.layerId,
            )
        }
    }

    private fun startMusicProgressTicker(startOffsetMs: Long) {
        musicProgressJob?.cancel()
        playWallStartMs = System.currentTimeMillis()
        playOffsetStartMs = startOffsetMs
        musicProgressJob = viewModelScope.launch {
            while (isActive) {
                delay(160L)
                val state = _uiState.value
                val activeLayer = state.activeMusicLayer ?: break
                if (!state.isMusicPlaying) break
                val dur = activeLayer.track.durationMs.coerceAtLeast(1_000L)
                val playerPos = ambientMusicPlayer.getCurrentPositionMs()
                val elapsedWall = (System.currentTimeMillis() - playWallStartMs).coerceAtLeast(0L)
                val nextPos = if (playerPos > 0L) {
                    playerPos.coerceIn(0L, dur)
                } else {
                    (playOffsetStartMs + elapsedWall) % dur
                }
                _uiState.update { it.copy(musicPositionMs = nextPos) }
            }
        }
    }

    fun moveMusicLayerSticker(layerId: Long, deltaNormalized: Offset) {
        _uiState.update { state ->
            val updated = state.musicLayers.map { layer ->
                if (layer.layerId != layerId) {
                    layer
                } else {
                    val current = layer.offsetNormalized
                    val next = Offset(
                        x = (current.x + deltaNormalized.x).coerceIn(-0.22f, 0.22f),
                        y = (current.y + deltaNormalized.y).coerceIn(-0.68f, 0.04f),
                    )
                    layer.copy(offsetNormalized = next)
                }
            }
            state.copy(musicLayers = updated)
        }
    }

    fun toggleSunMorph() {
        _uiState.update { it.copy(isSunMorphed = !it.isSunMorphed) }
    }

    fun toggleClockMorph() {
        _uiState.update { it.copy(isClockMorphed = !it.isClockMorphed) }
    }

    fun toggleWaveMorph() {
        _uiState.update { it.copy(isWaveMorphed = !it.isWaveMorphed) }
    }

    fun toggleIslandMorph() {
        _uiState.update { it.copy(isIslandMorphed = !it.isIslandMorphed) }
    }

    fun togglePalmMorph() {
        _uiState.update { it.copy(isPalmMorphed = !it.isPalmMorphed) }
    }

    fun toggleDateMorph() {
        _uiState.update { it.copy(isDateMorphed = !it.isDateMorphed) }
    }

    fun toggleSearchCapsule() {
        _uiState.update {
            val next = !it.isSearchCapsuleExpanded
            it.copy(
                isSearchCapsuleExpanded = next,
                searchQuery = if (!next) "" else it.searchQuery,
            )
        }
    }

    fun openFullSearch() {
        _uiState.update {
            it.copy(
                isSearchOpen = true,
                isSearchCapsuleExpanded = false,
            )
        }
    }

    fun closeSearch() {
        _uiState.update {
            it.copy(
                isSearchOpen = false,
                isSearchCapsuleExpanded = false,
                searchQuery = "",
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun submitSearch() {
        val trimmed = _uiState.value.searchQuery.trim()
        if (trimmed.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    isSearchOpen = false,
                    isSearchCapsuleExpanded = false,
                    searchQuery = "",
                )
            }
            showFeedback(FeedbackMessage.SearchingQuery(trimmed))
        }
    }

    fun onDockAppClicked(app: DockApp) {
        if (app == DockApp.MUSIC) {
            val opening = !(_uiState.value.activeDockApp == DockApp.MUSIC && _uiState.value.isMusicPickerVisible)
            _uiState.update {
                it.copy(
                    activeDockApp = if (opening) DockApp.MUSIC else null,
                    isMusicPickerVisible = opening,
                    isSearchOpen = false,
                    searchQuery = "",
                )
            }
            return
        }

        val togglingSame = _uiState.value.activeDockApp == app
        _uiState.update {
            it.copy(
                activeDockApp = if (togglingSame) null else app,
                isMusicPickerVisible = false,
                isSearchOpen = false,
                searchQuery = "",
            )
        }
        if (!togglingSame) {
            showFeedback(FeedbackMessage.OpeningApp(app.labelRes))
        }
    }

    fun onSearchResultAppClicked(app: DockApp) {
        _uiState.update {
            it.copy(
                activeDockApp = app,
                isMusicPickerVisible = app == DockApp.MUSIC,
                isSearchOpen = false,
                isSearchCapsuleExpanded = false,
                searchQuery = "",
            )
        }
        if (app != DockApp.MUSIC) {
            showFeedback(FeedbackMessage.OpeningApp(app.labelRes))
        }
    }

    fun resetAllMorphs() {
        musicProgressJob?.cancel()
        musicProgressJob = null
        ambientMusicPlayer.stop()
        _uiState.update {
            it.copy(
                activeDockApp = null,
                isSunMorphed = false,
                isClockMorphed = false,
                isWaveMorphed = false,
                isIslandMorphed = false,
                isPalmMorphed = false,
                isDateMorphed = false,
                isSearchCapsuleExpanded = false,
                isMusicPickerVisible = false,
                musicLayers = emptyList(),
                activeLayerId = null,
                isMusicPlaying = false,
                musicPositionMs = 0L,
                searchQuery = "",
                selectedCanvasBgIndex = 0,
                selectedCanvasRatio = CanvasRatio.SQUARE,
            )
        }
    }

    override fun onCleared() {
        musicProgressJob?.cancel()
        ambientMusicPlayer.release()
        super.onCleared()
    }

    private fun showFeedback(message: FeedbackMessage) {
        feedbackJob?.cancel()
        _uiState.update { it.copy(feedback = message) }
        feedbackJob = viewModelScope.launch {
            delay(1800L)
            _uiState.update { it.copy(feedback = null) }
        }
    }
}
