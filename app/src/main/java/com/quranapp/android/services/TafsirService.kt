package com.quranapp.android.services

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
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

data class TafsirResponse(
    val ayahs: List<TafsirAyah>
)

data class TafsirAyah(
    val ayah: Int,
    val surah: Int,
    val text: String,
    val languageCode: String? = null
)

data class AlquranCloudResponse(
    val code: Int,
    val status: String,
    val data: AyahData
)

data class AyahData(
    val number: Int,
    val text: String,
    val surah: SurahInfo,
    val ayah: Int
)

data class SurahInfo(
    val number: Int,
    val nameAr: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)

// ===== Tafsir Models =====

data class Tafsir(
    val surah: Int,
    val ayah: Int,
    val text: String,
    val source: String,
    val language: String = "ar"
)

enum class TafsirSource(val id: Int, val nameAr: String, val endpoint: String) {
    IBN_KATHIR(169, "تفسير ابن كثير", "ibn-kathir"),
    AS_SAADI(91, "تفسير السعدي", "as-saadi"),
    AL_JALALAYN(74, "تفسير الجلالين", "al-jalalayn")
}

// ===== Retrofit API Interfaces =====

interface QuranCdnApiInterface {
    @GET("tafsirs/{tafsirId}/by_ayah/{verseKey}")
    suspend fun getTafsir(
        @Path("tafsirId") tafsirId: Int,
        @Path("verseKey") verseKey: String
    ): Response<TafsirResponse>
}

interface AlquranCloudApiInterface {
    @GET("ayah/{surah}:{ayah}/ar.jalalayn")
    suspend fun getTafsirJalalayn(
        @Path("surah") surah: Int,
        @Path("ayah") ayah: Int
    ): Response<AlquranCloudResponse>
}

// ===== Retrofit Clients =====

private object QuranCdnRetrofitClient {
    private const val BASE_URL = "https://api.qurancdn.com/api/qdc/"
    private const val TIMEOUT_SECONDS = 30L

    val apiService: QuranCdnApiInterface by lazy {
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
            .create(QuranCdnApiInterface::class.java)
    }
}

private object AlquranCloudRetrofitClient {
    private const val BASE_URL = "https://api.alquran.cloud/v1/"
    private const val TIMEOUT_SECONDS = 30L

    val apiService: AlquranCloudApiInterface by lazy {
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
            .create(AlquranCloudApiInterface::class.java)
    }
}

// ===== Tafsir Service =====

class TafsirService(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val quranCdnApi = QuranCdnRetrofitClient.apiService
    private val alquranCloudApi = AlquranCloudRetrofitClient.apiService
    private val cacheDir = File(context.filesDir, "tafsir_cache")

    companion object {
        private const val CACHE_FILE_PATTERN = "tafsir_%d_%d_%d.json"
    }

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    // ===== Primary Tafsir Fetching =====

    suspend fun getTafsir(
        surah: Int,
        ayah: Int,
        source: TafsirSource = TafsirSource.IBN_KATHIR
    ): Result<Tafsir> = withContext(Dispatchers.IO) {
        runCatching {
            // Check cache first
            val cached = readTafsirFromCache(surah, ayah, source.id)
            if (cached != null) {
                return@runCatching cached
            }

            // Fetch from primary API
            val verseKey = "$surah:$ayah"
            val response = quranCdnApi.getTafsir(source.id, verseKey)

            if (response.isSuccessful && !response.body()?.ayahs.isNullOrEmpty()) {
                val tafsirAyah = response.body()!!.ayahs[0]
                val tafsir = Tafsir(
                    surah = surah,
                    ayah = ayah,
                    text = tafsirAyah.text,
                    source = source.nameAr
                )

                cacheTafsir(tafsir, source.id)
                tafsir
            } else {
                // Fallback to Al-Qur'an Cloud for specific tafsirs
                when (source) {
                    TafsirSource.AL_JALALAYN -> getTafsirJalalaynFallback(surah, ayah)
                    else -> throw Exception("Failed to fetch tafsir from primary API")
                }
            }
        }
    }

    suspend fun getTafsirFromMultipleSources(
        surah: Int,
        ayah: Int,
        sources: List<TafsirSource> = listOf(
            TafsirSource.IBN_KATHIR,
            TafsirSource.AS_SAADI,
            TafsirSource.AL_JALALAYN
        )
    ): Result<Map<String, Tafsir>> = withContext(Dispatchers.IO) {
        runCatching {
            val tafsirs = mutableMapOf<String, Tafsir>()

            for (source in sources) {
                try {
                    val tafsir = getTafsir(surah, ayah, source).getOrNull()
                    if (tafsir != null) {
                        tafsirs[source.nameAr] = tafsir
                    }
                } catch (e: Exception) {
                    // Continue with next source
                }
            }

            if (tafsirs.isEmpty()) {
                throw Exception("Failed to fetch tafsir from any source")
            }

            tafsirs
        }
    }

    // ===== Fallback Methods =====

    private suspend fun getTafsirJalalaynFallback(
        surah: Int,
        ayah: Int
    ): Tafsir = withContext(Dispatchers.IO) {
        val response = alquranCloudApi.getTafsirJalalayn(surah, ayah)

        if (response.isSuccessful) {
            val data = response.body()?.data
            if (data != null) {
                return@withContext Tafsir(
                    surah = surah,
                    ayah = ayah,
                    text = data.text,
                    source = "تفسير الجلالين"
                )
            }
        }

        throw Exception("Failed to fetch tafsir from fallback API")
    }

    // ===== Cache Management =====

    private fun generateCacheKey(surah: Int, ayah: Int, tafsirId: Int): String {
        return String.format(CACHE_FILE_PATTERN, surah, ayah, tafsirId)
    }

    private fun getCacheFile(surah: Int, ayah: Int, tafsirId: Int): File {
        return File(cacheDir, generateCacheKey(surah, ayah, tafsirId))
    }

    private fun readTafsirFromCache(surah: Int, ayah: Int, tafsirId: Int): Tafsir? {
        return try {
            val file = getCacheFile(surah, ayah, tafsirId)
            if (file.exists()) {
                gson.fromJson(file.readText(), Tafsir::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun cacheTafsir(tafsir: Tafsir, tafsirId: Int) {
        try {
            val file = getCacheFile(tafsir.surah, tafsir.ayah, tafsirId)
            file.writeText(gson.toJson(tafsir))
        } catch (e: Exception) {
            // Silently fail
        }
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
    }

    fun clearTafsirCache(surah: Int, ayah: Int) {
        TafsirSource.values().forEach { source ->
            getCacheFile(surah, ayah, source.id).delete()
        }
    }

    fun clearSurahCache(surah: Int) {
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("tafsir_$surah")) {
                file.delete()
            }
        }
    }

    suspend fun preloadSurahTafsirs(
        surah: Int,
        source: TafsirSource = TafsirSource.IBN_KATHIR
    ): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val ayahCount = getTotalAyahsInSurah(surah)
            var loadedCount = 0

            for (ayah in 1..ayahCount) {
                try {
                    getTafsir(surah, ayah, source)
                    loadedCount++
                } catch (e: Exception) {
                    // Continue loading remaining ayahs
                }
            }

            loadedCount
        }
    }

    // ===== Helper Methods =====

    private fun getTotalAyahsInSurah(surah: Int): Int {
        val ayahsPerSurah = intArrayOf(
            7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, 111, 110,
            98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 30, 73, 54, 45, 83, 182, 88, 75,
            85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, 49, 78, 48, 45, 90, 80, 61, 50, 45,
            33, 34, 39, 28, 34, 31, 34, 34, 28, 80, 30, 31, 29, 32, 31, 29, 34, 30, 30, 29, 30,
            30, 26, 28, 27, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 29, 29, 29, 30, 30, 30,
            30, 29, 29, 29, 29, 28, 29, 30, 29, 29, 29, 29, 30, 29, 29, 29, 28, 29, 29, 29, 29
        )
        return ayahsPerSurah.getOrNull(surah - 1) ?: 0
    }

    fun getAvailableTafsirsInfo(): List<Pair<String, String>> {
        return listOf(
            "ibn-kathir" to "تفسير ابن كثير",
            "as-saadi" to "تفسير السعدي",
            "al-jalalayn" to "تفسير الجلالين"
        )
    }
}
