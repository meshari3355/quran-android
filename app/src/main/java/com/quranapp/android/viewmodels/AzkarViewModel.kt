package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.repository.AzkarRepository
import com.quranapp.android.models.Zikr
import com.quranapp.android.models.ZikrCategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AzkarCategory(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val description: String,
    val icon: String? = null,
    val color: String = "#B8840A",
    val zikrCount: Int = 0
)

data class AzkarProgress(
    val id: Long,
    val categoryId: String,
    val zikrId: String,
    val count: Int,
    val isCompleted: Boolean,
    val timestamp: Long
)

data class AzkarUiState(
    val categories: List<AzkarCategory> = emptyList(),
    val currentCategory: AzkarCategory? = null,
    val zikrs: List<Zikr> = emptyList(),
    val currentZikrIndex: Int = 0,
    val progress: Map<String, AzkarProgress> = emptyMap(),
    val tasbihCount: Int = 0,
    val maxTasbihCount: Int = 100,
    val isLoading: Boolean = false,
    val categoryCompletionPercentage: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val azkarRepository: AzkarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AzkarUiState())
    val uiState: StateFlow<AzkarUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadProgress()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                azkarRepository.getAzkarCategoriesStream().collect { result ->
                    result.onSuccess { zikrCategories ->
                        val categories = zikrCategories.map { zikrCat ->
                            AzkarCategory(
                                id = zikrCat.id.toString(),
                                nameArabic = zikrCat.name,
                                nameEnglish = zikrCat.nameEn,
                                description = "",
                                icon = zikrCat.icon,
                                color = zikrCat.color,
                                zikrCount = zikrCat.items.size
                            )
                        }
                        _uiState.update { it.copy(categories = categories, isLoading = false) }
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

    fun loadCategoryZikrs(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val category = _uiState.value.categories.find { it.id == categoryId }
                val categoryType = try {
                    ZikrCategoryType.valueOf(categoryId.uppercase())
                } catch (e: IllegalArgumentException) {
                    // Try matching by partial name or index
                    ZikrCategoryType.values().find {
                        it.name.equals(categoryId, ignoreCase = true) ||
                        it.englishName.equals(categoryId, ignoreCase = true)
                    } ?: ZikrCategoryType.GENERAL
                }

                azkarRepository.getAzkarByCategoryStream(categoryType).collect { result ->
                    result.onSuccess { zikrs ->
                        val percentage = calculateCompletionPercentage(categoryId)
                        _uiState.update {
                            it.copy(
                                currentCategory = category,
                                zikrs = zikrs,
                                currentZikrIndex = 0,
                                categoryCompletionPercentage = percentage,
                                isLoading = false
                            )
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

    fun getCurrentZikr(): Zikr? {
        return _uiState.value.zikrs.getOrNull(_uiState.value.currentZikrIndex)
    }

    fun nextZikr() {
        val nextIndex = _uiState.value.currentZikrIndex + 1
        if (nextIndex < _uiState.value.zikrs.size) {
            _uiState.update { it.copy(currentZikrIndex = nextIndex) }
            resetTasbihCount()
        }
    }

    fun previousZikr() {
        val prevIndex = _uiState.value.currentZikrIndex - 1
        if (prevIndex >= 0) {
            _uiState.update { it.copy(currentZikrIndex = prevIndex) }
            resetTasbihCount()
        }
    }

    fun incrementTasbih() {
        val currentCount = _uiState.value.tasbihCount
        if (currentCount < _uiState.value.maxTasbihCount) {
            _uiState.update { it.copy(tasbihCount = currentCount + 1) }
        } else {
            nextZikr()
        }
    }

    fun resetTasbihCount() {
        _uiState.update { it.copy(tasbihCount = 0) }
    }

    fun setMaxTasbihCount(count: Int) {
        _uiState.update { it.copy(maxTasbihCount = count) }
    }

    fun saveProgress() {
        viewModelScope.launch {
            try {
                val currentZikr = getCurrentZikr()
                val currentCategory = _uiState.value.currentCategory

                if (currentZikr != null && currentCategory != null) {
                    azkarRepository.saveAzkarProgress(
                        currentCategory.id,
                        _uiState.value.tasbihCount
                    )

                    val progress = AzkarProgress(
                        id = 0,
                        categoryId = currentCategory.id,
                        zikrId = currentZikr.id,
                        count = _uiState.value.tasbihCount,
                        isCompleted = _uiState.value.tasbihCount >= _uiState.value.maxTasbihCount,
                        timestamp = System.currentTimeMillis()
                    )

                    val updatedProgress = _uiState.value.progress.toMutableMap()
                    updatedProgress[currentZikr.id] = progress
                    _uiState.update { it.copy(progress = updatedProgress) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadProgress() {
        viewModelScope.launch {
            try {
                val allProgress = emptyMap<String, AzkarProgress>()
                _uiState.update { it.copy(progress = allProgress) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetCategoryProgress(categoryId: String) {
        viewModelScope.launch {
            try {
                azkarRepository.resetAzkarProgress(categoryId)
                val updatedProgress = _uiState.value.progress.toMutableMap()
                updatedProgress.entries.removeAll { it.value.categoryId == categoryId }

                _uiState.update {
                    it.copy(
                        progress = updatedProgress,
                        categoryCompletionPercentage = calculateCompletionPercentage(categoryId)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        progress = emptyMap(),
                        tasbihCount = 0,
                        categoryCompletionPercentage = 0f
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun calculateCompletionPercentage(categoryId: String): Float {
        val categoryZikrs = _uiState.value.zikrs
        if (categoryZikrs.isEmpty()) return 0f

        val completedCount = categoryZikrs.count { zikr ->
            _uiState.value.progress[zikr.id]?.isCompleted == true
        }

        return (completedCount.toFloat() / categoryZikrs.size) * 100f
    }

    fun getDailyCompletionPercentage(): Float {
        val allZikrs = _uiState.value.zikrs
        if (allZikrs.isEmpty()) return 0f

        val completedCount = allZikrs.count { zikr ->
            _uiState.value.progress[zikr.id]?.isCompleted == true
        }

        return (completedCount.toFloat() / allZikrs.size) * 100f
    }

    fun isZikrCompleted(zikrId: String): Boolean {
        val progress = _uiState.value.progress[zikrId]
        return progress?.isCompleted == true
    }

    fun getRemainingCount(zikrId: String): Int {
        val progress = _uiState.value.progress[zikrId]
        val zikr = _uiState.value.zikrs.find { it.id == zikrId }
        return if (progress != null && zikr != null) {
            (zikr.count - progress.count).coerceAtLeast(0)
        } else {
            zikr?.count ?: 0
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        value = function(value)
    }

}
