package com.quranapp.android.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

enum class PrayerName(val arabicName: String, val englishName: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha")
}

data class PrayerTime(
    @SerializedName("name")
    val name: PrayerName,
    @SerializedName("time")
    val time: String,
    @SerializedName("iconName")
    val iconName: String
)

data class SavedCity(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("country")
    val country: String = "",
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("timezone")
    val timezone: String = "",
    @SerializedName("date")
    val date: String,
    @SerializedName("prayerTimes")
    val prayerTimes: Map<String, String>
) {
    companion object {
        fun create(
            name: String,
            date: LocalDate = LocalDate.now(),
            prayerTimes: Map<PrayerName, String> = emptyMap()
        ): SavedCity {
            val timesMap = prayerTimes.mapKeys { it.key.name }
            return SavedCity(name = name, date = date.toString(), prayerTimes = timesMap)
        }
    }

    fun getPrayerTime(prayerName: PrayerName): String? = prayerTimes[prayerName.name]

    fun getPrayerTime(prayerNameString: String): String? = prayerTimes[prayerNameString]

    fun getAllPrayerTimes(): Map<PrayerName, String> {
        return prayerTimes.mapKeys { (key, _) ->
            PrayerName.valueOf(key)
        }
    }
}

data class PrayerResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: PrayerData
)

data class PrayerData(
    @SerializedName("timings")
    val timings: Map<String, String>,
    @SerializedName("date")
    val date: PrayerDateInfo,
    @SerializedName("meta")
    val meta: PrayerMeta
)

data class PrayerDateInfo(
    @SerializedName("readable")
    val readable: String,
    @SerializedName("timestamp")
    val timestamp: String
)

data class PrayerMeta(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("method")
    val method: MethodInfo
)

data class MethodInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

/**
 * API response: { "data": { "date": "...", "hijri": {...}, "prayers": { "fajr": {"name_ar":"...", "time":"04:29"}, ... } } }
 */
data class ServerPrayerTimes(
    @SerializedName("date") val date: String? = null,
    @SerializedName("hijri") val hijri: ServerHijriDate? = null,
    @SerializedName("prayers") val prayers: ServerPrayers? = null,
    @SerializedName("method") val method: Int? = null,
    @SerializedName("timezone") val timezone: Int? = null,
    // Backward-compatible flat fields (for Aladhan fallback)
    @SerializedName("fajr") val fajr: String? = null,
    @SerializedName("sunrise") val sunrise: String? = null,
    @SerializedName("dhuhr") val dhuhr: String? = null,
    @SerializedName("asr") val asr: String? = null,
    @SerializedName("maghrib") val maghrib: String? = null,
    @SerializedName("isha") val isha: String? = null
) {
    /** Get prayer time string - tries nested prayers first, then flat fields */
    fun getFajrTime(): String? = prayers?.fajr?.time ?: fajr
    fun getSunriseTime(): String? = prayers?.sunrise?.time ?: sunrise
    fun getDhuhrTime(): String? = prayers?.dhuhr?.time ?: dhuhr
    fun getAsrTime(): String? = prayers?.asr?.time ?: asr
    fun getMaghribTime(): String? = prayers?.maghrib?.time ?: maghrib
    fun getIshaTime(): String? = prayers?.isha?.time ?: isha
}

data class ServerPrayers(
    @SerializedName("fajr") val fajr: ServerPrayerEntry? = null,
    @SerializedName("sunrise") val sunrise: ServerPrayerEntry? = null,
    @SerializedName("dhuhr") val dhuhr: ServerPrayerEntry? = null,
    @SerializedName("asr") val asr: ServerPrayerEntry? = null,
    @SerializedName("maghrib") val maghrib: ServerPrayerEntry? = null,
    @SerializedName("isha") val isha: ServerPrayerEntry? = null
)

data class ServerPrayerEntry(
    @SerializedName("name_ar") val nameAr: String? = null,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("time") val time: String? = null
)

data class ServerHijriDate(
    @SerializedName("year") val year: Int? = null,
    @SerializedName("month") val month: Int? = null,
    @SerializedName("day") val day: Int? = null,
    @SerializedName("month_name_ar") val monthNameAr: String? = null,
    @SerializedName("formatted") val formatted: String? = null
)

data class ServerPrayerMethod(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("is_default") val isDefault: Int = 0
)
