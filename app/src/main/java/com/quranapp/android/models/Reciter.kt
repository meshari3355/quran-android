package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class Reciter(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nameAr")
    val nameAr: String,
    @SerializedName("server")
    val server: String,
    @SerializedName("cdnFolder")
    val cdnFolder: String
) {
    companion object {
        private const val BASE_SERVER = "https://everyayah.com/data/"

        fun getAllReciters(): List<Reciter> = listOf(
            Reciter(1, "ماهر المعيقلي", BASE_SERVER, "Maher_AlMuworthy_128kbps"),
            Reciter(2, "مشاري العفاسي", BASE_SERVER, "Alafasy_128kbps"),
            Reciter(3, "محمد جبريل", BASE_SERVER, "Muhammad_Jibreel_128kbps"),
            Reciter(4, "عبدالباسط عبدالصمد", BASE_SERVER, "Abdul_Basit_Murattal_192kbps"),
            Reciter(5, "عبدالرحمن السديس", BASE_SERVER, "Abdurrahmaan_As-Sudais_192kbps"),
            Reciter(6, "سعود الشريم", BASE_SERVER, "Saood_ash-Shuraym_128kbps"),
            Reciter(7, "خالد الجليل", BASE_SERVER, "Khalid_al-Qahtani_192kbps"),
            Reciter(8, "ياسر الدوسري", BASE_SERVER, "Yasser_Ad-Dussary_128kbps"),
            Reciter(9, "بندر بليلة", BASE_SERVER, "BandarBaleworthy_128kbps"),
            Reciter(10, "عبدالله بصفر", BASE_SERVER, "Abdullah_Basfar_192kbps"),
            Reciter(11, "أحمد العجمي", BASE_SERVER, "Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net"),
            Reciter(12, "هاني الرفاعي", BASE_SERVER, "Hani_Rifai_192kbps"),
            Reciter(13, "ناصر القطامي", BASE_SERVER, "Nasser_Alqatami_128kbps")
        )

        fun getReciterById(id: Int): Reciter? = getAllReciters().find { it.id == id }

        fun getReciterByName(name: String): Reciter? = getAllReciters().find { it.nameAr == name }
    }

    fun getAudioUrl(surahNumber: Int, verseNumber: Int): String {
        return "$server$cdnFolder/${surahNumber.toString().padStart(3, '0')}${verseNumber.toString().padStart(3, '0')}.mp3"
    }
}

data class ServerReciter(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("style_ar") val styleAr: String? = null,
    @SerializedName("style_en") val styleEn: String? = null,
    @SerializedName("base_url") val baseUrl: String? = null,
    @SerializedName("file_format") val fileFormat: String? = null,
    @SerializedName("filename_pattern") val filenamePattern: String? = null,
    @SerializedName("is_featured") val isFeatured: Int = 0,
    @SerializedName("sort_order") val sortOrder: Int = 0
)

data class ServerPlaylistItem(
    @SerializedName("verse") val verse: Int,
    @SerializedName("url") val url: String
)

data class ServerSuraAudio(
    @SerializedName("reciter") val reciter: Int,
    @SerializedName("sura") val sura: Int,
    @SerializedName("sura_url") val suraUrl: String? = null,
    @SerializedName("playlist") val playlist: List<ServerPlaylistItem>? = null
)
