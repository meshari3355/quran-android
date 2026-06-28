package com.quranapp.android.data.repository

import com.quranapp.android.models.HadithBook
import com.quranapp.android.models.HadithBookSummary
import com.quranapp.android.models.HadithPortalCategory
import com.quranapp.android.models.HadithText
import com.quranapp.android.models.ServerHadith
import com.quranapp.android.models.ServerHadithBook
import com.quranapp.android.models.ServerHadithCollection
import com.quranapp.android.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HadithRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Get all Hadith collections
     */
    suspend fun getHadithCollections(): Result<List<ServerHadithCollection>> = runCatching {
        apiService.listHadithCollections()
            .getOrThrow()
            .data ?: throw Exception("No hadith collections found")
    }

    /**
     * Get all books in a specific collection
     */
    suspend fun getHadithBooks(collectionId: String): Result<List<ServerHadithBook>> = runCatching {
        apiService.listHadithBooks(collectionId)
            .getOrThrow()
            .data ?: throw Exception("No hadith books found")
    }

    /**
     * Get hadiths from a specific book with pagination
     */
    suspend fun getHadithList(
        collectionId: String,
        bookId: Int,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<ServerHadith>> = runCatching {
        apiService.listHadiths(collectionId, bookId, page, limit)
            .getOrThrow()
            .data ?: throw Exception("No hadiths found")
    }

    /**
     * Get a single hadith by number
     */
    suspend fun getHadith(collectionId: String, hadithNumber: Int): Result<ServerHadith> = runCatching {
        apiService.getHadith(collectionId, hadithNumber)
            .getOrThrow()
            .data ?: throw Exception("Hadith not found")
    }

    /**
     * Search hadith across all collections
     */
    suspend fun searchHadith(query: String, collectionId: String, limit: Int = 50): Result<List<ServerHadith>> = runCatching {
        apiService.searchHadiths(query, collectionId, limit)
            .getOrThrow()
            .data ?: throw Exception("No search results")
    }

    /**
     * Get a random hadith
     */
    suspend fun getRandomHadith(collectionId: String): Result<ServerHadith> = runCatching {
        apiService.getRandomHadith(collectionId)
            .getOrThrow()
            .data ?: throw Exception("No hadith found")
    }

    /**
     * Get all Hadith collections categorized
     */
    fun getHadithCategoriesStream(): Flow<Result<List<HadithPortalCategory>>> = flow {
        try {
            emit(Result.success(HadithBookSummary.getAllCategories()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get all books in a specific category
     */
    fun getHadithBooksStream(categoryId: String): Flow<Result<List<HadithBookSummary>>> = flow {
        try {
            val category = HadithBookSummary.getCategoryById(categoryId)
            emit(Result.success(category?.books ?: emptyList()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get detailed hadith book with chapters
     */
    suspend fun getHadithBook(bookId: String): Result<HadithBook> = runCatching {
        throw NotImplementedError("Hadith book not found")
    }

    /**
     * Search hadith across all collections
     */
    fun searchHadithStream(query: String): Flow<Result<List<HadithText>>> = flow {
        try {
            // TODO: Implement search across all collections
            emit(Result.success(emptyList<HadithText>()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get hadiths from a specific book and chapter
     */
    suspend fun getHadithByBookAndChapter(
        bookId: String,
        chapterId: String
    ): Result<List<HadithText>> = runCatching {
        emptyList<HadithText>()
    }

    /**
     * Get a single hadith by ID
     */
    suspend fun getHadithById(hadithId: String): Result<HadithText> = runCatching {
        throw NotImplementedError("Hadith not found")
    }

    /**
     * Get favorite hadiths saved by user
     */
    suspend fun getFavoriteHadiths(): Result<List<HadithText>> = runCatching {
        emptyList<HadithText>()
    }

    /**
     * Add hadith to favorites
     */
    suspend fun addToFavorites(hadithId: String): Result<Unit> = runCatching {
        // TODO: Save to local database
    }

    /**
     * Remove hadith from favorites
     */
    suspend fun removeFromFavorites(hadithId: String): Result<Unit> = runCatching {
        // TODO: Delete from local database
    }
}
