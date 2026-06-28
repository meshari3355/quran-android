package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.repository.HadithRepository
import com.quranapp.android.models.HadithPortalCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HadithEntry(
    val id: String,
    val bookId: String,
    val number: Int,
    val gradeArabic: String,
    val gradeEnglish: String,
    val text: String,
    val textEn: String = "",
    val isnad: String = "",
    val matn: String = "",
    val explanation: String = "",
    val narrator: String = "",
    val chain: String = ""
)

data class HadithUiState(
    val portalCategories: List<HadithPortalCategory> = emptyList(),
    val hadithBooks: List<com.quranapp.android.models.HadithBookSummary> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<HadithEntry> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val offlineDownloads: List<String> = emptyList(),
    val downloadProgress: Float = 0f,
    val isDownloading: Boolean = false,
    val currentHadith: HadithEntry? = null,
    val error: String? = null
)

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val hadithRepository: HadithRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HadithUiState())
    val uiState: StateFlow<HadithUiState> = _uiState.asStateFlow()

    init {
        loadPortalCategories()
        loadOfflineDownloads()
    }

    fun loadPortalCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                hadithRepository.getHadithCategoriesStream().collect { result ->
                    result.onSuccess { categories ->
                        _uiState.update { state ->
                            state.copy(portalCategories = categories, isLoading = false)
                        }
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

    fun loadBooks(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                hadithRepository.getHadithBooksStream(categoryId).collect { result ->
                    result.onSuccess { books ->
                        _uiState.update { state ->
                            state.copy(hadithBooks = books, isLoading = false)
                        }
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

    fun loadChapters(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = hadithRepository.getHadithBook(bookId)
                result.onSuccess { book ->
                    _uiState.update { it.copy(isLoading = false) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadBabs(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadHadiths(babId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadHadith(hadithId: String) {
        viewModelScope.launch {
            try {
                val result = hadithRepository.getHadithById(hadithId)
                result.onSuccess { hadith ->
                    val hadithEntry = HadithEntry(
                        id = hadith.id,
                        bookId = "",
                        number = hadith.hadithNumber,
                        gradeArabic = "",
                        gradeEnglish = "",
                        text = hadith.text,
                        narrator = hadith.narrator,
                        chain = hadith.chain
                    )
                    _uiState.update { it.copy(currentHadith = hadithEntry) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        value = function(value)
    }

    fun searchHadiths(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isSearching = true) }
            try {
                if (query.isEmpty()) {
                    _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                    return@launch
                }

                hadithRepository.searchHadithStream(query).collect { result ->
                    result.onSuccess { hadithTexts ->
                        val results = hadithTexts.map { text ->
                            HadithEntry(
                                id = text.id,
                                bookId = "",
                                number = text.hadithNumber,
                                gradeArabic = "",
                                gradeEnglish = "",
                                text = text.text,
                                narrator = text.narrator,
                                chain = text.chain
                            )
                        }
                        _uiState.update { it.copy(searchResults = results, isSearching = false) }
                    }
                    result.onFailure { e ->
                        _uiState.update { it.copy(isSearching = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, error = e.message) }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }

    private fun loadOfflineDownloads() {
        viewModelScope.launch {
            try {
                val result = hadithRepository.getFavoriteHadiths()
                result.onSuccess { hadithTexts ->
                    // NOTE: HadithText doesn't contain bookId, so downloads will be empty
                    // TODO: Update HadithText model to include bookId or use a wrapper type
                    val downloads = emptyList<String>()
                    _uiState.update { it.copy(offlineDownloads = downloads) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun downloadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true) }
            try {
                for (i in 0..100 step 10) {
                    _uiState.update { it.copy(downloadProgress = i / 100f) }
                    kotlinx.coroutines.delay(500)
                }

                val downloads = _uiState.value.offlineDownloads + bookId
                _uiState.update {
                    it.copy(
                        offlineDownloads = downloads,
                        isDownloading = false,
                        downloadProgress = 0f
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDownloading = false, error = e.message) }
            }
        }
    }

    fun removeDownload(bookId: String) {
        viewModelScope.launch {
            try {
                val downloads = _uiState.value.offlineDownloads.filter { it != bookId }
                _uiState.update { it.copy(offlineDownloads = downloads) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun isBookDownloaded(bookId: String): Boolean {
        return _uiState.value.offlineDownloads.contains(bookId)
    }

    fun getDownloadSize(bookId: String): String {
        return "5.2 MB"
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

}
