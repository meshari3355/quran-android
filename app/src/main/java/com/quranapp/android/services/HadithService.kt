package com.quranapp.android.services

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ===== Hadith Models =====

import com.quranapp.android.models.HadithBook as HadithBookModel

data class Hadith(
    val id: String,
    val hadithNumber: String,
    val text: String,
    val textAr: String? = null,
    val narrator: String,
    val narratorAr: String? = null,
    val book: String,
    val bookAr: String? = null,
    val chapter: String? = null,
    val chapterAr: String? = null,
    val grading: String? = null,
    val gradeAr: String? = null,
    val source: String? = null
)

data class HadithSearchResult(
    val hadith: Hadith,
    val relevanceScore: Float = 0f
)

// Service-specific book data model
data class HadithBookService(
    val id: String,
    val nameAr: String,
    val hadithCount: Int
)

// ===== Hadith Service =====

class HadithService(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val cache = mutableMapOf<String, List<Hadith>>()
    private var allHadithsLoaded = false

    companion object {
        private const val HADITHS_ASSET_PATH = "hadiths.json"
        private const val CACHE_KEY_ALL = "all_hadiths"
    }

    // ===== Data Loading =====

    suspend fun loadAllHadiths(): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            if (cache[CACHE_KEY_ALL] != null) {
                return@runCatching cache[CACHE_KEY_ALL]!!
            }

            val assetManager = context.assets
            val inputStream = assetManager.open(HADITHS_ASSET_PATH)
            val jsonString = inputStream.bufferedReader().readText()

            val type = object : TypeToken<List<Hadith>>() {}.type
            val hadiths: List<Hadith> = gson.fromJson(jsonString, type)

            cache[CACHE_KEY_ALL] = hadiths
            allHadithsLoaded = true
            hadiths
        }
    }

    suspend fun loadHadithsByBook(bookId: String): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            allHadiths.filter { it.book.lowercase() == bookId.lowercase() }
        }
    }

    suspend fun loadHadithsByChapter(book: String, chapter: String): Result<List<Hadith>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val allHadiths = loadAllHadiths().getOrThrow()
                allHadiths.filter { hadith ->
                    hadith.book.lowercase() == book.lowercase() &&
                            hadith.chapter?.lowercase() == chapter.lowercase()
                }
            }
        }

    // ===== Search Functions =====

    suspend fun searchHadiths(query: String): Result<List<HadithSearchResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            val lowerQuery = query.lowercase()

            val results = allHadiths.mapNotNull { hadith ->
                val queryIn = listOf(
                    hadith.text.lowercase(),
                    hadith.textAr?.lowercase() ?: "",
                    hadith.narrator.lowercase(),
                    hadith.narratorAr?.lowercase() ?: "",
                    hadith.book.lowercase()
                )

                val matchCount = queryIn.count { it.contains(lowerQuery) }
                if (matchCount > 0) {
                    val relevanceScore = matchCount.toFloat() / queryIn.size
                    HadithSearchResult(hadith, relevanceScore)
                } else {
                    null
                }
            }

            results.sortedByDescending { it.relevanceScore }
        }
    }

    suspend fun searchHadithsByText(text: String): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            val lowerText = text.lowercase()
            allHadiths.filter { hadith ->
                hadith.text.lowercase().contains(lowerText) ||
                        (hadith.textAr?.lowercase()?.contains(lowerText) ?: false)
            }
        }
    }

    suspend fun searchHadithsByNarrator(narrator: String): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            val lowerNarrator = narrator.lowercase()
            allHadiths.filter { hadith ->
                hadith.narrator.lowercase().contains(lowerNarrator) ||
                        (hadith.narratorAr?.lowercase()?.contains(lowerNarrator) ?: false)
            }
        }
    }

    suspend fun getHadithById(id: String): Result<Hadith?> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            allHadiths.find { it.id == id }
        }
    }

    // ===== Book Management =====

    suspend fun getAvailableBooks(): Result<List<HadithBookModel>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            val books = mutableMapOf<String, MutableList<Hadith>>()

            for (hadith in allHadiths) {
                val bookList = books.getOrPut(hadith.book) { mutableListOf() }
                bookList.add(hadith)
            }

            books.map { (bookName, hadiths) ->
                HadithBookModel(
                    id = bookName.lowercase().replace(" ", "_"),
                    bookName = bookName,
                    hadithCount = hadiths.size
                )
            }.sortedBy { it.bookName }
        }
    }

    suspend fun getChaptersInBook(book: String): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val bookHadiths = loadHadithsByBook(book).getOrThrow()
            val chapters = bookHadiths.mapNotNull { it.chapter }.distinct().sorted()
            chapters
        }
    }

    // ===== Random Hadith =====

    suspend fun getRandomHadith(): Result<Hadith> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            if (allHadiths.isEmpty()) {
                throw Exception("No hadiths available")
            }
            allHadiths.random()
        }
    }

    suspend fun getRandomHadithFromBook(book: String): Result<Hadith> = withContext(Dispatchers.IO) {
        runCatching {
            val bookHadiths = loadHadithsByBook(book).getOrThrow()
            if (bookHadiths.isEmpty()) {
                throw Exception("No hadiths found in book: $book")
            }
            bookHadiths.random()
        }
    }

    // ===== Advanced Filtering =====

    suspend fun getHadithsByGrade(grade: String): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            val lowerGrade = grade.lowercase()
            allHadiths.filter { hadith ->
                hadith.grading?.lowercase()?.contains(lowerGrade) ?: false
            }
        }
    }

    suspend fun filterHadiths(
        book: String? = null,
        chapter: String? = null,
        narrator: String? = null,
        grade: String? = null
    ): Result<List<Hadith>> = withContext(Dispatchers.IO) {
        runCatching {
            var hadiths = loadAllHadiths().getOrThrow()

            book?.let { b ->
                hadiths = hadiths.filter { it.book.lowercase() == b.lowercase() }
            }

            chapter?.let { c ->
                hadiths = hadiths.filter { it.chapter?.lowercase() == c.lowercase() }
            }

            narrator?.let { n ->
                hadiths = hadiths.filter {
                    it.narrator.lowercase().contains(n.lowercase()) ||
                            (it.narratorAr?.lowercase()?.contains(n.lowercase()) ?: false)
                }
            }

            grade?.let { g ->
                hadiths = hadiths.filter {
                    it.grading?.lowercase()?.contains(g.lowercase()) ?: false
                }
            }

            hadiths
        }
    }

    // ===== Statistics =====

    suspend fun getHadithCount(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            loadAllHadiths().getOrThrow().size
        }
    }

    suspend fun getHadithCountByBook(): Result<Map<String, Int>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            allHadiths.groupingBy { it.book }.eachCount()
        }
    }

    suspend fun getHadithCountByNarrator(): Result<Map<String, Int>> = withContext(Dispatchers.IO) {
        runCatching {
            val allHadiths = loadAllHadiths().getOrThrow()
            allHadiths.groupingBy { it.narrator }.eachCount()
        }
    }

    // ===== Cache Management =====

    fun clearCache() {
        cache.clear()
        allHadithsLoaded = false
    }

    fun getCacheSize(): Int {
        return cache.values.sumOf { it.size }
    }

    fun isCacheLoaded(): Boolean {
        return allHadithsLoaded
    }

    // ===== Utility Methods =====

    fun formatHadith(hadith: Hadith, language: String = "en"): String {
        return if (language == "ar") {
            buildString {
                append("الكتاب: ${hadith.bookAr ?: hadith.book}\n")
                if (!hadith.chapterAr.isNullOrEmpty()) {
                    append("الباب: ${hadith.chapterAr}\n")
                }
                if (!hadith.narratorAr.isNullOrEmpty()) {
                    append("الراوي: ${hadith.narratorAr}\n")
                }
                append("\n${hadith.textAr ?: hadith.text}\n")
                if (!hadith.gradeAr.isNullOrEmpty()) {
                    append("\nالتصحيح: ${hadith.gradeAr}")
                }
            }
        } else {
            buildString {
                append("Book: ${hadith.book}\n")
                hadith.chapter?.let { append("Chapter: $it\n") }
                append("Narrator: ${hadith.narrator}\n")
                append("\n${hadith.text}\n")
                hadith.grading?.let { append("\nGrading: $it") }
            }
        }
    }
}
