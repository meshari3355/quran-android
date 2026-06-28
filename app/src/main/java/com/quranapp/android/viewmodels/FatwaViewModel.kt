package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.repository.FatwaRepository
import com.quranapp.android.models.ServerFatwa
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FatwaUiState(
    val fatwas: List<ServerFatwa> = emptyList(),
    val selectedFatwa: ServerFatwa? = null,
    val searchQuery: String = "",
    val searchResults: List<ServerFatwa> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FatwaViewModel @Inject constructor(
    private val fatwaRepository: FatwaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FatwaUiState())
    val uiState: StateFlow<FatwaUiState> = _uiState.asStateFlow()

    init {
        loadFatwas()
    }

    fun loadFatwas(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = fatwaRepository.getFatwaList(page = page, perPage = 20)
                result.onSuccess { fatwas ->
                    val allFatwas = if (page == 1) fatwas else _uiState.value.fatwas + fatwas
                    _uiState.update {
                        it.copy(
                            fatwas = allFatwas,
                            isLoading = false,
                            currentPage = page,
                            hasMore = fatwas.size >= 20
                        )
                    }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "حدث خطأ غير معروف") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "حدث خطأ غير معروف") }
            }
        }
    }

    fun loadMore() {
        if (!_uiState.value.isLoading && _uiState.value.hasMore) {
            loadFatwas(_uiState.value.currentPage + 1)
        }
    }

    fun selectFatwa(fatwa: ServerFatwa) {
        _uiState.update { it.copy(selectedFatwa = fatwa) }
    }

    fun deselectFatwa() {
        _uiState.update { it.copy(selectedFatwa = null) }
    }

    fun searchFatwas(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isSearching = true, error = null) }
            if (query.isEmpty()) {
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                return@launch
            }
            try {
                val result = fatwaRepository.searchFatwas(query)
                result.onSuccess { fatwas ->
                    _uiState.update { it.copy(searchResults = fatwas, isSearching = false) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(isSearching = false, error = e.message ?: "خطأ في البحث") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, error = e.message ?: "خطأ في البحث") }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        if (_uiState.value.searchQuery.isNotEmpty()) {
            searchFatwas(_uiState.value.searchQuery)
        } else {
            loadFatwas(1)
        }
    }

    private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        value = function(value)
    }
}
