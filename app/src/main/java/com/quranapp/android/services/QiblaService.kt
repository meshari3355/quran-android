package com.quranapp.android.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

// ===== Qibla Direction Data Models =====

data class QiblaDirection(
    val bearing: Double, // 0-360 degrees
    val distance: Double // in kilometers
) {
    val bearingFormatted: String
        get() = String.format("%.1f°", bearing)

    val distanceFormatted: String
        get() = String.format("%.1f km", distance)

    val compassDirection: String
        get() = when {
            bearing < 11.25 || bearing >= 348.75 -> "N"
            bearing < 33.75 -> "NNE"
            bearing < 56.25 -> "NE"
            bearing < 78.75 -> "ENE"
            bearing < 101.25 -> "E"
            bearing < 123.75 -> "ESE"
            bearing < 146.25 -> "SE"
            bearing < 168.75 -> "SSE"
            bearing < 191.25 -> "S"
            bearing < 213.75 -> "SSW"
            bearing < 236.25 -> "SW"
            bearing < 258.75 -> "WSW"
            bearing < 281.25 -> "W"
            bearing < 303.75 -> "WNW"
            bearing < 326.25 -> "NW"
            else -> "NNW"
        }
}

data class CompassState(
    val heading: Float = 0f, // 0-360 degrees, direction device is pointing
    val accuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE,
    val lastUpdate: Long = 0L
) {
    val isAccurate: Boolean
        get() = accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE
}

// ===== Qibla Service =====

class QiblaService(
    private val context: Context,
    private val locationService: LocationService
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var compassListener: SensorEventListener? = null
    private val accelerometerData = FloatArray(3)
    private val magnetometerData = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val _compassState = MutableStateFlow(CompassState())
    val compassState: StateFlow<CompassState> = _compassState.asStateFlow()

    companion object {
        private const val KAABA_LATITUDE = 21.4225
        private const val KAABA_LONGITUDE = 39.8262
        private const val EARTH_RADIUS_KM = 6371.0
    }

    // ===== Qibla Calculation =====

    suspend fun calculateQibla(latitude: Double, longitude: Double): Result<QiblaDirection> {
        return runCatching {
            val bearing = calculateBearing(latitude, longitude, KAABA_LATITUDE, KAABA_LONGITUDE)
            val distance = calculateDistance(latitude, longitude, KAABA_LATITUDE, KAABA_LONGITUDE)

            QiblaDirection(bearing, distance)
        }
    }

    suspend fun calculateQiblaFromCurrentLocation(): Result<QiblaDirection> {
        return runCatching {
            val location = locationService.getCurrentLocation().getOrThrow()
            calculateQibla(location.latitude, location.longitude).getOrThrow()
        }
    }

    // ===== Compass Heading Management =====

    fun startCompassListener(onHeadingUpdate: (CompassState) -> Unit = {}) {
        if (accelerometer == null || magnetometer == null) {
            throw Exception("Device does not have required sensors for compass")
        }

        compassListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, accelerometerData, 0, 3)
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, magnetometerData, 0, 3)
                    }
                }

                // Calculate rotation matrix
                val success = SensorManager.getRotationMatrix(
                    rotationMatrix, null,
                    accelerometerData, magnetometerData
                )

                if (success) {
                    // Get orientation angles (azimuth, pitch, roll)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    // azimuth is in radians, convert to degrees
                    val heading = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    val normalizedHeading = (heading + 360) % 360

                    val newState = CompassState(
                        heading = normalizedHeading,
                        accuracy = event.accuracy,
                        lastUpdate = System.currentTimeMillis()
                    )

                    _compassState.value = newState
                    onHeadingUpdate(newState)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                val currentState = _compassState.value
                _compassState.value = currentState.copy(accuracy = accuracy)
            }
        }

        compassListener?.let {
            sensorManager.registerListener(it, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(it, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopCompassListener() {
        compassListener?.let {
            sensorManager.unregisterListener(it)
        }
        compassListener = null
    }

    fun getCompassHeading(): Float {
        return _compassState.value.heading
    }

    fun isCompassAccurate(): Boolean {
        return _compassState.value.isAccurate
    }

    // ===== Helper Methods =====

    private fun calculateBearing(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) -
                sin(lat1Rad) * cos(lat2Rad) * cos(dLon)

        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)

        val c = 2 * asin(sqrt(a))

        return EARTH_RADIUS_KM * c
    }

    fun getRelativeBearing(qiblaBearing: Double, deviceHeading: Float): Double {
        var relative = qiblaBearing - deviceHeading
        while (relative < 0) relative += 360
        while (relative >= 360) relative -= 360
        return relative
    }

    fun getQiblaIndicatorAngle(qiblaBearing: Double, deviceHeading: Float): Double {
        val relative = getRelativeBearing(qiblaBearing, deviceHeading)
        return relative
    }
}
