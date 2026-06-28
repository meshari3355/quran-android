package com.quranapp.android.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.quranapp.android.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ===== REQUEST DTOs - Device Tracking =====

data class RegisterDeviceRequest(
    val deviceId: String,
    val deviceName: String,
    val osVersion: String,
    val appVersion: String,
    val platform: String = "android"
)

data class TrackEventRequest(
    val deviceId: String,
    val eventName: String,
    val eventData: Map<String, Any>? = null,
    val timestamp: Long
)

data class SessionStartRequest(
    val deviceId: String,
    val userId: String? = null,
    val timestamp: Long
)

data class SessionEndRequest(
    val deviceId: String,
    val sessionId: String,
    val duration: Long,
    val timestamp: Long
)

// ===== RESPONSE DTOs - Device Tracking =====

data class RegisterDeviceResponse(
    val success: Boolean,
    val message: String?,
    val data: DeviceData? = null
)

data class DeviceData(
    val deviceId: String,
    val registeredAt: String
)

data class TrackEventResponse(
    val success: Boolean,
    val message: String?
)

data class SessionStartResponse(
    val success: Boolean,
    val message: String?,
    val sessionId: String? = null
)

data class SessionEndResponse(
    val success: Boolean,
    val message: String?
)

// ===== RESPONSE DTOs - Aladhan Fallback (API-specific) =====

data class AladhanTimingsResponse(
    val code: Int,
    val status: String,
    val data: AladhanData?
)

data class AladhanData(
    val timings: AladhanTimings?
)

data class AladhanTimings(
    @SerializedName("Fajr")
    val fajr: String,
    @SerializedName("Sunrise")
    val sunrise: String? = null,
    @SerializedName("Dhuhr")
    val dhuhr: String,
    @SerializedName("Asr")
    val asr: String,
    @SerializedName("Sunset")
    val sunset: String? = null,
    @SerializedName("Maghrib")
    val maghrib: String,
    @SerializedName("Isha")
    val isha: String
)

// ===== Retrofit API Interfaces =====

interface QuranApi {
    @GET("quran.php")
    suspend fun listSurahs(@Query("action") action: String = "suras"): Response<ServerResponse<List<ServerSura>>>

    @GET("quran.php")
    suspend fun getPage(
        @Query("action") action: String = "page",
        @Query("page") page: Int
    ): Response<ServerResponse<List<ServerVerse>>>

    @GET("quran.php")
    suspend fun getPageWithTranslation(
        @Query("action") action: String = "page",
        @Query("page") page: Int,
        @Query("translation") translation: String
    ): Response<ServerResponse<List<ServerVerse>>>

    @GET("quran.php")
    suspend fun getSurah(
        @Query("action") action: String = "sura",
        @Query("sura") sura: Int
    ): Response<ServerResponse<List<ServerVerse>>>

    @GET("quran.php")
    suspend fun getVerse(
        @Query("action") action: String = "verse",
        @Query("sura") sura: Int,
        @Query("verse") verse: Int
    ): Response<ServerResponse<ServerVerse>>

    @GET("quran.php")
    suspend fun getJuz(
        @Query("action") action: String = "juz",
        @Query("juz") juz: Int
    ): Response<ServerResponse<List<ServerVerse>>>

    @GET("quran.php")
    suspend fun search(
        @Query("action") action: String = "search",
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): Response<ServerResponse<List<ServerSearchVerse>>>

    @GET("quran.php")
    suspend fun getRandomVerse(@Query("action") action: String = "random"): Response<ServerResponse<ServerVerse>>
}

interface AudioApi {
    @GET("audio.php")
    suspend fun listReciters(@Query("action") action: String = "reciters"): Response<ServerResponse<List<ServerReciter>>>

    @GET("audio.php")
    suspend fun getFeaturedReciters(@Query("action") action: String = "featured"): Response<ServerResponse<List<ServerReciter>>>

    @GET("audio.php")
    suspend fun getSurahAudio(
        @Query("action") action: String = "sura",
        @Query("reciter") reciter: Int,
        @Query("sura") sura: Int
    ): Response<ServerResponse<ServerSuraAudio>>

    @GET("audio.php")
    suspend fun getVerseAudio(
        @Query("action") action: String = "verse",
        @Query("reciter") reciter: Int,
        @Query("sura") sura: Int,
        @Query("verse") verse: Int
    ): Response<ServerResponse<ServerVerse>>

    @GET("audio.php")
    suspend fun getPlaylist(
        @Query("action") action: String = "playlist",
        @Query("reciter") reciter: Int,
        @Query("sura") sura: Int
    ): Response<ServerResponse<List<ServerPlaylistItem>>>
}

interface PrayerApi {
    @GET("prayer_times.php")
    suspend fun getPrayerTimes(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("date") date: String,
        @Query("method") method: Int = 10,
        @Query("asr") asr: String = "standard"
    ): Response<ServerResponse<ServerPrayerTimes>>

    @GET("prayer_times.php")
    suspend fun getPrayerMethods(@Query("action") action: String = "methods"): Response<ServerResponse<List<ServerPrayerMethod>>>
}

interface TafsirApi {
    @GET("tafsir.php")
    suspend fun getTafsir(
        @Query("action") action: String = "sura",
        @Query("sura") sura: Int,
        @Query("tafsir") tafsir: Int
    ): Response<ServerTafsirResponse>
}

interface HadithApi {
    @GET("hadith.php")
    suspend fun listCollections(@Query("action") action: String = "collections"): Response<ServerResponse<List<ServerHadithCollection>>>

    @GET("hadith.php")
    suspend fun listBooks(
        @Query("action") action: String = "books",
        @Query("collection") collection: String
    ): Response<ServerResponse<List<ServerHadithBook>>>

    @GET("hadith.php")
    suspend fun listHadiths(
        @Query("action") action: String = "list",
        @Query("collection") collection: String,
        @Query("book") book: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ServerHadithPage>

    @GET("hadith.php")
    suspend fun getHadith(
        @Query("action") action: String = "get",
        @Query("collection") collection: String,
        @Query("number") number: Int
    ): Response<ServerResponse<ServerHadith>>

    @GET("hadith.php")
    suspend fun searchHadiths(
        @Query("action") action: String = "search",
        @Query("q") query: String,
        @Query("collection") collection: String,
        @Query("limit") limit: Int = 50
    ): Response<ServerResponse<List<ServerHadith>>>

    @GET("hadith.php")
    suspend fun getRandomHadith(
        @Query("action") action: String = "random",
        @Query("collection") collection: String
    ): Response<ServerResponse<ServerHadith>>
}

interface FatwaApi {
    @GET("fatwa.php")
    suspend fun listFatwas(
        @Query("action") action: String = "list",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null
    ): Response<ServerFatwaListResponse>

    @GET("fatwa.php")
    suspend fun getFatwa(
        @Query("action") action: String = "get",
        @Query("id") id: Int
    ): Response<ServerResponse<ServerFatwa>>

    @GET("fatwa.php")
    suspend fun searchFatwas(
        @Query("action") action: String = "search",
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): Response<ServerFatwaListResponse>

    @GET("fatwa.php")
    suspend fun getRandomFatwa(@Query("action") action: String = "random"): Response<ServerResponse<ServerFatwa>>
}

interface DeviceTrackingApi {
    @POST("register_device.php")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): Response<RegisterDeviceResponse>

    @POST("track_event.php")
    suspend fun trackEvent(@Body request: TrackEventRequest): Response<TrackEventResponse>

    @POST("session_start.php")
    suspend fun startSession(@Body request: SessionStartRequest): Response<SessionStartResponse>

    @POST("session_end.php")
    suspend fun endSession(@Body request: SessionEndRequest): Response<SessionEndResponse>
}

// ===== Retrofit Client Singleton =====

object RetrofitClient {
    private const val BASE_URL = "https://quran.meshari.tech/api/"
    private const val FALLBACK_BASE_URL = "https://api.aladhan.com/v1/"
    private const val TIMEOUT_SECONDS = 30L

    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val primaryRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val fallbackRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FALLBACK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Primary API Services
    val quranApi: QuranApi by lazy {
        primaryRetrofit.create(QuranApi::class.java)
    }

    val audioApi: AudioApi by lazy {
        primaryRetrofit.create(AudioApi::class.java)
    }

    val prayerApi: PrayerApi by lazy {
        primaryRetrofit.create(PrayerApi::class.java)
    }

    val tafsirApi: TafsirApi by lazy {
        primaryRetrofit.create(TafsirApi::class.java)
    }

    val hadithApi: HadithApi by lazy {
        primaryRetrofit.create(HadithApi::class.java)
    }

    val fatwaApi: FatwaApi by lazy {
        primaryRetrofit.create(FatwaApi::class.java)
    }

    val deviceTrackingApi: DeviceTrackingApi by lazy {
        primaryRetrofit.create(DeviceTrackingApi::class.java)
    }

    // Fallback API for Prayer Times
    fun createAladhanRetrofit(): Retrofit = fallbackRetrofit
}

// ===== API Service Class =====

class ApiService {
    private val quranApi = RetrofitClient.quranApi
    private val audioApi = RetrofitClient.audioApi
    private val prayerApi = RetrofitClient.prayerApi
    private val tafsirApi = RetrofitClient.tafsirApi
    private val hadithApi = RetrofitClient.hadithApi
    private val fatwaApi = RetrofitClient.fatwaApi
    private val deviceTrackingApi = RetrofitClient.deviceTrackingApi

    // ===== Quran Methods =====

    suspend fun listSurahs(): Result<ServerResponse<List<ServerSura>>> = runCatching {
        val response = quranApi.listSurahs()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getPage(page: Int): Result<ServerResponse<List<ServerVerse>>> = runCatching {
        val response = quranApi.getPage(page = page)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getPageWithTranslation(page: Int, translationId: String): Result<ServerResponse<List<ServerVerse>>> = runCatching {
        val response = quranApi.getPageWithTranslation(page = page, translation = translationId)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getSurah(surah: Int): Result<ServerResponse<List<ServerVerse>>> = runCatching {
        val response = quranApi.getSurah(sura = surah)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getVerse(sura: Int, verse: Int): Result<ServerResponse<ServerVerse>> = runCatching {
        val response = quranApi.getVerse(sura = sura, verse = verse)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getJuz(juz: Int): Result<ServerResponse<List<ServerVerse>>> = runCatching {
        val response = quranApi.getJuz(juz = juz)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchQuran(query: String, limit: Int = 50): Result<ServerResponse<List<ServerSearchVerse>>> = runCatching {
        val response = quranApi.search(query = query, limit = limit)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getRandomVerse(): Result<ServerResponse<ServerVerse>> = runCatching {
        val response = quranApi.getRandomVerse()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ===== Audio Methods =====

    suspend fun listReciters(): Result<ServerResponse<List<ServerReciter>>> = runCatching {
        val response = audioApi.listReciters()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getFeaturedReciters(): Result<ServerResponse<List<ServerReciter>>> = runCatching {
        val response = audioApi.getFeaturedReciters()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getSurahAudio(reciterId: Int, surah: Int): Result<ServerResponse<ServerSuraAudio>> = runCatching {
        val response = audioApi.getSurahAudio(reciter = reciterId, sura = surah)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getVerseAudio(reciterId: Int, sura: Int, verse: Int): Result<ServerResponse<ServerVerse>> = runCatching {
        val response = audioApi.getVerseAudio(reciter = reciterId, sura = sura, verse = verse)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getPlaylist(reciterId: Int, surah: Int): Result<ServerResponse<List<ServerPlaylistItem>>> = runCatching {
        val response = audioApi.getPlaylist(reciter = reciterId, sura = surah)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ===== Prayer Times Methods =====

    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: String,
        method: Int = 10,
        asr: String = "standard"
    ): Result<ServerResponse<ServerPrayerTimes>> = runCatching {
        val response = prayerApi.getPrayerTimes(latitude = latitude, longitude = longitude, date = date, method = method, asr = asr)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getPrayerMethods(): Result<ServerResponse<List<ServerPrayerMethod>>> = runCatching {
        val response = prayerApi.getPrayerMethods()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getPrayerTimesFromAladhan(
        latitude: Double,
        longitude: Double,
        method: Int = 4
    ): Result<AladhanTimingsResponse> = runCatching {
        val aladhanApi = RetrofitClient.createAladhanRetrofit().create(AladhanFallbackApi::class.java)
        val response = aladhanApi.getTimings(latitude, longitude, method)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ===== Tafsir Methods =====

    suspend fun getTafsir(surah: Int, tafsirId: Int): Result<ServerTafsirResponse> = runCatching {
        val response = tafsirApi.getTafsir(sura = surah, tafsir = tafsirId)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getTafsirIbnKathir(surah: Int): Result<ServerTafsirResponse> = runCatching {
        return@runCatching getTafsir(surah, 169).getOrThrow()
    }

    suspend fun getTafsirSaadi(surah: Int): Result<ServerTafsirResponse> = runCatching {
        return@runCatching getTafsir(surah, 91).getOrThrow()
    }

    suspend fun getTafsirJalalyn(surah: Int): Result<ServerTafsirResponse> = runCatching {
        return@runCatching getTafsir(surah, 74).getOrThrow()
    }

    // ===== Hadith Methods =====

    suspend fun listHadithCollections(): Result<ServerResponse<List<ServerHadithCollection>>> = runCatching {
        val response = hadithApi.listCollections()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun listHadithBooks(collectionId: String): Result<ServerResponse<List<ServerHadithBook>>> = runCatching {
        val response = hadithApi.listBooks(collection = collectionId)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun listHadiths(
        collectionId: String,
        bookId: Int,
        page: Int = 1,
        limit: Int = 20
    ): Result<ServerHadithPage> = runCatching {
        val response = hadithApi.listHadiths(collection = collectionId, book = bookId, page = page, limit = limit)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getHadith(collectionId: String, hadithNumber: Int): Result<ServerResponse<ServerHadith>> = runCatching {
        val response = hadithApi.getHadith(collection = collectionId, number = hadithNumber)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchHadiths(
        query: String,
        collectionId: String,
        limit: Int = 50
    ): Result<ServerResponse<List<ServerHadith>>> = runCatching {
        val response = hadithApi.searchHadiths(query = query, collection = collectionId, limit = limit)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getRandomHadith(collectionId: String): Result<ServerResponse<ServerHadith>> = runCatching {
        val response = hadithApi.getRandomHadith(collection = collectionId)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ===== Fatwa Methods =====

    suspend fun listFatwas(page: Int = 1, perPage: Int = 20, category: String? = null): Result<ServerFatwaListResponse> = runCatching {
        val response = fatwaApi.listFatwas(page = page, perPage = perPage, category = category)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getFatwa(id: Int): Result<ServerResponse<ServerFatwa>> = runCatching {
        val response = fatwaApi.getFatwa(id = id)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun searchFatwas(query: String, page: Int = 1): Result<ServerFatwaListResponse> = runCatching {
        val response = fatwaApi.searchFatwas(query = query, page = page)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getRandomFatwa(): Result<ServerResponse<ServerFatwa>> = runCatching {
        val response = fatwaApi.getRandomFatwa()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    // ===== Device Tracking Methods =====

    suspend fun registerDevice(
        deviceId: String,
        deviceName: String,
        osVersion: String,
        appVersion: String
    ): Result<RegisterDeviceResponse> = runCatching {
        val request = RegisterDeviceRequest(
            deviceId = deviceId,
            deviceName = deviceName,
            osVersion = osVersion,
            appVersion = appVersion
        )
        val response = deviceTrackingApi.registerDevice(request)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun trackEvent(
        deviceId: String,
        eventName: String,
        eventData: Map<String, Any>? = null
    ): Result<TrackEventResponse> = runCatching {
        val request = TrackEventRequest(
            deviceId = deviceId,
            eventName = eventName,
            eventData = eventData,
            timestamp = System.currentTimeMillis()
        )
        val response = deviceTrackingApi.trackEvent(request)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun startSession(
        deviceId: String,
        userId: String? = null
    ): Result<SessionStartResponse> = runCatching {
        val request = SessionStartRequest(
            deviceId = deviceId,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )
        val response = deviceTrackingApi.startSession(request)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun endSession(
        deviceId: String,
        sessionId: String,
        duration: Long
    ): Result<SessionEndResponse> = runCatching {
        val request = SessionEndRequest(
            deviceId = deviceId,
            sessionId = sessionId,
            duration = duration,
            timestamp = System.currentTimeMillis()
        )
        val response = deviceTrackingApi.endSession(request)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}

// ===== Aladhan Fallback API =====

interface AladhanFallbackApi {
    @GET("timings")
    suspend fun getTimings(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4
    ): Response<AladhanTimingsResponse>
}
