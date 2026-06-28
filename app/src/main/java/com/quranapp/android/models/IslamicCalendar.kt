package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class HijriDate(
    @SerializedName("day")
    val day: Int,
    @SerializedName("month")
    val month: Int,
    @SerializedName("year")
    val year: Int,
    @SerializedName("monthName")
    val monthName: String = ""
) {
    companion object {
        fun getMonthName(month: Int): String = when (month) {
            1 -> "محرّم"
            2 -> "صفر"
            3 -> "ربيع الأول"
            4 -> "ربيع الثاني"
            5 -> "جمادى الأول"
            6 -> "جمادى الآخرة"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذو القعدة"
            12 -> "ذو الحجة"
            else -> "Unknown"
        }

        fun getMonthNameEnglish(month: Int): String = when (month) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabi' al-awwal"
            4 -> "Rabi' al-thani"
            5 -> "Jumada al-awwal"
            6 -> "Jumada al-thani"
            7 -> "Rajab"
            8 -> "Sha'ban"
            9 -> "Ramadan"
            10 -> "Shawwal"
            11 -> "Dhu al-Qi'dah"
            12 -> "Dhu al-Hijjah"
            else -> "Unknown"
        }
    }
}

data class IslamicEvent(
    @SerializedName("name")
    val name: String,
    @SerializedName("nameEn")
    val nameEn: String,
    @SerializedName("month")
    val month: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("description")
    val description: String = ""
) {
    companion object {
        fun getAllEvents(): List<IslamicEvent> = listOf(
            IslamicEvent("الهجرة النبوية", "Hijra", 1, 1, "The migration of Prophet Muhammad from Mecca to Medina"),
            IslamicEvent("رأس السنة الهجرية", "Islamic New Year", 1, 1, "Islamic New Year"),
            IslamicEvent("مولد النبي محمد", "Mawlid al-Nabi", 3, 12, "The birthday of Prophet Muhammad"),
            IslamicEvent("ليلة الإسراء والمعراج", "Isra and Mi'raj", 7, 27, "The night journey and ascension of Prophet Muhammad"),
            IslamicEvent("بداية شهر رمضان", "Start of Ramadan", 9, 1, "The beginning of the holy month of Ramadan"),
            IslamicEvent("ليلة القدر", "Laylat al-Qadr", 9, 27, "The Night of Power, one of the last nights of Ramadan"),
            IslamicEvent("عيد الفطر", "Eid al-Fitr", 10, 1, "Festival of Breaking the Fast"),
            IslamicEvent("يوم عرفة", "Day of Arafah", 12, 9, "The day before Eid al-Adha"),
            IslamicEvent("عيد الأضحى", "Eid al-Adha", 12, 10, "Festival of Sacrifice"),
            IslamicEvent("أيام التشريق", "Days of Tashriq", 12, 11, "The days of Tashriq")
        )

        fun getEventByDate(month: Int, day: Int): IslamicEvent? {
            return getAllEvents().find { it.month == month && it.day == day }
        }

        fun getEventsByMonth(month: Int): List<IslamicEvent> {
            return getAllEvents().filter { it.month == month }
        }
    }
}

data class IslamicCalendarResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: HijriDate
)
