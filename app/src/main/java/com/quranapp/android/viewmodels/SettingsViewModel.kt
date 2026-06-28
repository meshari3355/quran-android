package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.ui.theme.ThemeMode
import com.quranapp.android.ui.theme.AccentColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.GOLD,
    val language: String = "en",
    val fontSize: Float = 1f,
    val enableNotifications: Boolean = true,
    val enablePrayerNotifications: Boolean = true,
    val enableAzkarReminders: Boolean = true,
    val enableDownloadNotifications: Boolean = true,
    val prayerNotificationTime: Int = 5,
    val azkarReminderTime: String = "06:00",
    val enableNightMode: Boolean = false,
    val dynamicColor: Boolean = true,
    val enableOfflineMode: Boolean = false,
    val autoDownloadData: Boolean = false,
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            try {
                val themeMode = preferencesManager.getThemeMode()
                val accentColor = preferencesManager.getAccentColor()
                val language = preferencesManager.getLanguage()
                val fontSize = preferencesManager.getFontSize()
                val enableNotifications = preferencesManager.getNotificationsEnabled()
                val enablePrayerNotifications = preferencesManager.getPrayerNotificationsEnabled()
                val enableAzkarReminders = preferencesManager.getAzkarRemindersEnabled()
                val enableDownloadNotifications = preferencesManager.getDownloadNotificationsEnabled()
                val prayerNotificationTime = preferencesManager.getPrayerNotificationTime()
                val azkarReminderTime = preferencesManager.getAzkarReminderTime()
                val enableNightMode = preferencesManager.getNightModeEnabled()
                val dynamicColor = preferencesManager.getDynamicColorEnabled()
                val enableOfflineMode = preferencesManager.getOfflineModeEnabled()
                val autoDownloadData = preferencesManager.getAutoDownloadEnabled()

                _uiState.update {
                    it.copy(
                        themeMode = themeMode,
                        accentColor = accentColor,
                        language = language,
                        fontSize = fontSize,
                        enableNotifications = enableNotifications,
                        enablePrayerNotifications = enablePrayerNotifications,
                        enableAzkarReminders = enableAzkarReminders,
                        enableDownloadNotifications = enableDownloadNotifications,
                        prayerNotificationTime = prayerNotificationTime,
                        azkarReminderTime = azkarReminderTime,
                        enableNightMode = enableNightMode,
                        dynamicColor = dynamicColor,
                        enableOfflineMode = enableOfflineMode,
                        autoDownloadData = autoDownloadData
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                preferencesManager.saveThemeMode(themeMode)
                _uiState.update { it.copy(themeMode = themeMode) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            try {
                preferencesManager.saveAccentColor(accentColor)
                _uiState.update { it.copy(accentColor = accentColor) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            try {
                preferencesManager.saveLanguage(language)
                _uiState.update { it.copy(language = language) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setFontSize(fontSize: Float) {
        viewModelScope.launch {
            try {
                preferencesManager.saveFontSize(fontSize)
                _uiState.update { it.copy(fontSize = fontSize) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveNotificationsEnabled(enabled)
                _uiState.update { it.copy(enableNotifications = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setPrayerNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.savePrayerNotificationsEnabled(enabled)
                _uiState.update { it.copy(enablePrayerNotifications = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setAzkarRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveAzkarRemindersEnabled(enabled)
                _uiState.update { it.copy(enableAzkarReminders = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setDownloadNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveDownloadNotificationsEnabled(enabled)
                _uiState.update { it.copy(enableDownloadNotifications = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setPrayerNotificationTime(minutes: Int) {
        viewModelScope.launch {
            try {
                preferencesManager.savePrayerNotificationTime(minutes)
                _uiState.update { it.copy(prayerNotificationTime = minutes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setAzkarReminderTime(time: String) {
        viewModelScope.launch {
            try {
                preferencesManager.saveAzkarReminderTime(time)
                _uiState.update { it.copy(azkarReminderTime = time) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setNightModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveNightModeEnabled(enabled)
                _uiState.update { it.copy(enableNightMode = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveDynamicColorEnabled(enabled)
                _uiState.update { it.copy(dynamicColor = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setOfflineModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveOfflineModeEnabled(enabled)
                _uiState.update { it.copy(enableOfflineMode = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setAutoDownloadEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.saveAutoDownloadEnabled(enabled)
                _uiState.update { it.copy(autoDownloadData = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            try {
                preferencesManager.clearAllPreferences()
                loadSettings()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                preferencesManager.resetToDefaults()
                loadSettings()
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
