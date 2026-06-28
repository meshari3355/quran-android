package com.quranapp.android.data.repository

import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.models.ServerFatwa
import com.quranapp.android.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FatwaRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    /**
     * Get list of fatwas with pagination
     */
    suspend fun getFatwaList(
        page: Int = 1,
        perPage: Int = 20,
        category: String? = null
    ): Result<List<ServerFatwa>> = runCatching {
        apiService.listFatwas(page, perPage, category)
            .getOrThrow()
            .data ?: throw Exception("No fatwas found")
    }

    /**
     * Get a specific fatwa by ID
     */
    suspend fun getFatwa(id: Int): Result<ServerFatwa> = runCatching {
        apiService.getFatwa(id)
            .getOrThrow()
            .data ?: throw Exception("Fatwa not found")
    }

    /**
     * Search fatwas
     */
    suspend fun searchFatwas(query: String, page: Int = 1): Result<List<ServerFatwa>> = runCatching {
        apiService.searchFatwas(query, page)
            .getOrThrow()
            .data ?: throw Exception("No search results")
    }

    /**
     * Get a random fatwa
     */
    suspend fun getRandomFatwa(): Result<ServerFatwa> = runCatching {
        apiService.getRandomFatwa()
            .getOrThrow()
            .data ?: throw Exception("No fatwa found")
    }

    /**
     * Get fatwas as a Flow
     */
    fun getFatwaListStream(
        page: Int = 1,
        perPage: Int = 20,
        category: String? = null
    ): Flow<Result<List<ServerFatwa>>> = flow {
        try {
            val result = getFatwaList(page, perPage, category)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Search fatwas as a Flow
     */
    fun searchFatwaStream(query: String): Flow<Result<List<ServerFatwa>>> = flow {
        try {
            val result = searchFatwas(query)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get saved fatwas (bookmarks)
     */
    suspend fun getSavedFatwas(): Result<List<ServerFatwa>> = runCatching {
        // TODO: Load from local database
        emptyList<ServerFatwa>()
    }

    /**
     * Add fatwa to saved/bookmarks
     */
    suspend fun addToSaved(fatwaId: Int): Result<Unit> = runCatching {
        // TODO: Save to local database
    }

    /**
     * Remove fatwa from saved/bookmarks
     */
    suspend fun removeFromSaved(fatwaId: Int): Result<Unit> = runCatching {
        // TODO: Delete from local database
    }

    /**
     * Check if fatwa is saved
     */
    suspend fun isFatwaSaved(fatwaId: Int): Result<Boolean> = runCatching {
        // TODO: Check in local database
        false
    }
}
