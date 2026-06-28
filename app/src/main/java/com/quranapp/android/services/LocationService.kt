package com.quranapp.android.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ===== Location Data Models =====

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val cityName: String? = null,
    val countryName: String? = null
)

data class LocationPermissionStatus(
    val fineLocationGranted: Boolean,
    val coarseLocationGranted: Boolean
) {
    val hasPermission: Boolean
        get() = fineLocationGranted || coarseLocationGranted
}

// ===== Location Service =====

class LocationService(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val geocoder = Geocoder(context, Locale.getDefault())
    private var lastKnownLocation: UserLocation? = null

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
        private const val LOCATION_FASTEST_INTERVAL = 5000L // 5 seconds
        private const val LOCATION_MAX_WAIT_TIME = 20000L // 20 seconds
    }

    // ===== Permission Management =====

    fun checkPermissionStatus(): LocationPermissionStatus {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return LocationPermissionStatus(fineLocationGranted, coarseLocationGranted)
    }

    fun hasLocationPermission(): Boolean {
        return checkPermissionStatus().hasPermission
    }

    // ===== Location Retrieval =====

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<UserLocation> = withContext(Dispatchers.IO) {
        runCatching {
            if (!hasLocationPermission()) {
                throw SecurityException("Location permissions not granted")
            }

            val location = suspendCancellableCoroutine<Location> { continuation ->
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                    .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
                    .setMaxUpdateDelayMillis(LOCATION_MAX_WAIT_TIME)
                    .build()

                fusedLocationClient.getCurrentLocation(
                    locationRequest.priority,
                    null
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(Exception("Failed to get current location"))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }

            val cityName = reverseGeocode(location.latitude, location.longitude)
            val countryName = reverseGeocodeCountry(location.latitude, location.longitude)

            val userLocation = UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestamp = location.time,
                cityName = cityName,
                countryName = countryName
            )

            lastKnownLocation = userLocation
            userLocation
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Result<UserLocation> = withContext(Dispatchers.IO) {
        runCatching {
            // Return cached location if available
            lastKnownLocation?.let { return@runCatching it }

            if (!hasLocationPermission()) {
                throw SecurityException("Location permissions not granted")
            }

            val location = suspendCancellableCoroutine<Location> { continuation ->
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(Exception("Last known location not available"))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }

            val cityName = reverseGeocode(location.latitude, location.longitude)
            val countryName = reverseGeocodeCountry(location.latitude, location.longitude)

            val userLocation = UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestamp = location.time,
                cityName = cityName,
                countryName = countryName
            )

            lastKnownLocation = userLocation
            userLocation
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocationWithTimeout(timeoutMs: Long = 10000): Result<UserLocation> = withContext(Dispatchers.IO) {
        runCatching {
            if (!hasLocationPermission()) {
                throw SecurityException("Location permissions not granted")
            }

            val location = suspendCancellableCoroutine<Location> { continuation ->
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_UPDATE_INTERVAL
                ).build()

                fusedLocationClient.getCurrentLocation(
                    locationRequest.priority,
                    null
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        continuation.resume(loc)
                    } else {
                        continuation.resumeWithException(Exception("Location is null"))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }

            val cityName = reverseGeocode(location.latitude, location.longitude)
            val countryName = reverseGeocodeCountry(location.latitude, location.longitude)

            val userLocation = UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestamp = location.time,
                cityName = cityName,
                countryName = countryName
            )

            lastKnownLocation = userLocation
            userLocation
        }
    }

    // ===== Reverse Geocoding =====

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    // Try to get city first, fall back to locality or admin area
                    address.locality ?: address.adminArea ?: address.countryName
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    private suspend fun reverseGeocodeCountry(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].countryName
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): Result<Address> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0]
                } else {
                    throw IOException("No address found for coordinates")
                }
            }
        }
    }

    // ===== Distance Calculation =====

    fun getDistanceInKm(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results)
        return results[0] / 1000 // Convert to kilometers
    }

    fun getDistanceInMeters(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results)
        return results[0]
    }

    // ===== Cache Management =====

    fun getLastKnownLocationSync(): UserLocation? {
        return lastKnownLocation
    }

    fun setLastKnownLocation(location: UserLocation) {
        lastKnownLocation = location
    }

    fun clearCachedLocation() {
        lastKnownLocation = null
    }
}
