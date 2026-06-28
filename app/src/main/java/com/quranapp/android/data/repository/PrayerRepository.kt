package com.quranapp.android.data.repository

import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.models.PrayerResponse
import com.quranapp.android.models.PrayerTime
import com.quranapp.android.models.SavedCity
import com.quranapp.android.models.ServerPrayerMethod
import com.quranapp.android.models.ServerPrayerTimes
import com.quranapp.android.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    // In-memory cache for prayer times (24-hour cache)
    private var cachedPrayerTimes: Pair<String, ServerPrayerTimes>? = null
    private var cacheTimestamp: Long = 0
    private val CACHE_DURATION = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

    /**
     * Get prayer times for a specific location as a Flow
     * Tries primary API first, falls back to Aladhan if needed
     */
    fun getPrayerTimesStream(
        latitude: Double,
        longitude: Double,
        date: String,
        method: Int = 10
    ): Flow<Result<ServerPrayerTimes>> = flow {
        try {
            // Check cache first
            val cacheKey = "$latitude,$longitude,$date"
            if (isCacheValid(cacheKey)) {
                cachedPrayerTimes?.second?.let {
                    emit(Result.success(it))
                    return@flow
                }
            }

            // Try primary API
            val result = apiService.getPrayerTimes(latitude, longitude, date, method)
            if (result.isSuccess) {
                result.getOrNull()?.data?.let { prayerData ->
                    cachedPrayerTimes = cacheKey to prayerData
                    cacheTimestamp = System.currentTimeMillis()
                    emit(Result.success(prayerData))
                } ?: emit(Result.failure(Exception("Empty prayer times data")))
            } else {
                // Fallback to Aladhan API
                val fallbackResult = apiService.getPrayerTimesFromAladhan(latitude, longitude, method)
                if (fallbackResult.isSuccess) {
                    val aladhanData = fallbackResult.getOrThrow()
                    val prayerData = aladhanData.data?.timings?.let {
                        ServerPrayerTimes(
                            fajr = it.fajr,
                            dhuhr = it.dhuhr,
                            asr = it.asr,
                            maghrib = it.maghrib,
                            isha = it.isha,
                            sunrise = it.sunrise,
                            date = date
                        )
                    }
                    prayerData?.let {
                        cachedPrayerTimes = cacheKey to it
                        cacheTimestamp = System.currentTimeMillis()
                        emit(Result.success(it))
                    } ?: emit(Result.failure(Exception("Invalid fallback response")))
                } else {
                    emit(Result.failure(fallbackResult.exceptionOrNull() ?: Exception("Unknown error")))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get prayer methods list
     */
    suspend fun getPrayerMethods(): Result<List<ServerPrayerMethod>> = runCatching {
        apiService.getPrayerMethods()
            .getOrThrow()
            .data ?: throw Exception("No prayer methods found")
    }

    /**
     * Get Qibla direction for a specific location
     */
    fun getQiblaDirectionStream(
        latitude: Double,
        longitude: Double
    ): Flow<Result<Double>> = flow {
        try {
            val bearing = calculateQiblaBearing(latitude, longitude)
            emit(Result.success(bearing))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get saved cities for quick prayer time lookup
     */
    suspend fun getSavedCities(): Result<List<SavedCity>> = runCatching {
        // TODO: Load saved cities from local database
        emptyList<SavedCity>()
    }

    /**
     * Save a city for quick prayer time lookup
     */
    suspend fun saveCityPreference(city: SavedCity): Result<Unit> = runCatching {
        // TODO: Save city to local database
    }

    /**
     * Remove a saved city
     */
    suspend fun removeSavedCity(cityName: String): Result<Unit> = runCatching {
        // TODO: Delete city from local database
    }

    /**
     * Calculate Qibla bearing using Haversine formula
     * Kaaba coordinates: 21.4225° N, 39.8262° E
     */
    private fun calculateQiblaBearing(userLat: Double, userLon: Double): Double {
        val kaabaLat = Math.toRadians(21.4225)
        val kaabaLon = Math.toRadians(39.8262)
        val userLatRad = Math.toRadians(userLat)
        val userLonRad = Math.toRadians(userLon)

        val dLon = kaabaLon - userLonRad

        val y = Math.sin(dLon) * Math.cos(kaabaLat)
        val x = Math.cos(userLatRad) * Math.sin(kaabaLat) -
                Math.sin(userLatRad) * Math.cos(kaabaLat) * Math.cos(dLon)

        val bearing = Math.atan2(y, x)
        return (Math.toDegrees(bearing) + 360) % 360
    }

    /**
     * Check if cache is still valid
     */
    private fun isCacheValid(cacheKey: String): Boolean {
        if (cachedPrayerTimes?.first != cacheKey) return false
        val age = System.currentTimeMillis() - cacheTimestamp
        return age < CACHE_DURATION
    }

    /**
     * Clear prayer times cache
     */
    fun clearCache() {
        cachedPrayerTimes = null
        cacheTimestamp = 0
    }
}
