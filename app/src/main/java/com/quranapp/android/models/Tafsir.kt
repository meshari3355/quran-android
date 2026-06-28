package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

enum class TafsirBook(val id: Int, val nameAr: String, val nameEn: String) {
    IBN_KATHIR(169, "تفسير ابن كثير", "Ibn Kathir"),
    SAADI(91, "تفسير السعدي", "Al-Saadi"),
    JALALAYN(74, "تفسير الجلالين", "Al-Jalalayn")
}

data class ServerTafsirAyah(
    @SerializedName("id") val id: Int,
    @SerializedName("verse_key") val verseKey: String? = null,
    @SerializedName("text") val text: String
)

data class ServerTafsirResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<ServerTafsirAyah>? = null,
    @SerializedName("tafsir") val tafsir: String? = null,
    @SerializedName("sura") val sura: Int? = null
)
