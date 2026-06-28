package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.repository.QuranRepository
import com.quranapp.android.data.repository.TafsirRepository
import com.quranapp.android.models.Surah
import com.quranapp.android.models.BookmarkModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data classes for UI state
data class Ayah(
    val id: Long,
    val surahId: Int,
    val number: Int,
    val text: String,
    val transliteration: String = "",
    val translation: String = "",
    val tafsir: String? = null,
    val page: Int = 1,
    val juz: Int = 1,
    val hizbQuarter: Int = 1,
    val sajdah: Boolean = false
)

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentSurah: Int = 0,
    val currentAyah: Int = 0,
    val currentReciter: Int = 0,
    val progress: Float = 0f,
    val duration: Float = 0f,
    val playbackSpeed: Float = 1f
)

data class TafsirData(
    val id: Long,
    val surahId: Int,
    val ayahNumber: Int,
    val tafsirText: String,
    val author: String = "",
    val source: String = "",
    val language: String = "ar"
)

data class Bookmark(
    val id: Long,
    val surahId: Int,
    val ayahNumber: Int,
    val page: Int,
    val timestamp: Long,
    val notes: String = ""
)

data class QuranUiState(
    val surahList: List<Surah> = emptyList(),
    val currentSurah: Surah? = null,
    val currentPage: Int = 1,
    val currentAyah: Ayah? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filterType: SurahFilter = SurahFilter.ALL,
    val bookmarks: List<Bookmark> = emptyList(),
    val audioState: AudioPlaybackState = AudioPlaybackState(),
    val tafsirData: TafsirData? = null,
    val offlineCacheState: OfflineCacheState = OfflineCacheState(),
    val error: String? = null
)

enum class SurahFilter {
    ALL,
    MECCAN,
    MEDINAN
}

data class OfflineCacheState(
    val isCached: Boolean = false,
    val cacheProgress: Float = 0f,
    val cachedSurahIds: List<Int> = emptyList()
)

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    private val tafsirRepository: TafsirRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    val surahListStream: StateFlow<List<Surah>> = quranRepository.getSurahsStream()
        .map { result -> result.getOrDefault(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadSurahList()
    }

    fun loadSurahList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                quranRepository.getSurahsStream().collect { result ->
                    result.onSuccess { surahs ->
                        _uiState.update { it.copy(surahList = surahs, isLoading = false) }
                    }
                    result.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadSurah(surahId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                quranRepository.getSurahByIdStream(surahId).collect { result ->
                    result.onSuccess { surah ->
                        _uiState.update { it.copy(currentSurah = surah, isLoading = false) }
                    }
                    result.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadAyah(surahId: Int, ayahNumber: Int) {
        viewModelScope.launch {
            try {
                val result = quranRepository.getSurahVerses(surahId)
                result.onSuccess { verses ->
                    val serverVerse = verses.find { it.verseNumber == ayahNumber }
                    if (serverVerse != null) {
                        val ayah = Ayah(
                            id = serverVerse.id.toLong(),
                            surahId = serverVerse.suraId,
                            number = serverVerse.verseNumber,
                            text = serverVerse.textUthmani,
                            page = serverVerse.page ?: 1,
                            juz = serverVerse.juz ?: 1,
                            sajdah = (serverVerse.sajda ?: 0) > 0
                        )
                        _uiState.update { it.copy(currentAyah = ayah) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * Load verses for a specific mushaf page from the API
     */
    suspend fun loadPageVerses(page: Int): Result<List<com.quranapp.android.models.ServerVerse>> {
        return quranRepository.getPage(page)
    }

    fun setCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun searchSurahs(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    return@launch
                }
                val results = _uiState.value.surahList.filter { surah ->
                    surah.nameEn.contains(query, ignoreCase = true) ||
                    surah.nameAr.contains(query)
                }
                _uiState.update { it.copy(surahList = results) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun filterSurahs(filterType: SurahFilter) {
        _uiState.update { it.copy(filterType = filterType) }
        viewModelScope.launch {
            try {
                val filtered = when (filterType) {
                    SurahFilter.ALL -> _uiState.value.surahList
                    SurahFilter.MECCAN -> _uiState.value.surahList.filter { it.type == "مكية" }
                    SurahFilter.MEDINAN -> _uiState.value.surahList.filter { it.type == "مدنية" }
                }
                _uiState.update { it.copy(surahList = filtered) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addBookmark(surahId: Int, ayahNumber: Int, page: Int = 1) {
        viewModelScope.launch {
            try {
                val bookmark = Bookmark(
                    id = 0,
                    surahId = surahId,
                    ayahNumber = ayahNumber,
                    page = page,
                    timestamp = System.currentTimeMillis(),
                    notes = ""
                )
                val bookmarkModel = BookmarkModel(
                    id = "",
                    surahName = "Surah $surahId",
                    surahId = surahId,
                    page = page,
                    timestamp = System.currentTimeMillis(),
                    verseNumber = ayahNumber,
                    notes = ""
                )
                quranRepository.addBookmark(bookmarkModel)
                val bookmarks = _uiState.value.bookmarks + bookmark
                _uiState.update { it.copy(bookmarks = bookmarks) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                quranRepository.removeBookmark(bookmarkId.toString())
                val bookmarks = _uiState.value.bookmarks.filter { it.id != bookmarkId }
                _uiState.update { it.copy(bookmarks = bookmarks) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            try {
                val result = quranRepository.getBookmarks()
                result.onSuccess { bookmarkModels ->
                    val bookmarks = bookmarkModels.map { model ->
                        Bookmark(
                            id = model.id.toLongOrNull() ?: 0,
                            surahId = model.surahId,
                            ayahNumber = model.verseNumber,
                            page = model.page,
                            timestamp = model.timestamp,
                            notes = model.notes
                        )
                    }
                    _uiState.update { it.copy(bookmarks = bookmarks) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun startAudioPlayback(surahId: Int, reciterId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        audioState = it.audioState.copy(
                            isPlaying = true,
                            currentSurah = surahId,
                            currentReciter = reciterId
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun pauseAudioPlayback() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(audioState = it.audioState.copy(isPlaying = false)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resumeAudioPlayback() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(audioState = it.audioState.copy(isPlaying = true)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setAudioProgress(progress: Float) {
        _uiState.update { it.copy(audioState = it.audioState.copy(progress = progress)) }
    }

    fun loadTafsir(surahId: Int, ayahNumber: Int) {
        viewModelScope.launch {
            try {
                val result = tafsirRepository.getTafsir(surahId)
                result.onSuccess { response ->
                    // Find the specific ayah's tafsir from the response data
                    val ayahTafsir = response.data?.getOrNull(ayahNumber - 1)
                    if (ayahTafsir != null) {
                        val tafsir = TafsirData(
                            id = ayahTafsir.id.toLong(),
                            surahId = surahId,
                            ayahNumber = ayahNumber,
                            tafsirText = ayahTafsir.text,
                            author = response.tafsir ?: "تفسير ابن كثير",
                            source = "quran.meshari.tech",
                            language = "ar"
                        )
                        _uiState.update { it.copy(tafsirData = tafsir) }
                    }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun downloadSurah(surahId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        offlineCacheState = it.offlineCacheState.copy(cacheProgress = 0.1f)
                    )
                }
                val cachedIds = _uiState.value.offlineCacheState.cachedSurahIds + surahId
                _uiState.update {
                    it.copy(
                        offlineCacheState = it.offlineCacheState.copy(
                            cachedSurahIds = cachedIds,
                            isCached = true,
                            cacheProgress = 1f
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearOfflineCache() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(offlineCacheState = OfflineCacheState()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        value = function(value)
    }
}
