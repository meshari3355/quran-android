package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class Surah(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nameAr")
    val nameAr: String,
    @SerializedName("nameEn")
    val nameEn: String,
    @SerializedName("versesCount")
    val versesCount: Int,
    @SerializedName("pageNumber")
    val pageNumber: Int,
    @SerializedName("type")
    val type: String // "مكية" or "مدنية"
) {
    companion object {
        fun getAllSurahs(): List<Surah> = listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", 7, 1, "مكية"),
            Surah(2, "البقرة", "Al-Baqarah", 286, 2, "مدنية"),
            Surah(3, "آل عمران", "Aal-Imran", 200, 50, "مدنية"),
            Surah(4, "النساء", "An-Nisa", 176, 77, "مدنية"),
            Surah(5, "المائدة", "Al-Ma'idah", 120, 106, "مدنية"),
            Surah(6, "الأنعام", "Al-An'am", 165, 128, "مكية"),
            Surah(7, "الأعراف", "Al-A'raf", 206, 151, "مكية"),
            Surah(8, "الأنفال", "Al-Anfal", 75, 177, "مدنية"),
            Surah(9, "التوبة", "At-Tawbah", 129, 187, "مدنية"),
            Surah(10, "يونس", "Yunus", 109, 208, "مكية"),
            Surah(11, "هود", "Hud", 123, 221, "مكية"),
            Surah(12, "يوسف", "Yusuf", 111, 235, "مكية"),
            Surah(13, "الرعد", "Ar-Ra'd", 43, 249, "مدنية"),
            Surah(14, "إبراهيم", "Ibrahim", 52, 255, "مكية"),
            Surah(15, "الحجر", "Al-Hijr", 99, 262, "مكية"),
            Surah(16, "النحل", "An-Nahl", 128, 267, "مكية"),
            Surah(17, "الإسراء", "Al-Isra", 111, 282, "مكية"),
            Surah(18, "الكهف", "Al-Kahf", 110, 293, "مكية"),
            Surah(19, "مريم", "Maryam", 98, 305, "مكية"),
            Surah(20, "طه", "Taha", 135, 312, "مكية"),
            Surah(21, "الأنبياء", "Al-Anbiya", 112, 322, "مكية"),
            Surah(22, "الحج", "Al-Hajj", 78, 332, "مدنية"),
            Surah(23, "المؤمنون", "Al-Mu'minun", 118, 342, "مكية"),
            Surah(24, "النور", "An-Nur", 64, 350, "مدنية"),
            Surah(25, "الفرقان", "Al-Furqan", 77, 359, "مكية"),
            Surah(26, "الشعراء", "Ash-Shu'ara", 227, 367, "مكية"),
            Surah(27, "النمل", "An-Naml", 93, 377, "مكية"),
            Surah(28, "القصص", "Al-Qasas", 88, 385, "مكية"),
            Surah(29, "العنكبوت", "Al-Ankabut", 69, 396, "مكية"),
            Surah(30, "الروم", "Ar-Rum", 60, 404, "مكية"),
            Surah(31, "لقمان", "Luqman", 34, 411, "مكية"),
            Surah(32, "السجدة", "As-Sajdah", 30, 415, "مكية"),
            Surah(33, "الأحزاب", "Al-Ahzab", 73, 418, "مدنية"),
            Surah(34, "سبأ", "Saba", 54, 428, "مكية"),
            Surah(35, "فاطر", "Fatir", 45, 434, "مكية"),
            Surah(36, "يس", "Ya-Sin", 83, 440, "مكية"),
            Surah(37, "الصافات", "As-Saffat", 182, 446, "مكية"),
            Surah(38, "ص", "Sad", 88, 453, "مكية"),
            Surah(39, "الزمر", "Az-Zumar", 75, 458, "مكية"),
            Surah(40, "غافر", "Ghafir", 85, 467, "مكية"),
            Surah(41, "فصلت", "Fussilat", 54, 477, "مكية"),
            Surah(42, "الشورى", "Ash-Shura", 53, 483, "مكية"),
            Surah(43, "الزخرف", "Az-Zukhruf", 89, 489, "مكية"),
            Surah(44, "الدخان", "Ad-Dukhan", 59, 496, "مكية"),
            Surah(45, "الجاثية", "Al-Jathiyah", 37, 499, "مكية"),
            Surah(46, "الأحقاف", "Al-Ahqaf", 35, 502, "مكية"),
            Surah(47, "محمد", "Muhammad", 38, 507, "مدنية"),
            Surah(48, "الفتح", "Al-Fath", 29, 511, "مدنية"),
            Surah(49, "الحجرات", "Al-Hujurat", 18, 515, "مدنية"),
            Surah(50, "ق", "Qaf", 45, 518, "مكية"),
            Surah(51, "الذاريات", "Adh-Dhariyat", 60, 520, "مكية"),
            Surah(52, "الطور", "At-Tur", 49, 523, "مكية"),
            Surah(53, "النجم", "An-Najm", 62, 526, "مكية"),
            Surah(54, "القمر", "Al-Qamar", 55, 528, "مكية"),
            Surah(55, "الرحمن", "Ar-Rahman", 78, 531, "مدنية"),
            Surah(56, "الواقعة", "Al-Waqi'ah", 96, 534, "مكية"),
            Surah(57, "الحديد", "Al-Hadid", 29, 537, "مدنية"),
            Surah(58, "المجادلة", "Al-Mujadila", 22, 542, "مدنية"),
            Surah(59, "الحشر", "Al-Hashr", 24, 545, "مدنية"),
            Surah(60, "الممتحنة", "Al-Mumtahanah", 13, 549, "مدنية"),
            Surah(61, "الصف", "As-Saff", 14, 551, "مدنية"),
            Surah(62, "الجمعة", "Al-Jumu'ah", 11, 553, "مدنية"),
            Surah(63, "المنافقون", "Al-Munafiqun", 11, 554, "مدنية"),
            Surah(64, "التغابن", "At-Taghabun", 18, 556, "مدنية"),
            Surah(65, "الطلاق", "At-Talaq", 12, 558, "مدنية"),
            Surah(66, "التحريم", "At-Tahrim", 12, 560, "مدنية"),
            Surah(67, "الملك", "Al-Mulk", 30, 562, "مكية"),
            Surah(68, "القلم", "Al-Qalam", 52, 564, "مكية"),
            Surah(69, "الحاقة", "Al-Haqqah", 52, 566, "مكية"),
            Surah(70, "المعارج", "Al-Ma'arij", 44, 568, "مكية"),
            Surah(71, "نوح", "Nuh", 28, 570, "مكية"),
            Surah(72, "الجن", "Al-Jinn", 28, 572, "مكية"),
            Surah(73, "المزمل", "Al-Muzzammil", 20, 574, "مكية"),
            Surah(74, "المدثر", "Al-Muddaththir", 56, 575, "مكية"),
            Surah(75, "القيامة", "Al-Qiyamah", 40, 577, "مكية"),
            Surah(76, "الإنسان", "Al-Insan", 31, 578, "مدنية"),
            Surah(77, "المرسلات", "Al-Mursalat", 50, 580, "مكية"),
            Surah(78, "النبأ", "An-Naba", 40, 582, "مكية"),
            Surah(79, "النازعات", "An-Nazi'at", 46, 583, "مكية"),
            Surah(80, "عبس", "Abasa", 42, 585, "مكية"),
            Surah(81, "التكوير", "At-Takwir", 29, 586, "مكية"),
            Surah(82, "الانفطار", "Al-Infitar", 19, 587, "مكية"),
            Surah(83, "المطففين", "Al-Mutaffifin", 36, 587, "مكية"),
            Surah(84, "الانشقاق", "Al-Inshiqaq", 25, 589, "مكية"),
            Surah(85, "البروج", "Al-Buruj", 22, 590, "مكية"),
            Surah(86, "الطارق", "At-Tariq", 17, 591, "مكية"),
            Surah(87, "الأعلى", "Al-A'la", 19, 591, "مكية"),
            Surah(88, "الغاشية", "Al-Ghashiyah", 26, 592, "مكية"),
            Surah(89, "الفجر", "Al-Fajr", 30, 593, "مكية"),
            Surah(90, "البلد", "Al-Balad", 20, 594, "مكية"),
            Surah(91, "الشمس", "Ash-Shams", 15, 595, "مكية"),
            Surah(92, "الليل", "Al-Layl", 21, 595, "مكية"),
            Surah(93, "الضحى", "Ad-Duha", 11, 596, "مكية"),
            Surah(94, "الشرح", "Ash-Sharh", 8, 596, "مكية"),
            Surah(95, "التين", "At-Tin", 8, 597, "مكية"),
            Surah(96, "العلق", "Al-Alaq", 19, 597, "مكية"),
            Surah(97, "القدر", "Al-Qadr", 5, 598, "مكية"),
            Surah(98, "البينة", "Al-Bayyinah", 8, 598, "مدنية"),
            Surah(99, "الزلزلة", "Az-Zalzalah", 8, 599, "مدنية"),
            Surah(100, "العاديات", "Al-Adiyat", 11, 599, "مكية"),
            Surah(101, "القارعة", "Al-Qari'ah", 11, 600, "مكية"),
            Surah(102, "التكاثر", "At-Takathur", 8, 600, "مكية"),
            Surah(103, "العصر", "Al-Asr", 3, 601, "مكية"),
            Surah(104, "الهمزة", "Al-Humazah", 9, 601, "مكية"),
            Surah(105, "الفيل", "Al-Fil", 5, 601, "مكية"),
            Surah(106, "قريش", "Quraysh", 4, 602, "مكية"),
            Surah(107, "الماعون", "Al-Ma'un", 7, 602, "مكية"),
            Surah(108, "الكوثر", "Al-Kawthar", 3, 602, "مكية"),
            Surah(109, "الكافرون", "Al-Kafirun", 6, 603, "مكية"),
            Surah(110, "النصر", "An-Nasr", 3, 603, "مدنية"),
            Surah(111, "المسد", "Al-Masad", 5, 603, "مكية"),
            Surah(112, "الإخلاص", "Al-Ikhlas", 4, 604, "مكية"),
            Surah(113, "الفلق", "Al-Falaq", 5, 604, "مكية"),
            Surah(114, "الناس", "An-Nas", 6, 604, "مكية")
        )

        fun getSurahById(id: Int): Surah? = getAllSurahs().find { it.id == id }
    }
}

// Server response types
data class ServerSura(
    @SerializedName("id") val id: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("name_transliteration") val nameTransliteration: String? = null,
    @SerializedName("verses_count") val versesCount: Int,
    @SerializedName("revelation_type") val revelationType: String? = null,
    @SerializedName("pages_start") val pagesStart: Int? = null,
    @SerializedName("juz_start") val juzStart: Int? = null
)

data class ServerVerse(
    @SerializedName("id") val id: Int,
    @SerializedName("sura_id") val suraId: Int,
    @SerializedName("verse_number") val verseNumber: Int,
    @SerializedName("text_uthmani") val textUthmani: String,
    @SerializedName("text_simple") val textSimple: String? = null,
    @SerializedName("juz") val juz: Int? = null,
    @SerializedName("hizb") val hizb: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("sajda") val sajda: Int? = null,
    @SerializedName("sura_name_ar") val suraNameAr: String? = null,
    @SerializedName("sura_name_en") val suraNameEn: String? = null,
    @SerializedName("translations") val translations: List<ServerTranslation>? = null
)

data class ServerTranslation(
    @SerializedName("translator_id") val translatorId: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("text") val text: String
)

data class ServerSearchVerse(
    @SerializedName("id") val id: Int,
    @SerializedName("sura_id") val suraId: Int,
    @SerializedName("verse_number") val verseNumber: Int,
    @SerializedName("text_uthmani") val textUthmani: String,
    @SerializedName("sura_name_ar") val suraNameAr: String? = null,
    @SerializedName("sura_name_en") val suraNameEn: String? = null
)

// Generic server response wrappers
data class ServerResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") val message: String? = null
)

data class ServerListResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<T>? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("pages") val pages: Int? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null,
    @SerializedName("message") val message: String? = null
)
