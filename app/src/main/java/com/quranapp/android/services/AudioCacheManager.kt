package com.quranapp.android.services

import android.content.Context
import com.quranapp.android.models.Reciter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AudioDownloadProgress(
    val totalItems: Int = 0,
    val downloadedItems: Int = 0,
    val isDownloading: Boolean = false,
    val currentSurah: Int = 0,
    val currentAyah: Int = 0,
    val currentReciter: String = "",
    val error: String? = null
)

class AudioCacheManager(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val cacheDir = File(context.filesDir, "audio_cache")

    private val _downloadProgress = MutableStateFlow(AudioDownloadProgress())
    val downloadProgress: StateFlow<AudioDownloadProgress> = _downloadProgress.asStateFlow()

    companion object {
        private const val AUDIO_BASE_URL = "https://everyayah.com/data"
        private val AYAHS_PER_SURAH = intArrayOf(
            7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, 111, 110,
            98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 30, 73, 54, 45, 83, 182, 88, 75,
            85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, 49, 78, 48, 45, 90, 80, 61, 50, 45,
            33, 34, 39, 28, 34, 31, 34, 34, 28, 80, 30, 31, 29, 32, 31, 29, 34, 30, 30, 29, 30,
            30, 26, 28, 27, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 29, 29, 29, 30, 30, 30,
            30, 29, 29, 29, 29, 28, 29, 30, 29, 29, 29, 29, 30, 29, 29, 29, 28, 29, 29, 29, 29
        )
    }

    init {
        if (!cacheDir.exists()) cacheDir.mkdirs()
    }

    fun getAvailableReciters(): List<Reciter> = Reciter.getAllReciters()

    suspend fun downloadAudio(surah: Int, ayah: Int, reciterId: Int): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val reciter = Reciter.getAllReciters().find { it.id == reciterId }
                    ?: throw Exception("Reciter not found: $reciterId")

                val cachedPath = getAudioFilePath(surah, ayah, reciterId)
                if (cachedPath.exists()) return@runCatching cachedPath.absolutePath

                val url = buildAudioUrl(surah, ayah, reciter.cdnFolder)
                downloadAndCache(url, cachedPath).absolutePath
            }
        }

    suspend fun downloadSurahAudio(surah: Int, reciterId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val reciter = Reciter.getAllReciters().find { it.id == reciterId }
                    ?: throw Exception("Reciter not found: $reciterId")

                val ayahCount = AYAHS_PER_SURAH.getOrNull(surah - 1) ?: 0
                _downloadProgress.value = AudioDownloadProgress(
                    totalItems = ayahCount, isDownloading = true,
                    currentSurah = surah, currentReciter = reciter.nameAr
                )

                var downloaded = 0
                for (ayah in 1..ayahCount) {
                    val cached = getAudioFilePath(surah, ayah, reciterId)
                    if (!cached.exists()) {
                        downloadAndCache(buildAudioUrl(surah, ayah, reciter.cdnFolder), cached)
                    }
                    downloaded++
                    _downloadProgress.value = _downloadProgress.value.copy(
                        downloadedItems = downloaded, currentAyah = ayah
                    )
                }
                _downloadProgress.value = AudioDownloadProgress(isDownloading = false)
            }
        }

    suspend fun getAudioUrl(surah: Int, ayah: Int, reciterId: Int, cacheOnly: Boolean = false): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cached = getAudioFilePath(surah, ayah, reciterId)
                if (cached.exists()) return@runCatching cached.absolutePath
                if (cacheOnly) throw Exception("Audio not cached")

                val reciter = Reciter.getAllReciters().find { it.id == reciterId }
                    ?: throw Exception("Reciter not found")
                buildAudioUrl(surah, ayah, reciter.cdnFolder)
            }
        }

    fun isAudioCached(surah: Int, ayah: Int, reciterId: Int): Boolean =
        getAudioFilePath(surah, ayah, reciterId).exists()

    fun getCacheSize(): Long {
        var size = 0L
        cacheDir.walkTopDown().forEach { size += it.length() }
        return size
    }

    fun clearCache() {
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
    }

    fun clearReciterCache(reciterId: Int) {
        File(cacheDir, reciterId.toString()).deleteRecursively()
    }

    private fun buildAudioUrl(surah: Int, ayah: Int, cdnFolder: String): String {
        val s = String.format(Locale.US, "%03d", surah)
        val a = String.format(Locale.US, "%03d", ayah)
        return "$AUDIO_BASE_URL/$cdnFolder/$s$a.mp3"
    }

    private fun getAudioFilePath(surah: Int, ayah: Int, reciterId: Int): File {
        val reciterDir = File(cacheDir, reciterId.toString())
        val surahDir = File(reciterDir, String.format(Locale.US, "%03d", surah))
        return File(surahDir, String.format(Locale.US, "%03d.mp3", ayah))
    }

    private fun downloadAndCache(url: String, targetFile: File): File {
        targetFile.parentFile?.mkdirs()
        val request = Request.Builder().url(url).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            response.body?.let { targetFile.writeBytes(it.bytes()) }
                ?: throw Exception("Empty response")
        }
        return targetFile
    }
}
