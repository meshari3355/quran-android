package com.quranapp.android.services

import java.util.*
import com.quranapp.android.models.HijriDate as HijriDateModel

// ===== Hijri Date Models for internal service use =====

// Service-specific data classes that extend model functionality
data class HijriDateInfo(
    val year: Int,
    val month: Int,
    val day: Int
) {
    fun toFormattedString(language: String = "ar"): String {
        val monthNames = if (language == "ar") {
            arrayOf("محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الآخرة",
                "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة")
        } else {
            arrayOf("Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani", "Jumada al-Ula",
                "Jumada al-Akhirah", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah")
        }
        val monthName = if (month in 1..12) monthNames[month - 1] else ""
        return "$day $monthName $year"
    }

    override fun toString(): String {
        return "$day/$month/$year"
    }

    companion object {
        fun fromModel(hijriDate: HijriDateModel): HijriDateInfo {
            return HijriDateInfo(hijriDate.year, hijriDate.month, hijriDate.day)
        }
    }
}

data class IslamicEventService(
    val name: String,
    val nameAr: String,
    val hijriMonth: Int,
    val hijriDay: Int,
    val description: String? = null,
    val descriptionAr: String? = null
)

// ===== Islamic Calendar Service =====

class IslamicCalendarService {

    companion object {
        // Months in Islamic calendar
        private val HIJRI_MONTHS_AR = arrayOf(
            "محرم",
            "صفر",
            "ربيع الأول",
            "ربيع الثاني",
            "جمادى الأولى",
            "جمادى الآخرة",
            "رجب",
            "شعبان",
            "رمضان",
            "شوال",
            "ذو القعدة",
            "ذو الحجة"
        )

        private val HIJRI_MONTHS_EN = arrayOf(
            "Muharram",
            "Safar",
            "Rabi' al-awwal",
            "Rabi' al-thani",
            "Jumada al-awwal",
            "Jumada al-thani",
            "Rajab",
            "Sha'ban",
            "Ramadan",
            "Shawwal",
            "Dhu al-Qi'dah",
            "Dhu al-Hijjah"
        )

        // Gregorian calendar adjustment
        private const val GREGORIAN_EPOCH = 1721425.5
        private const val ISLAMIC_EPOCH = 1948439.5
    }

    // ===== Hijri Date Conversion =====

    fun gregorianToHijri(date: Date = Date()): HijriDateInfo {
        val calendar = Calendar.getInstance()
        calendar.time = date

        return gregorianToHijri(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun gregorianToHijri(gregorianYear: Int, gregorianMonth: Int, gregorianDay: Int): HijriDateInfo {
        // Adjust for January and February
        val a = (14 - gregorianMonth) / 12
        val yg = gregorianYear + 4800 - a
        val m = gregorianMonth + 12 * a - 3

        // Calculate Julian day number
        val jdn = gregorianDay + (153 * m + 2) / 5 + 365 * yg + yg / 4 - yg / 100 + yg / 400 - 32045

        // Convert to Islamic calendar
        val l = jdn - 1948440
        val n = (30 * l + 10646) / 10646
        val j = jdn + 1 - (11 * n + 3) / 30
        val r = ((j - 1) % 30) + 1
        val w = (j - 1) / 354 + 1
        val yh = (30 * w + r - 1) / 10631

        val hijriYear = (11 * yh + 3) / 30 + 1
        val hijriMonth = ((11 * ((j - 1) % 354 + 1) + 3) / 325) % 12 + 1
        val hijriDay = ((j - 1) % 354 + 1) - ((hijriMonth - 1) * 30 + hijriMonth / 2)

        return HijriDateInfo(hijriYear, hijriMonth, hijriDay)
    }

    fun hijriToGregorian(hijriYear: Int, hijriMonth: Int, hijriDay: Int): Date {
        // Calculate Julian day number from Islamic date
        val jdn = hijriDay + 29 * (hijriMonth - 1) + (hijriMonth - 1) / 11 +
                (hijriYear - 1) * 354 + (3 + 11 * hijriYear) / 30 + 1948440

        // Convert JDN to Gregorian
        val l = jdn + 68569
        val n = (4 * l) / 146097
        val l2 = l - (146097 * n + 3) / 4
        val i = (4000 * (l2 + 1)) / 1461001

        val l3 = l2 - (1461 * i) / 4 + 31
        val j = (80 * l3) / 2447
        val day = l3 - (2447 * j) / 80
        val l4 = j / 11
        val month = j + 2 - 12 * l4
        val year = 100 * (n - 49) + i + l4

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day.toInt())
        return calendar.time
    }

    // ===== Month Information =====

    fun getMonthName(month: Int, language: String = "ar"): String {
        if (month < 1 || month > 12) return ""
        return if (language == "ar") {
            HIJRI_MONTHS_AR[month - 1]
        } else {
            HIJRI_MONTHS_EN[month - 1]
        }
    }

    fun getAllMonths(language: String = "ar"): List<String> {
        return if (language == "ar") {
            HIJRI_MONTHS_AR.toList()
        } else {
            HIJRI_MONTHS_EN.toList()
        }
    }

    // ===== Islamic Events =====

    fun getIslamicEvents(): List<IslamicEventService> {
        return listOf(
            // Muharram
            IslamicEventService(
                "Islamic New Year",
                "رأس السنة الهجرية",
                1, 1,
                "First day of Muharram marks the start of the Islamic calendar year.",
                "أول يوم من محرم وهو بداية السنة الهجرية"
            ),
            IslamicEventService(
                "Day of Ashura",
                "يوم عاشوراء",
                1, 10,
                "Significant day of fasting and remembrance in Islamic tradition.",
                "يوم مهم في التاريخ الإسلامي ويستحب الصيام فيه"
            ),

            // Rajab
            IslamicEventService(
                "Isra and Mi'raj",
                "الإسراء والمعراج",
                7, 27,
                "Commemoration of Prophet Muhammad's night journey to Jerusalem and ascension to heaven.",
                "ليلة إسراء ومعراج النبي محمد صلى الله عليه وسلم"
            ),

            // Sha'ban
            IslamicEventService(
                "Laylat al-Baraa'h",
                "ليلة البراءة",
                8, 15,
                "Night of forgiveness and freedom from Hell-fire.",
                "ليلة البراءة وهي من الليالي المباركة"
            ),

            // Ramadan
            IslamicEventService(
                "Ramadan Begins",
                "بداية شهر رمضان",
                9, 1,
                "Start of the blessed month of fasting.",
                "بداية الشهر الفضيل شهر الصيام والقيام"
            ),
            IslamicEventService(
                "Laylat al-Qadr",
                "ليلة القدر",
                9, 27,
                "Night of Power - the night when the Quran was first revealed.",
                "ليلة القدر وهي خير من ألف شهر"
            ),

            // Shawwal
            IslamicEventService(
                "Eid al-Fitr",
                "عيد الفطر",
                10, 1,
                "Festival of Breaking the Fast at the end of Ramadan.",
                "عيد الفطر وهو أول أيام شوال"
            ),

            // Dhu al-Hijjah
            IslamicEventService(
                "Hajj Season Begins",
                "بداية موسم الحج",
                12, 8,
                "Beginning of the pilgrimage season to Mecca.",
                "بداية موسم الحج ويوم التروية"
            ),
            IslamicEventService(
                "Day of Arafah",
                "يوم عرفة",
                12, 9,
                "Day of standing at Mount Arafah, the peak of Hajj.",
                "يوم عرفة وهو يوم عظيم الشأن"
            ),
            IslamicEventService(
                "Eid al-Adha",
                "عيد الأضحى",
                12, 10,
                "Festival of Sacrifice celebrating the willingness of Ibrahim to sacrifice his son.",
                "عيد الأضحى المبارك"
            )
        )
    }

    fun getEventForDate(hijriMonth: Int, hijriDay: Int): IslamicEventService? {
        return getIslamicEvents().firstOrNull { event ->
            event.hijriMonth == hijriMonth && event.hijriDay == hijriDay
        }
    }

    fun getEventsInMonth(hijriMonth: Int): List<IslamicEventService> {
        return getIslamicEvents().filter { it.hijriMonth == hijriMonth }
    }

    fun getUpcomingEvents(daysAhead: Int = 30): List<Pair<IslamicEventService, Date>> {
        val today = Date()
        val todayHijri = gregorianToHijri(today)
        val upcomingEvents = mutableListOf<Pair<IslamicEventService, Date>>()

        val calendar = Calendar.getInstance()
        calendar.time = today

        repeat(daysAhead) {
            val currentDate = calendar.time
            val currentHijri = gregorianToHijri(currentDate)
            val event = getEventForDate(currentHijri.month, currentHijri.day)
            if (event != null) {
                upcomingEvents.add(Pair(event, currentDate))
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return upcomingEvents
    }

    // ===== Utility Methods =====

    fun getCurrentHijriDate(): HijriDateInfo {
        return gregorianToHijri(Date())
    }

    fun getDaysInHijriMonth(month: Int, year: Int): Int {
        return if (month % 2 == 1) 30 else 29
    }

    fun getDaysInHijriYear(year: Int): Int {
        return if (isLeapHijriYear(year)) 355 else 354
    }

    fun isLeapHijriYear(year: Int): Boolean {
        return (11 * year + 3) % 30 < 11
    }

    fun getHijriYearsSinceHijra(hijriYear: Int = 1): Int {
        return hijriYear - 1
    }

    fun getApproximateGregorianYear(hijriYear: Int): Int {
        return (hijriYear * 33 + 622) / 32
    }

    fun formatHijriDate(hijriDate: HijriDateInfo, language: String = "ar"): String {
        val monthName = getMonthName(hijriDate.month, language)
        return if (language == "ar") {
            "${hijriDate.day} $monthName ${hijriDate.year}ه"
        } else {
            "${hijriDate.day} $monthName ${hijriDate.year} AH"
        }
    }

    fun getHijriYearRange(gregorianYear: Int): Pair<Int, Int> {
        // Approximate Hijri year at start of Gregorian year
        val janHijri = gregorianToHijri(gregorianYear, 1, 1)
        // Approximate Hijri year at end of Gregorian year
        val decHijri = gregorianToHijri(gregorianYear, 12, 31)
        return Pair(janHijri.year, decHijri.year)
    }
}
