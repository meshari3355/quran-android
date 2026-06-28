package com.quranapp.android.data.local

import android.content.Context
import android.content.SharedPreferences
import com.quranapp.android.ui.theme.ThemeMode
import com.quranapp.android.ui.theme.AccentColor

class PreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "QuranApp_Preferences"
        private lateinit var instance: PreferencesManager

        fun initialize(context: Context) {
            instance = PreferencesManager(context)
        }

        fun getInstance(): PreferencesManager = instance

        // Preference Keys
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT_COLOR = "accent_color"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_PRAYER_NOTIFICATIONS = "prayer_notifications"
        private const val KEY_AZKAR_REMINDERS = "azkar_reminders"
        private const val KEY_DOWNLOAD_NOTIFICATIONS = "download_notifications"
        private const val KEY_PRAYER_NOTIFICATION_TIME = "prayer_notification_time"
        private const val KEY_AZKAR_REMINDER_TIME = "azkar_reminder_time"
        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_OFFLINE_MODE = "offline_mode"
        private const val KEY_AUTO_DOWNLOAD = "auto_download"
        private const val KEY_LAST_READ_SURAH = "last_read_surah"
        private const val KEY_LAST_READ_PAGE = "last_read_page"
        private const val KEY_LAST_READING_TIMESTAMP = "last_reading_timestamp"
        private const val KEY_NEXT_PRAYER_NAME = "next_prayer_name"
        private const val KEY_NEXT_PRAYER_TIME = "next_prayer_time"
        private const val KEY_LAST_PRAYER_LATITUDE = "last_prayer_latitude"
        private const val KEY_LAST_PRAYER_LONGITUDE = "last_prayer_longitude"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val DEFAULT_PRAYER_LATITUDE = "24.7136"
        private const val DEFAULT_PRAYER_LONGITUDE = "46.6753"
    }

    // Theme Methods
    fun getThemeMode(): ThemeMode {
        val modeStr = sharedPreferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(modeStr ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    fun saveThemeMode(mode: ThemeMode) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    // Accent Color Methods
    fun getAccentColor(): AccentColor {
        val colorStr = sharedPreferences.getString(KEY_ACCENT_COLOR, AccentColor.GOLD.name)
        return try {
            AccentColor.valueOf(colorStr ?: AccentColor.GOLD.name)
        } catch (e: IllegalArgumentException) {
            AccentColor.GOLD
        }
    }

    fun saveAccentColor(color: AccentColor) {
        sharedPreferences.edit().putString(KEY_ACCENT_COLOR, color.name).apply()
    }

    // Language Methods
    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "ar") ?: "ar"
    }

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    // Font Size Methods
    fun getFontSize(): Float {
        return sharedPreferences.getFloat(KEY_FONT_SIZE, 1f)
    }

    fun saveFontSize(size: Float) {
        sharedPreferences.edit().putFloat(KEY_FONT_SIZE, size).apply()
    }

    // Notification Methods
    fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun saveNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getPrayerNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_PRAYER_NOTIFICATIONS, true)
    }

    fun savePrayerNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_PRAYER_NOTIFICATIONS, enabled).apply()
    }

    fun getAzkarRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AZKAR_REMINDERS, true)
    }

    fun saveAzkarRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AZKAR_REMINDERS, enabled).apply()
    }

    fun getDownloadNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DOWNLOAD_NOTIFICATIONS, false)
    }

    fun saveDownloadNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DOWNLOAD_NOTIFICATIONS, enabled).apply()
    }

    fun getPrayerNotificationTime(): Int {
        return sharedPreferences.getInt(KEY_PRAYER_NOTIFICATION_TIME, 5)
    }

    fun savePrayerNotificationTime(minutes: Int) {
        sharedPreferences.edit().putInt(KEY_PRAYER_NOTIFICATION_TIME, minutes).apply()
    }

    fun getAzkarReminderTime(): String {
        return sharedPreferences.getString(KEY_AZKAR_REMINDER_TIME, "06:00") ?: "06:00"
    }

    fun saveAzkarReminderTime(time: String) {
        sharedPreferences.edit().putString(KEY_AZKAR_REMINDER_TIME, time).apply()
    }

    // Display Mode Methods
    fun getNightModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NIGHT_MODE, false)
    }

    fun saveNightModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NIGHT_MODE, enabled).apply()
    }

    fun getDynamicColorEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, true)
    }

    fun saveDynamicColorEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    // Offline Mode Methods
    fun getOfflineModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_OFFLINE_MODE, false)
    }

    fun saveOfflineModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
    }

    fun getAutoDownloadEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_DOWNLOAD, false)
    }

    fun saveAutoDownloadEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_DOWNLOAD, enabled).apply()
    }

    // Reading Progress Methods
    fun getLastReadSurah(): Int {
        return sharedPreferences.getInt(KEY_LAST_READ_SURAH, 1)
    }

    fun saveLastReadSurah(surahId: Int) {
        sharedPreferences.edit().putInt(KEY_LAST_READ_SURAH, surahId).apply()
    }

    fun getLastReadPage(): Int {
        return sharedPreferences.getInt(KEY_LAST_READ_PAGE, 1)
    }

    fun saveLastReadPage(page: Int) {
        sharedPreferences.edit().putInt(KEY_LAST_READ_PAGE, page).apply()
    }

    fun getLastReadingTimestamp(): Long {
        return sharedPreferences.getLong(KEY_LAST_READING_TIMESTAMP, 0L)
    }

    fun saveLastReadingTimestamp(timestamp: Long = System.currentTimeMillis()) {
        sharedPreferences.edit().putLong(KEY_LAST_READING_TIMESTAMP, timestamp).apply()
    }

    fun getNextPrayerName(): String {
        return sharedPreferences.getString(KEY_NEXT_PRAYER_NAME, "الصلاة القادمة") ?: "الصلاة القادمة"
    }

    fun getNextPrayerTime(): String {
        return sharedPreferences.getString(KEY_NEXT_PRAYER_TIME, "--:--") ?: "--:--"
    }

    fun saveNextPrayer(name: String, time: String) {
        sharedPreferences.edit()
            .putString(KEY_NEXT_PRAYER_NAME, name)
            .putString(KEY_NEXT_PRAYER_TIME, time)
            .apply()
    }

    fun getLastPrayerLatitude(): Double {
        return sharedPreferences
            .getString(KEY_LAST_PRAYER_LATITUDE, DEFAULT_PRAYER_LATITUDE)
            ?.toDoubleOrNull()
            ?: DEFAULT_PRAYER_LATITUDE.toDouble()
    }

    fun getLastPrayerLongitude(): Double {
        return sharedPreferences
            .getString(KEY_LAST_PRAYER_LONGITUDE, DEFAULT_PRAYER_LONGITUDE)
            ?.toDoubleOrNull()
            ?: DEFAULT_PRAYER_LONGITUDE.toDouble()
    }

    fun saveLastPrayerLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit()
            .putString(KEY_LAST_PRAYER_LATITUDE, latitude.toString())
            .putString(KEY_LAST_PRAYER_LONGITUDE, longitude.toString())
            .apply()
    }

    // First Launch Methods
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    // Clear All Methods
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    fun resetToDefaults() {
        sharedPreferences.edit().apply {
            putString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            putString(KEY_ACCENT_COLOR, AccentColor.GOLD.name)
            putString(KEY_LANGUAGE, "ar")
            putFloat(KEY_FONT_SIZE, 1f)
            putBoolean(KEY_NOTIFICATIONS_ENABLED, true)
            putBoolean(KEY_PRAYER_NOTIFICATIONS, true)
            putBoolean(KEY_AZKAR_REMINDERS, true)
            putBoolean(KEY_DOWNLOAD_NOTIFICATIONS, false)
            putInt(KEY_PRAYER_NOTIFICATION_TIME, 5)
            putString(KEY_AZKAR_REMINDER_TIME, "06:00")
            putBoolean(KEY_NIGHT_MODE, false)
            putBoolean(KEY_DYNAMIC_COLOR, true)
            putBoolean(KEY_OFFLINE_MODE, false)
            putBoolean(KEY_AUTO_DOWNLOAD, false)
            putInt(KEY_LAST_READ_SURAH, 1)
            putInt(KEY_LAST_READ_PAGE, 1)
            putLong(KEY_LAST_READING_TIMESTAMP, 0L)
            putString(KEY_NEXT_PRAYER_NAME, "الصلاة القادمة")
            putString(KEY_NEXT_PRAYER_TIME, "--:--")
            putString(KEY_LAST_PRAYER_LATITUDE, DEFAULT_PRAYER_LATITUDE)
            putString(KEY_LAST_PRAYER_LONGITUDE, DEFAULT_PRAYER_LONGITUDE)
            apply()
        }
    }
}
