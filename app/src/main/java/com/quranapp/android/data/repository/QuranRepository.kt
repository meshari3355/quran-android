package com.quranapp.android.data.repository

import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.models.BookmarkModel
import com.quranapp.android.models.ServerVerse
import com.quranapp.android.models.Surah
import com.quranapp.android.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    // In-memory cache for surahs
    private var surahsCache: List<Surah>? = null

    /**
     * Get all surahs from API with caching
     */
    fun getSurahsStream(): Flow<Result<List<Surah>>> = flow {
        try {
            // Check cache first
            surahsCache?.let {
                emit(Result.success(it))
                return@flow
            }

            // Fetch from API
            apiService.listSurahs()
                .onSuccess { response ->
                    response.data?.let { serverSuras ->
                        val surahs = serverSuras.map { s ->
                            Surah(
                                id = s.id,
                                nameAr = s.nameAr,
                                nameEn = s.nameEn,
                                versesCount = s.versesCount,
                                pageNumber = s.pagesStart ?: 1,
                                type = if (s.revelationType == "meccan") "مكية" else "مدنية"
                            )
                        }
                        surahsCache = surahs
                        emit(Result.success(surahs))
                    } ?: emit(Result.failure(Exception("Empty surahs list")))
                }
                .onFailure { error ->
                    emit(Result.failure(error))
                }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get a specific surah by ID
     */
    fun getSurahByIdStream(surahId: Int): Flow<Result<Surah?>> = flow {
        try {
            apiService.getSurah(surahId)
                .onSuccess { response ->
                    response.data?.firstOrNull()?.let { verse ->
                        val surah = Surah.getSurahById(surahId)
                        emit(Result.success(surah))
                    } ?: emit(Result.success(null))
                }
                .onFailure { error ->
                    emit(Result.failure(error))
                }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get Quran page content
     */
    suspend fun getPage(page: Int): Result<List<ServerVerse>> = runCatching {
        apiService.getPage(page)
            .getOrThrow()
            .data ?: throw Exception("Empty page data")
    }

    /**
     * Get Quran page with translation
     */
    suspend fun getPageWithTranslation(page: Int, translationId: String): Result<List<ServerVerse>> = runCatching {
        apiService.getPageWithTranslation(page, translationId)
            .getOrThrow()
            .data ?: throw Exception("Empty page data")
    }

    /**
     * Get verses from a specific surah
     */
    suspend fun getSurahVerses(surahNum: Int): Result<List<ServerVerse>> = runCatching {
        apiService.getSurah(surahNum)
            .getOrThrow()
            .data ?: throw Exception("Empty verses list")
    }

    /**
     * Search for verses
     */
    suspend fun searchQuran(query: String, limit: Int = 50): Result<List<ServerVerse>> = runCatching {
        apiService.searchQuran(query, limit)
            .getOrThrow()
            .data?.mapNotNull { serverSearchVerse ->
                ServerVerse(
                    id = serverSearchVerse.id,
                    suraId = serverSearchVerse.suraId,
                    verseNumber = serverSearchVerse.verseNumber,
                    textUthmani = serverSearchVerse.textUthmani,
                    suraNameAr = serverSearchVerse.suraNameAr,
                    suraNameEn = serverSearchVerse.suraNameEn
                )
            } ?: throw Exception("No search results")
    }

    /**
     * Get a random verse for daily verse feature
     */
    suspend fun getRandomVerse(): Result<ServerVerse> = runCatching {
        apiService.getRandomVerse()
            .getOrThrow()
            .data ?: throw Exception("No verse found")
    }

    /**
     * Get all bookmarks
     */
    suspend fun getBookmarks(): Result<List<BookmarkModel>> = runCatching {
        // TODO: Load bookmarks from local database
        emptyList<BookmarkModel>()
    }

    /**
     * Add a new bookmark
     */
    suspend fun addBookmark(bookmark: BookmarkModel): Result<Unit> = runCatching {
        // TODO: Save bookmark to local database
    }

    /**
     * Remove a bookmark by ID
     */
    suspend fun removeBookmark(bookmarkId: String): Result<Unit> = runCatching {
        // TODO: Delete bookmark from local database
    }

    /**
     * Track reading progress
     */
    suspend fun trackReadingProgress(
        surahId: Int,
        verseNumber: Int,
        pageNumber: Int
    ): Result<Unit> = runCatching {
        preferencesManager.saveLastReadSurah(surahId)
        preferencesManager.saveLastReadPage(pageNumber)
        preferencesManager.saveLastReadingTimestamp()
    }

    /**
     * Clear surahs cache
     */
    fun clearCache() {
        surahsCache = null
    }
}
