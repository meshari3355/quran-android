package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class HadithBook(
    @SerializedName("id")
    val id: String,
    @SerializedName("bookName")
    val bookName: String,
    @SerializedName("hadithCount")
    val hadithCount: Int,
    @SerializedName("chapters")
    val chapters: List<HadithChapter> = emptyList()
)

data class HadithChapter(
    @SerializedName("id")
    val id: String,
    @SerializedName("chapterName")
    val chapterName: String,
    @SerializedName("chapterNumber")
    val chapterNumber: Int,
    @SerializedName("babs")
    val babs: List<HadithBab> = emptyList()
)

data class HadithBab(
    @SerializedName("id")
    val id: String,
    @SerializedName("babName")
    val babName: String,
    @SerializedName("babNumber")
    val babNumber: Int,
    @SerializedName("ahadith")
    val ahadith: List<HadithText> = emptyList()
)

data class HadithText(
    @SerializedName("id")
    val id: String,
    @SerializedName("hadithNumber")
    val hadithNumber: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("narrator")
    val narrator: String = "",
    @SerializedName("chain")
    val chain: String = ""
)

data class HadithPortalCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("categoryNameEn")
    val categoryNameEn: String,
    @SerializedName("books")
    val books: List<HadithBookSummary>
)

data class HadithBookSummary(
    @SerializedName("id")
    val id: String,
    @SerializedName("bookName")
    val bookName: String,
    @SerializedName("bookNameEn")
    val bookNameEn: String,
    @SerializedName("hadithCount")
    val hadithCount: Int
) {
    companion object {
        fun getAllCategories(): List<HadithPortalCategory> = listOf(
            HadithPortalCategory(
                id = "1",
                categoryName = "الصحاح",
                categoryNameEn = "Authentic Collections",
                books = listOf(
                    HadithBookSummary("sahih_bukhari", "صحيح البخاري", "Sahih Bukhari", 7563),
                    HadithBookSummary("sahih_muslim", "صحيح مسلم", "Sahih Muslim", 5362)
                )
            ),
            HadithPortalCategory(
                id = "2",
                categoryName = "السنن",
                categoryNameEn = "Sunan Collections",
                books = listOf(
                    HadithBookSummary("abu_dawud", "سنن أبي داود", "Sunan Abu Dawud", 5274),
                    HadithBookSummary("tirmidhi", "سنن الترمذي", "Sunan At-Tirmidhi", 3956),
                    HadithBookSummary("nasai", "سنن النسائي", "Sunan An-Nasai", 5758),
                    HadithBookSummary("ibn_majah", "سنن ابن ماجه", "Sunan Ibn Majah", 4341)
                )
            ),
            HadithPortalCategory(
                id = "3",
                categoryName = "المسانيد والموطآت",
                categoryNameEn = "Musnad and Muwatta Collections",
                books = listOf(
                    HadithBookSummary("ahmad", "مسند أحمد", "Musnad Ahmad", 27647),
                    HadithBookSummary("malik", "موطأ مالك", "Muwatta Malik", 1858),
                    HadithBookSummary("darimi", "سنن الدارمي", "Sunan Ad-Darimi", 3503)
                )
            ),
            HadithPortalCategory(
                id = "4",
                categoryName = "الأربعون النووية",
                categoryNameEn = "Forty Hadith Nawawi",
                books = listOf(
                    HadithBookSummary("nawawi_40", "الأربعون النووية", "Forty Hadith Nawawi", 42)
                )
            )
        )

        fun getCategoryById(id: String): HadithPortalCategory? = getAllCategories().find { it.id == id }

        fun getBookById(bookId: String): HadithBookSummary? {
            getAllCategories().forEach { category ->
                category.books.find { it.id == bookId }?.let { return it }
            }
            return null
        }
    }
}

data class ServerHadithCollection(
    @SerializedName("id") val id: String,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("author_ar") val authorAr: String? = null,
    @SerializedName("total_hadiths") val totalHadiths: Int? = null,
    @SerializedName("is_available") val isAvailable: Int = 0,
    @SerializedName("sort_order") val sortOrder: Int? = null
)

data class ServerHadithBook(
    @SerializedName("id") val id: Int,
    @SerializedName("collection_id") val collectionId: String,
    @SerializedName("book_number") val bookNumber: Int,
    @SerializedName("name_ar") val nameAr: String,
    @SerializedName("name_en") val nameEn: String? = null,
    @SerializedName("hadiths_count") val hadithsCount: Int? = null
)

data class ServerHadith(
    @SerializedName("id") val id: Int,
    @SerializedName("collection_id") val collectionId: String,
    @SerializedName("book_id") val bookId: Int? = null,
    @SerializedName("hadith_number") val hadithNumber: Int? = null,
    @SerializedName("narrator_ar") val narratorAr: String? = null,
    @SerializedName("text_ar") val textAr: String,
    @SerializedName("text_en") val textEn: String? = null,
    @SerializedName("grade_ar") val gradeAr: String? = null,
    @SerializedName("grade_en") val gradeEn: String? = null,
    @SerializedName("reference") val reference: String? = null
)

data class ServerHadithPage(
    @SerializedName("success") val success: Boolean,
    @SerializedName("collection") val collection: String? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("pages") val pages: Int? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null,
    @SerializedName("data") val data: List<ServerHadith>? = null
)
