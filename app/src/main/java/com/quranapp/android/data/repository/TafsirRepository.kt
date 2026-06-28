package com.quranapp.android.data.repository

import com.quranapp.android.models.ServerTafsirResponse
import com.quranapp.android.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TafsirRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Tafsir IDs
    companion object {
        const val TAFSIR_IBN_KATHIR = 169
        const val TAFSIR_SAADI = 91
        const val TAFSIR_JALALYN = 74
    }

    // In-memory disk cache for tafsir content
    private val tafsirCache = mutableMapOf<String, ServerTafsirResponse>()

    /**
     * Get tafsir for a specific surah
     * Default: Ibn Kathir
     */
    suspend fun getTafsir(surah: Int, tafsirId: Int = TAFSIR_IBN_KATHIR): Result<ServerTafsirResponse> = runCatching {
        val cacheKey = "$surah-$tafsirId"

        // Check cache first
        tafsirCache[cacheKey]?.let {
            return@runCatching it
        }

        // Fetch from API
        val response = apiService.getTafsir(surah, tafsirId)
            .getOrThrow()

        // Cache the result
        tafsirCache[cacheKey] = response

        response
    }

    /**
     * Get tafsir as a Flow
     */
    fun getTafsirStream(surah: Int, tafsirId: Int = TAFSIR_IBN_KATHIR): Flow<Result<ServerTafsirResponse>> = flow {
        try {
            val result = getTafsir(surah, tafsirId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get Ibn Kathir's tafsir for a surah
     */
    suspend fun getTafsirIbnKathir(surah: Int): Result<ServerTafsirResponse> = runCatching {
        getTafsir(surah, TAFSIR_IBN_KATHIR).getOrThrow()
    }

    /**
     * Get Saadi's tafsir for a surah
     */
    suspend fun getTafsirSaadi(surah: Int): Result<ServerTafsirResponse> = runCatching {
        getTafsir(surah, TAFSIR_SAADI).getOrThrow()
    }

    /**
     * Get Jalalyn's tafsir for a surah
     */
    suspend fun getTafsirJalalyn(surah: Int): Result<ServerTafsirResponse> = runCatching {
        getTafsir(surah, TAFSIR_JALALYN).getOrThrow()
    }

    /**
     * Get Ibn Kathir's tafsir as a Flow
     */
    fun getTafsirIbnKathirStream(surah: Int): Flow<Result<ServerTafsirResponse>> = flow {
        try {
            val result = getTafsirIbnKathir(surah)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get Saadi's tafsir as a Flow
     */
    fun getTafsirSaadiStream(surah: Int): Flow<Result<ServerTafsirResponse>> = flow {
        try {
            val result = getTafsirSaadi(surah)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get Jalalyn's tafsir as a Flow
     */
    fun getTafsirJalalynStream(surah: Int): Flow<Result<ServerTafsirResponse>> = flow {
        try {
            val result = getTafsirJalalyn(surah)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Clear all cached tafsir data
     */
    fun clearCache() {
        tafsirCache.clear()
    }

    /**
     * Clear cache for a specific surah
     */
    fun clearCacheBySurah(surah: Int) {
        tafsirCache.entries.removeAll { it.key.startsWith("$surah-") }
    }
}
