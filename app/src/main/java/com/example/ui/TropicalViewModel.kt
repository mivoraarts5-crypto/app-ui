package com.example.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimeMode {
    MOCKUP,
    LIVE,
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

sealed interface FeedbackMessage {
    data class OpeningApp(@param:StringRes val appNameRes: Int) : FeedbackMessage
    data class SearchingQuery(val query: String) : FeedbackMessage
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
) {
    val hasAnyMorph: Boolean
        get() = activeDockApp != null ||
            isSunMorphed ||
            isClockMorphed ||
            isWaveMorphed ||
            isIslandMorphed ||
            isPalmMorphed ||
            isDateMorphed ||
            isSearchCapsuleExpanded
}

class TropicalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TropicalUiState())
    val uiState: StateFlow<TropicalUiState> = _uiState.asStateFlow()

    private var feedbackJob: Job? = null

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
        val togglingSame = _uiState.value.activeDockApp == app
        _uiState.update {
            it.copy(
                activeDockApp = if (togglingSame) null else app,
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
                isSearchOpen = false,
                isSearchCapsuleExpanded = false,
                searchQuery = "",
            )
        }
        showFeedback(FeedbackMessage.OpeningApp(app.labelRes))
    }

    fun resetAllMorphs() {
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
                searchQuery = "",
            )
        }
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
