package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class FatwaModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("preview")
    val preview: String,
    @SerializedName("source")
    val source: String = "",
    @SerializedName("mufti")
    val mufti: String = "",
    @SerializedName("date")
    val date: String = ""
)

enum class FatwaCategory(val arabicName: String, val englishName: String) {
    AQIDAH("العقيدة", "Creed"),
    IBADAH("العبادات", "Worship"),
    MUAMALAT("المعاملات", "Transactions"),
    FAMILY("الأحوال الشخصية", "Family Law"),
    FOOD("الطعام والشراب", "Food and Drink"),
    CLOTHING("الملبس والزينة", "Clothing and Adornment"),
    SOCIAL("القضايا الاجتماعية", "Social Issues"),
    HEALTH("الصحة والطب", "Health and Medicine"),
    TECHNOLOGY("التكنولوجيا والمعاملات الحديثة", "Technology"),
    QURAN("القرآن الكريم", "Quran"),
    HADITH("الحديث الشريف", "Hadith")
}

data class ServerFatwa(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String? = null,
    @SerializedName("preview") val preview: String? = null,
    @SerializedName("question") val question: String? = null,
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("source") val source: String? = null
) {
    val sourceURL: String get() = "https://binbaz.org.sa/fatwas/$id"
}

data class ServerFatwaListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<ServerFatwa>? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("pages") val pages: Int? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null
)
