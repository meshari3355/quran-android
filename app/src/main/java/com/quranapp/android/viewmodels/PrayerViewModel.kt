package com.quranapp.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.data.repository.PrayerRepository
import com.quranapp.android.models.SavedCity
import com.quranapp.android.models.ServerPrayerTimes
import com.quranapp.android.services.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val country: String,
    val state: String? = null,
    val isSelected: Boolean = false
)

data class LocalPrayerTime(
    val hour: Int,
    val minute: Int,
    val prayerType: String
)

data class QiblaData(
    val bearing: Double,
    val distance: Double
)

data class PrayerUiState(
    val currentPrayerTimes: List<LocalPrayerTime> = emptyList(),
    val nextPrayerName: String = "Maghrib",
    val nextPrayerTime: String = "18:30",
    val timeUntilNextPrayer: String = "02:30:45",
    val cities: List<City> = emptyList(),
    val selectedCityIndex: Int = 0,
    val qiblaData: QiblaData = QiblaData(bearing = 0.0, distance = 0.0),
    val isLoadingPrayers: Boolean = false,
    val isLoadingQibla: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val notificationService: NotificationService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    companion object {
        private const val DEFAULT_LATITUDE = 24.7136
        private const val DEFAULT_LONGITUDE = 46.6753
    }

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        loadPrayerTimes()
        startPrayerCountdownTimer()
    }

    fun loadPrayerTimes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPrayers = true, error = null) }
            try {
                val selectedCity = _uiState.value.cities.getOrNull(_uiState.value.selectedCityIndex)
                val latitude = selectedCity?.latitude ?: DEFAULT_LATITUDE
                val longitude = selectedCity?.longitude ?: DEFAULT_LONGITUDE
                preferencesManager.saveLastPrayerLocation(latitude, longitude)
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                prayerRepository.getPrayerTimesStream(
                    latitude,
                    longitude,
                    currentDate,
                    10
                ).collect { result ->
                    result.onSuccess { prayerResponse ->
                        val prayerTimes = parsePrayerResponse(prayerResponse)
                        _uiState.update {
                            it.copy(
                                currentPrayerTimes = prayerTimes,
                                isLoadingPrayers = false,
                                error = null
                            )
                        }
                        updateNextPrayer()
                        schedulePrayerNotifications(prayerTimes)
                    }
                    result.onFailure { e ->
                        _uiState.update { it.copy(isLoadingPrayers = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingPrayers = false, error = e.message) }
            }
        }
    }

    fun loadCities() {
        viewModelScope.launch {
            try {
                val result = prayerRepository.getSavedCities()
                result.onSuccess { savedCities ->
                    val cities = savedCities.map { savedCity ->
                        City(
                            id = savedCity.id?.toLongOrNull() ?: 0,
                            name = savedCity.name,
                            latitude = savedCity.latitude,
                            longitude = savedCity.longitude,
                            timezone = savedCity.timezone ?: "UTC",
                            country = savedCity.country ?: "",
                            state = savedCity.state
                        )
                    }
                    _uiState.update { it.copy(cities = cities) }
                }
                result.onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            try {
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                val savedCity = SavedCity(
                    id = city.id.toString(),
                    name = city.name,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    timezone = city.timezone,
                    country = city.country,
                    state = city.state,
                    date = currentDate,
                    prayerTimes = emptyMap()
                )
                prayerRepository.saveCityPreference(savedCity)
                val cities = _uiState.value.cities + city
                _uiState.update { it.copy(cities = cities) }
                loadPrayerTimes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeCity(cityId: Long) {
        viewModelScope.launch {
            try {
                val city = _uiState.value.cities.find { it.id == cityId }
                if (city != null) {
                    prayerRepository.removeSavedCity(city.name)
                    val cities = _uiState.value.cities.filter { it.id != cityId }
                    var newIndex = _uiState.value.selectedCityIndex
                    if (newIndex >= cities.size) {
                        newIndex = (cities.size - 1).coerceAtLeast(0)
                    }
                    _uiState.update { it.copy(cities = cities, selectedCityIndex = newIndex) }
                    loadPrayerTimes()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun switchCity(cityIndex: Int) {
        if (cityIndex in _uiState.value.cities.indices) {
            _uiState.update { it.copy(selectedCityIndex = cityIndex) }
            loadPrayerTimes()
        }
    }

    fun loadQiblaData(userLatitude: Double, userLongitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingQibla = true) }
            try {
                prayerRepository.getQiblaDirectionStream(userLatitude, userLongitude).collect { result ->
                    result.onSuccess { bearing ->
                        val distance = calculateDistance(
                            userLatitude, userLongitude,
                            21.4225, 39.8262
                        )
                        _uiState.update {
                            it.copy(
                                qiblaData = QiblaData(bearing = bearing, distance = distance),
                                isLoadingQibla = false
                            )
                        }
                    }
                    result.onFailure { e ->
                        _uiState.update { it.copy(isLoadingQibla = false, error = e.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingQibla = false, error = e.message) }
            }
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) *
                Math.cos(lat1Rad) * Math.cos(lat2Rad)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }

    private fun updateNextPrayer() {
        try {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val prayerTimes = _uiState.value.currentPrayerTimes
            if (prayerTimes.isEmpty()) return

            var nextPrayer: LocalPrayerTime? = null

            for (prayer in prayerTimes) {
                if (prayer.hour > currentHour ||
                    (prayer.hour == currentHour && prayer.minute > currentMinute)) {
                    nextPrayer = prayer
                    break
                }
            }

            if (nextPrayer == null) {
                nextPrayer = prayerTimes.firstOrNull()
            }

            nextPrayer?.let {
                val nextName = getPrayerName(it.prayerType)
                val nextTime = String.format("%02d:%02d", it.hour, it.minute)

                _uiState.update { state ->
                    state.copy(
                        nextPrayerName = nextName,
                        nextPrayerTime = nextTime
                    )
                }
                preferencesManager.saveNextPrayer(nextName, nextTime)
                notificationService.updatePrayerTimesWidget()
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    private fun schedulePrayerNotifications(prayerTimes: List<LocalPrayerTime>) {
        if (!preferencesManager.getNotificationsEnabled() || !preferencesManager.getPrayerNotificationsEnabled()) {
            return
        }

        val prayerMap = prayerTimes
            .filter { it.prayerType.lowercase() != "sunrise" }
            .associate { prayer ->
                getPrayerName(prayer.prayerType) to String.format("%02d:%02d", prayer.hour, prayer.minute)
            }

        if (prayerMap.isEmpty()) return

        viewModelScope.launch {
            notificationService.schedulePrayerNotifications(
                prayerTimes = prayerMap,
                reminderMinutesBefore = preferencesManager.getPrayerNotificationTime()
            )
        }
    }

    private fun startPrayerCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                try {
                    updateCountdownTime()
                    kotlinx.coroutines.delay(1000)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message) }
                    break
                }
            }
        }
    }

    private fun updateCountdownTime() {
        try {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentSecond = calendar.get(Calendar.SECOND)

            val nextPrayerTimeParts = _uiState.value.nextPrayerTime.split(":")
            if (nextPrayerTimeParts.size != 2) return

            val nextHour = nextPrayerTimeParts[0].toIntOrNull() ?: return
            val nextMinute = nextPrayerTimeParts[1].toIntOrNull() ?: return

            val currentTotalSeconds = currentHour * 3600 + currentMinute * 60 + currentSecond
            val nextTotalSeconds = nextHour * 3600 + nextMinute * 60

            val diffSeconds = if (nextTotalSeconds > currentTotalSeconds) {
                nextTotalSeconds - currentTotalSeconds
            } else {
                (24 * 3600) - currentTotalSeconds + nextTotalSeconds
            }

            val hours = diffSeconds / 3600
            val minutes = (diffSeconds % 3600) / 60
            val seconds = diffSeconds % 60

            val countdownStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            _uiState.update { it.copy(timeUntilNextPrayer = countdownStr) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    private fun getPrayerName(prayerType: String): String {
        return when (prayerType.lowercase()) {
            "fajr" -> "الفجر"
            "sunrise" -> "الشروق"
            "dhuhr" -> "الظهر"
            "asr" -> "العصر"
            "maghrib" -> "المغرب"
            "isha" -> "العشاء"
            else -> prayerType
        }
    }

    /**
     * Load prayer times by coordinates directly (for location-based loading)
     */
    fun loadPrayerTimesByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPrayers = true, error = null) }
            try {
                preferencesManager.saveLastPrayerLocation(latitude, longitude)
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                prayerRepository.getPrayerTimesStream(latitude, longitude, currentDate, 10)
                    .collect { result ->
                        result.onSuccess { prayerResponse ->
                            val prayerTimes = parsePrayerResponse(prayerResponse)
                            _uiState.update {
                                it.copy(
                                    currentPrayerTimes = prayerTimes,
                                    isLoadingPrayers = false,
                                    error = null
                                )
                            }
                            updateNextPrayer()
                            schedulePrayerNotifications(prayerTimes)
                        }
                        result.onFailure { e ->
                            _uiState.update { it.copy(isLoadingPrayers = false, error = e.message) }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingPrayers = false, error = e.message) }
            }
        }
    }

    /**
     * Parse ServerPrayerTimes response into list of LocalPrayerTime
     * Handles both nested prayers object and flat field formats
     */
    private fun parsePrayerResponse(response: ServerPrayerTimes): List<LocalPrayerTime> {
        val prayerTimes = mutableListOf<LocalPrayerTime>()
        response.getFajrTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "fajr")) }
        }
        response.getSunriseTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "sunrise")) }
        }
        response.getDhuhrTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "dhuhr")) }
        }
        response.getAsrTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "asr")) }
        }
        response.getMaghribTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "maghrib")) }
        }
        response.getIshaTime()?.let { time ->
            parseTime(time)?.let { (h, m) -> prayerTimes.add(LocalPrayerTime(h, m, "isha")) }
        }
        return prayerTimes
    }

    /**
     * Parse time string like "05:30" or "05:30 (PKT)" to hour/minute pair
     */
    private fun parseTime(timeStr: String): Pair<Int, Int>? {
        return try {
            val cleaned = timeStr.split(" ").first().trim()
            val parts = cleaned.split(":")
            if (parts.size >= 2) {
                Pair(parts[0].toInt(), parts[1].toInt())
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
        value = function(value)
    }

}
