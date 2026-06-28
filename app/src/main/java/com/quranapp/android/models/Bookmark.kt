package com.quranapp.android.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class BookmarkModel(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    @SerializedName("surahName")
    val surahName: String,
    @SerializedName("surahId")
    val surahId: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("verseNumber")
    val verseNumber: Int = 0,
    @SerializedName("notes")
    val notes: String = ""
) {
    companion object {
        fun create(
            surahName: String,
            surahId: Int,
            page: Int,
            verseNumber: Int = 0,
            notes: String = ""
        ): BookmarkModel {
            return BookmarkModel(
                id = UUID.randomUUID().toString(),
                surahName = surahName,
                surahId = surahId,
                page = page,
                timestamp = System.currentTimeMillis(),
                verseNumber = verseNumber,
                notes = notes
            )
        }
    }
}
