package com.quranapp.android.services

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.util.concurrent.TimeUnit

// ===== DTOs =====

data class QuranAyah(
    val surah: Int,
    val ayah: Int,
    val arabicText: String,
    val translationEn: String? = null,
    val translationAr: String? = null
)

data class QuranPage(
    val pageNumber: Int,
    val ayahs: List<QuranAyah>
)

data class QuranApiPage(
    val page: Int,
    val text: String
)

// ===== Retrofit API Interface =====

interface QuranPageApiInterface {
    @GET("pages/{page}.json")
    suspend fun getQuranPage(@Path("page") page: Int): Response<QuranApiPage>
}

// ===== Retrofit Client =====

private object QuranRetrofitClient {
    private const val BASE_URL = "https://quran.meshari.tech/api/"
    private const val TIMEOUT_SECONDS = 30L

    val apiService: QuranPageApiInterface by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuranPageApiInterface::class.java)
    }
}

// ===== Download Progress Data =====

data class DownloadProgress(
    val totalPages: Int = 604,
    val downloadedPages: Int = 0,
    val isDownloading: Boolean = false,
    val currentPage: Int = 0,
    val error: String? = null
) {
    val progressPercent: Float
        get() = if (totalPages > 0) (downloadedPages.toFloat() / totalPages) * 100 else 0f
}

// ===== Quran Cache Manager =====

class QuranCacheManager(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val api = QuranRetrofitClient.apiService
    private val cacheDir = File(context.filesDir, "quran_cache")

    private val _downloadProgress = MutableStateFlow(DownloadProgress())
    val downloadProgress: StateFlow<DownloadProgress> = _downloadProgress.asStateFlow()

    companion object {
        private const val TOTAL_PAGES = 604
        private const val PAGE_FILE_PATTERN = "page_%03d.json"
    }

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    suspend fun getQuranPage(pageNumber: Int): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            // Check cache first
            val cachedContent = readPageFromCache(pageNumber)
            if (cachedContent != null) {
                return@runCatching cachedContent
            }

            // Fetch from API
            val response = api.getQuranPage(pageNumber)
            if (response.isSuccessful) {
                val pageText = response.body()?.text
                    ?: throw Exception("Empty page data")

                // Cache the page
                cachePageToStorage(pageNumber, pageText)
                pageText
            } else {
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }

    suspend fun downloadAllPages(overwrite: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            _downloadProgress.value = DownloadProgress(isDownloading = true)

            var downloadedCount = 0
            var lastError: Exception? = null

            for (page in 1..TOTAL_PAGES) {
                try {
                    // Skip if already cached and not overwriting
                    if (!overwrite && isPageCached(page)) {
                        downloadedCount++
                        _downloadProgress.value = _downloadProgress.value.copy(
                            downloadedPages = downloadedCount,
                            currentPage = page
                        )
                        continue
                    }

                    // Fetch and cache page
                    val response = api.getQuranPage(page)
                    if (response.isSuccessful) {
                        val pageText = response.body()?.text
                        if (pageText != null) {
                            cachePageToStorage(page, pageText)
                            downloadedCount++
                        }
                    }

                    _downloadProgress.value = _downloadProgress.value.copy(
                        downloadedPages = downloadedCount,
                        currentPage = page
                    )
                } catch (e: Exception) {
                    lastError = e
                    _downloadProgress.value = _downloadProgress.value.copy(
                        error = e.message ?: "Unknown error downloading page $page"
                    )
                    // Continue downloading other pages
                }
            }

            if (lastError != null && downloadedCount < TOTAL_PAGES) {
                throw lastError
            }

            _downloadProgress.value = DownloadProgress(
                downloadedPages = downloadedCount,
                isDownloading = false
            )
        }
    }

    suspend fun downloadPagesRange(
        startPage: Int,
        endPage: Int,
        overwrite: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val totalToDownload = endPage - startPage + 1
            _downloadProgress.value = DownloadProgress(
                totalPages = totalToDownload,
                isDownloading = true
            )

            var downloadedCount = 0
            var lastError: Exception? = null

            for (page in startPage..endPage) {
                try {
                    if (!overwrite && isPageCached(page)) {
                        downloadedCount++
                        _downloadProgress.value = _downloadProgress.value.copy(
                            downloadedPages = downloadedCount,
                            currentPage = page
                        )
                        continue
                    }

                    val response = api.getQuranPage(page)
                    if (response.isSuccessful) {
                        val pageText = response.body()?.text
                        if (pageText != null) {
                            cachePageToStorage(page, pageText)
                            downloadedCount++
                        }
                    }

                    _downloadProgress.value = _downloadProgress.value.copy(
                        downloadedPages = downloadedCount,
                        currentPage = page
                    )
                } catch (e: Exception) {
                    lastError = e
                }
            }

            if (lastError != null && downloadedCount < totalToDownload) {
                throw lastError
            }

            _downloadProgress.value = DownloadProgress(
                downloadedPages = downloadedCount,
                isDownloading = false
            )
        }
    }

    fun resumeDownload(): Result<Unit> {
        return runCatching {
            val currentState = _downloadProgress.value
            if (!currentState.isDownloading) {
                throw Exception("No download in progress")
            }
            // Resume would continue from currentPage + 1
            // This is handled by the download logic checking isPageCached
        }
    }

    fun pauseDownload() {
        _downloadProgress.value = _downloadProgress.value.copy(isDownloading = false)
    }

    fun getCachedPagesCount(): Int {
        return cacheDir.listFiles()?.size ?: 0
    }

    fun isPageCached(pageNumber: Int): Boolean {
        return getPageFile(pageNumber).exists()
    }

    fun getCacheSize(): Long {
        var size = 0L
        cacheDir.walkTopDown().forEach { file ->
            size += file.length()
        }
        return size
    }

    fun clearCache() {
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
        _downloadProgress.value = DownloadProgress()
    }

    fun clearPageCache(pageNumber: Int) {
        getPageFile(pageNumber).delete()
    }

    // ===== Private Methods =====

    private fun getPageFile(pageNumber: Int): File {
        return File(cacheDir, String.format(PAGE_FILE_PATTERN, pageNumber))
    }

    private fun readPageFromCache(pageNumber: Int): String? {
        return try {
            val file = getPageFile(pageNumber)
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            null
        }
    }

    private fun cachePageToStorage(pageNumber: Int, content: String) {
        try {
            val file = getPageFile(pageNumber)
            file.writeText(content)
        } catch (e: Exception) {
            throw Exception("Failed to cache page $pageNumber: ${e.message}")
        }
    }

    suspend fun searchPages(query: String): Result<List<Int>> = withContext(Dispatchers.IO) {
        runCatching {
            val matchedPages = mutableListOf<Int>()
            val lowerQuery = query.lowercase()

            for (page in 1..TOTAL_PAGES) {
                val content = getQuranPage(page).getOrNull()
                if (content?.lowercase()?.contains(lowerQuery) == true) {
                    matchedPages.add(page)
                }
            }

            matchedPages
        }
    }
}
