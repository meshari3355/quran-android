package com.quranapp.android.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*

// ===== Language Enum =====

enum class Language(val code: String, val displayName: String, val displayNameAr: String) {
    ARABIC("ar", "العربية", "العربية"),
    ENGLISH("en", "English", "الإنجليزية")
}

// ===== DataStore Extension =====

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

// ===== Language Manager =====

class LanguageManager(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        private const val DEFAULT_LANGUAGE = "ar"
    }

    // ===== Language Selection =====

    suspend fun setLanguage(language: Language): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStore.edit { preferences ->
                preferences[LANGUAGE_KEY] = language.code
            }
            Unit
        }
    }

    fun getLanguageFlow(): Flow<Language> {
        return dataStore.data.map { preferences ->
            val languageCode = preferences[LANGUAGE_KEY] ?: DEFAULT_LANGUAGE
            Language.values().find { it.code == languageCode } ?: Language.ARABIC
        }
    }

    suspend fun getCurrentLanguage(): Language = withContext(Dispatchers.IO) {
        try {
            val prefs = dataStore.data.firstOrNull()
            val languageCode = prefs?.get(LANGUAGE_KEY) ?: DEFAULT_LANGUAGE
            Language.entries.find { it.code == languageCode } ?: Language.ARABIC
        } catch (_: Exception) {
            Language.ARABIC
        }
    }

    suspend fun getLanguageSync(): Language = withContext(Dispatchers.IO) {
        try {
            var selectedLanguage = Language.ARABIC
            // This would normally require runBlocking or a callback
            // For now, return the system default or stored preference
            selectedLanguage
        } catch (e: Exception) {
            Language.ARABIC
        }
    }

    // ===== Localization =====

    fun getLocalizedString(key: String, language: Language = Language.ARABIC): String {
        return when (key) {
            // Common UI strings
            "app_name" -> if (language == Language.ARABIC) "تطبيق القرآن" else "Quran App"
            "home" -> if (language == Language.ARABIC) "الرئيسية" else "Home"
            "quran" -> if (language == Language.ARABIC) "القرآن الكريم" else "Quran"
            "prayer_times" -> if (language == Language.ARABIC) "أوقات الصلاة" else "Prayer Times"
            "qibla" -> if (language == Language.ARABIC) "القبلة" else "Qibla"
            "zakat" -> if (language == Language.ARABIC) "الزكاة" else "Zakat"
            "hadiths" -> if (language == Language.ARABIC) "الأحاديث" else "Hadiths"
            "calendar" -> if (language == Language.ARABIC) "التقويم الهجري" else "Islamic Calendar"
            "settings" -> if (language == Language.ARABIC) "الإعدادات" else "Settings"
            "about" -> if (language == Language.ARABIC) "عن التطبيق" else "About"

            // Prayer names
            "fajr" -> if (language == Language.ARABIC) "الفجر" else "Fajr"
            "sunrise" -> if (language == Language.ARABIC) "الشروق" else "Sunrise"
            "dhuhr" -> if (language == Language.ARABIC) "الظهر" else "Dhuhr"
            "asr" -> if (language == Language.ARABIC) "العصر" else "Asr"
            "maghrib" -> if (language == Language.ARABIC) "المغرب" else "Maghrib"
            "isha" -> if (language == Language.ARABIC) "العشاء" else "Isha"

            // Action buttons
            "ok" -> if (language == Language.ARABIC) "حسناً" else "OK"
            "cancel" -> if (language == Language.ARABIC) "إلغاء" else "Cancel"
            "delete" -> if (language == Language.ARABIC) "حذف" else "Delete"
            "save" -> if (language == Language.ARABIC) "حفظ" else "Save"
            "download" -> if (language == Language.ARABIC) "تحميل" else "Download"
            "search" -> if (language == Language.ARABIC) "بحث" else "Search"
            "share" -> if (language == Language.ARABIC) "مشاركة" else "Share"

            // Errors
            "error" -> if (language == Language.ARABIC) "خطأ" else "Error"
            "error_network" -> if (language == Language.ARABIC) "خطأ في الاتصال بالإنترنت" else "Network error"
            "error_empty" -> if (language == Language.ARABIC) "لا توجد بيانات" else "No data available"

            else -> key
        }
    }

    fun t(ar: String, en: String, language: Language = Language.ARABIC): String {
        return when (language) {
            Language.ARABIC -> ar
            Language.ENGLISH -> en
        }
    }

    // ===== Localized Collections =====

    fun getPrayerNames(language: Language = Language.ARABIC): Map<String, String> {
        return mapOf(
            "Fajr" to getLocalizedString("fajr", language),
            "Sunrise" to getLocalizedString("sunrise", language),
            "Dhuhr" to getLocalizedString("dhuhr", language),
            "Asr" to getLocalizedString("asr", language),
            "Maghrib" to getLocalizedString("maghrib", language),
            "Isha" to getLocalizedString("isha", language)
        )
    }

    fun getMenuItems(language: Language = Language.ARABIC): List<Pair<String, String>> {
        return listOf(
            getLocalizedString("quran", language) to "quran",
            getLocalizedString("prayer_times", language) to "prayer_times",
            getLocalizedString("qibla", language) to "qibla",
            getLocalizedString("zakat", language) to "zakat",
            getLocalizedString("hadiths", language) to "hadiths",
            getLocalizedString("calendar", language) to "calendar",
            getLocalizedString("settings", language) to "settings",
            getLocalizedString("about", language) to "about"
        )
    }

    // ===== Language Information =====

    fun getAvailableLanguages(): List<Language> {
        return Language.values().toList()
    }

    fun getLanguageByCode(code: String): Language? {
        return Language.values().find { it.code == code }
    }

    fun isRTL(language: Language = Language.ARABIC): Boolean {
        return language == Language.ARABIC
    }

    fun getCurrentLocale(language: Language = Language.ARABIC): Locale {
        return Locale(language.code)
    }

    // ===== System Language Detection =====

    fun getSystemLanguage(): Language {
        val systemLocale = Locale.getDefault().language
        return Language.values().find { it.code == systemLocale } ?: Language.ARABIC
    }

    fun shouldUseSystemLanguage(language: Language): Boolean {
        return language == getSystemLanguage()
    }

    // ===== String Formatting =====

    fun formatNumber(number: Int, language: Language = Language.ARABIC): String {
        return if (language == Language.ARABIC) {
            convertToArabicNumerals(number.toString())
        } else {
            number.toString()
        }
    }

    fun formatDouble(value: Double, decimals: Int = 2, language: Language = Language.ARABIC): String {
        val formatted = String.format("%.${decimals}f", value)
        return if (language == Language.ARABIC) {
            convertToArabicNumerals(formatted)
        } else {
            formatted
        }
    }

    private fun convertToArabicNumerals(input: String): String {
        val arabicNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumerals = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        var result = input
        for (i in englishNumerals.indices) {
            result = result.replace(englishNumerals[i], arabicNumerals[i])
        }
        return result
    }

    fun convertFromArabicNumerals(input: String): String {
        val arabicNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumerals = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        var result = input
        for (i in arabicNumerals.indices) {
            result = result.replace(arabicNumerals[i], englishNumerals[i])
        }
        return result
    }

    // ===== Language Persistence =====

    suspend fun resetToDefaultLanguage(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            setLanguage(Language.ARABIC).getOrThrow()
        }
    }

    suspend fun exportLanguagePreferences(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val prefs = dataStore.data.map { it }.toString()
            prefs
        }
    }
}
