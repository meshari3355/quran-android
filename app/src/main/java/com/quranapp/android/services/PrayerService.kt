package com.quranapp.android.services

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.quranapp.android.models.PrayerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// ===== DTOs for API Response Mapping =====

data class PrayerData(
    val timings: Map<String, String>,
    val date: DateInfo
)

data class DateInfo(
    val readable: String,
    val timestamp: Long,
    val gregorian: GregorianDate,
    val hijri: HijriDateInfoPrayer
)

data class GregorianDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: WeekdayInfo,
    val month: MonthInfo,
    val year: String
)

data class WeekdayInfo(
    val en: String,
    val ar: String
)

data class MonthInfo(
    val number: Int,
    val en: String,
    val ar: String
)

// Renamed to avoid conflict with models.HijriDate and service HijriDateInfo
data class HijriDateInfoPrayer(
    val date: String,
    val format: String,
    val day: String,
    val weekday: WeekdayInfo,
    val month: MonthInfo,
    val year: String
)

// ===== Retrofit API Interface =====

interface AladhanApiInterface {
    @GET("timings/{timestamp}")
    suspend fun getPrayerTimes(
        @Path("timestamp") timestamp: Long,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4
    ): Response<PrayerResponse>
}

// ===== Retrofit Client Singleton =====

private object AladhanRetrofitClient {
    private const val BASE_URL = "https://api.aladhan.com/v1/"
    private const val TIMEOUT_SECONDS = 30L

    val apiService: AladhanApiInterface by lazy {
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
            .create(AladhanApiInterface::class.java)
    }
}

// ===== Prayer Times Data Class =====

data class PrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val imsak: String? = null,
    val midnight: String? = null
) {
    fun toMap(): Map<String, String> = mapOf(
        "Fajr" to fajr,
        "Sunrise" to sunrise,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha
    ).apply {
        if (imsak != null) (this as MutableMap)["Imsak"] = imsak
        if (midnight != null) (this as MutableMap)["Midnight"] = midnight
    }
}

// ===== Prayer Service =====

class PrayerService(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {
    private val api = AladhanRetrofitClient.apiService

    companion object {
        private const val CACHE_PREFIX = "prayer_times_"
        private const val CACHE_DATE_SUFFIX = "_date"
        private const val CACHE_DURATION_HOURS = 24
        private const val PRAYER_METHOD_KEY = "prayer_method"
        private const val DEFAULT_METHOD = 4 // Umm Al-Qura
    }

    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Date = Date(),
        method: Int = getStoredMethod()
    ): Result<PrayerTimes> = withContext(Dispatchers.IO) {
        runCatching {
            val cacheKey = generateCacheKey(latitude, longitude, date)
            val cachedTimes = getCachedPrayerTimes(cacheKey)

            if (cachedTimes != null) {
                return@runCatching cachedTimes
            }

            val timestamp = date.time / 1000
            val response = api.getPrayerTimes(timestamp, latitude, longitude, method)

            if (response.isSuccessful) {
                val prayerData = response.body()?.data?.timings
                    ?: throw Exception("Empty prayer data")

                val prayerTimes = PrayerTimes(
                    fajr = prayerData["Fajr"] ?: "",
                    sunrise = prayerData["Sunrise"] ?: "",
                    dhuhr = prayerData["Dhuhr"] ?: "",
                    asr = prayerData["Asr"] ?: "",
                    maghrib = prayerData["Maghrib"] ?: "",
                    isha = prayerData["Isha"] ?: "",
                    imsak = prayerData["Imsak"],
                    midnight = prayerData["Midnight"]
                )

                cachePrayerTimes(cacheKey, prayerTimes)
                prayerTimes
            } else {
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }

    suspend fun getPrayerTimesForToday(
        latitude: Double,
        longitude: Double,
        method: Int = getStoredMethod()
    ): Result<PrayerTimes> = getPrayerTimes(latitude, longitude, Date(), method)

    fun setPrayerMethod(method: Int) {
        sharedPreferences.edit().putInt(PRAYER_METHOD_KEY, method).apply()
    }

    fun getStoredMethod(): Int {
        return sharedPreferences.getInt(PRAYER_METHOD_KEY, DEFAULT_METHOD)
    }

    fun getSupportedMethods(): Map<Int, String> = mapOf(
        1 to "Karachi (University of Islamic Sciences)",
        2 to "ISNA (Islamic Society of North America)",
        3 to "MWL (Muslim World League)",
        4 to "Umm Al-Qura",
        5 to "Egyptian General Authority",
        7 to "JAKIM (Jabatan Kemajuan Islam Malaysia)",
        8 to "UOIF (Union Organisation Islamique de France)",
        9 to "Al Jazeera",
        10 to "Tunisia",
        11 to "Turkey",
        12 to "Tehran",
        13 to "SINGAPORE",
        14 to "PORTUGAL",
        15 to "Dubai",
        16 to "SOLAT",
        17 to "Korean"
    )

    private fun generateCacheKey(latitude: Double, longitude: Double, date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateString = dateFormat.format(date)
        val latRounded = String.format("%.2f", latitude)
        val lngRounded = String.format("%.2f", longitude)
        return "$CACHE_PREFIX${latRounded}_${lngRounded}_$dateString"
    }

    private fun getCachedPrayerTimes(cacheKey: String): PrayerTimes? {
        val json = sharedPreferences.getString(cacheKey, null) ?: return null
        val cachedDate = sharedPreferences.getLong("${cacheKey}$CACHE_DATE_SUFFIX", 0)

        val cacheAgeHours = (System.currentTimeMillis() - cachedDate) / (1000 * 60 * 60)
        if (cacheAgeHours > CACHE_DURATION_HOURS) {
            sharedPreferences.edit()
                .remove(cacheKey)
                .remove("${cacheKey}$CACHE_DATE_SUFFIX")
                .apply()
            return null
        }

        return try {
            gson.fromJson(json, PrayerTimes::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun cachePrayerTimes(cacheKey: String, times: PrayerTimes) {
        val json = gson.toJson(times)
        sharedPreferences.edit()
            .putString(cacheKey, json)
            .putLong("${cacheKey}$CACHE_DATE_SUFFIX", System.currentTimeMillis())
            .apply()
    }

    fun clearCache() {
        sharedPreferences.edit().apply {
            val allKeys = sharedPreferences.all.keys
            allKeys.forEach { key ->
                if (key.startsWith(CACHE_PREFIX)) {
                    remove(key)
                }
            }
            apply()
        }
    }
}
