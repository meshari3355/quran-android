package com.quranapp.android.ui.screens.prayer

import com.quranapp.android.ui.components.QuranLoadingIndicator

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.viewmodels.PrayerViewModel
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

// ========================================
// DATA MODELS
// ========================================

data class PrayerTimeDisplay(
    val name: String,
    val arabicName: String,
    val time: String,
    val icon: String,
    val isNotificationEnabled: Boolean = true
)

data class SavedCityData(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentCity: Boolean = false
)

private const val DEFAULT_RIYADH_LATITUDE = 24.7136
private const val DEFAULT_RIYADH_LONGITUDE = 46.6753
private val fixedCityCoordinates = mapOf(
    "الرياض" to (24.7136 to 46.6753),
    "جدة" to (21.5422 to 39.1721),
    "المدينة" to (24.5247 to 39.5692),
    "الدمام" to (26.4207 to 50.0888)
)

// ========================================
// MAIN SCREEN
// ========================================

@Composable
fun PrayerTimesScreen(
    onNavigateToQibla: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PrayerViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var cityName by remember { mutableStateOf("الرياض") }
    var showCitySelector by remember { mutableStateOf(false) }
    var notificationStates by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var savedCities by remember {
        mutableStateOf(
            listOf(
                SavedCityData("جدة", 21.5422, 39.1721),
                SavedCityData("المدينة", 24.5247, 39.5692)
            )
        )
    }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var prayerMethod by remember { mutableIntStateOf(10) }
    val locationPermissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            locationPermissions.any { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        hasLocationPermission = grantResults.values.any { it }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    // Load real prayer times from location
    LaunchedEffect(hasLocationPermission) {
        fun loadDefaultRiyadh() {
            cityName = "الرياض"
            viewModel.loadPrayerTimesByLocation(DEFAULT_RIYADH_LATITUDE, DEFAULT_RIYADH_LONGITUDE)
        }

        fun loadUserLocation(latitude: Double, longitude: Double) {
            cityName = "موقعي الحالي"
            viewModel.loadPrayerTimesByLocation(latitude, longitude)
        }

        if (!hasLocationPermission) {
            loadDefaultRiyadh()
            return@LaunchedEffect
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fun loadLastKnownOrDefault() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        loadUserLocation(location.latitude, location.longitude)
                    } else {
                        loadDefaultRiyadh()
                    }
                }.addOnFailureListener {
                    loadDefaultRiyadh()
                }
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    loadUserLocation(location.latitude, location.longitude)
                } else {
                    loadLastKnownOrDefault()
                }
            }.addOnFailureListener {
                loadLastKnownOrDefault()
            }
        } catch (e: SecurityException) {
            loadDefaultRiyadh()
        }
    }

    // Convert ViewModel prayer times to display format
    val prayerTimes = remember(uiState.currentPrayerTimes) {
        if (uiState.currentPrayerTimes.isNotEmpty()) {
            uiState.currentPrayerTimes.map { pt ->
                val (arabicName, icon) = mapPrayerType(pt.prayerType)
                PrayerTimeDisplay(
                    name = pt.prayerType.replaceFirstChar { it.uppercase() },
                    arabicName = arabicName,
                    time = String.format(Locale.US, "%02d:%02d", pt.hour, pt.minute),
                    icon = icon
                )
            }
        } else {
            listOf(
                PrayerTimeDisplay("Fajr", "الفجر", "--:--", "🌙"),
                PrayerTimeDisplay("Sunrise", "الشروق", "--:--", "🌅"),
                PrayerTimeDisplay("Dhuhr", "الظهر", "--:--", "☀️"),
                PrayerTimeDisplay("Asr", "العصر", "--:--", "🌤"),
                PrayerTimeDisplay("Maghrib", "المغرب", "--:--", "🌇"),
                PrayerTimeDisplay("Isha", "العشاء", "--:--", "🌑")
            )
        }
    }

    // Find next prayer
    val nextPrayerIndex = remember(uiState.currentPrayerTimes) {
        val cal = java.util.Calendar.getInstance()
        val currentMinutes = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
        val idx = uiState.currentPrayerTimes.indexOfFirst { it.hour * 60 + it.minute > currentMinutes }
        if (idx >= 0) idx else 0
    }
    val nextPrayer = prayerTimes.getOrElse(nextPrayerIndex) {
        prayerTimes.firstOrNull() ?: PrayerTimeDisplay("", "", "--:--", "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Loading overlay
        if (uiState.isLoadingPrayers && uiState.currentPrayerTimes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                QuranLoadingIndicator(color = colors.gold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.md)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
                PrayerScreenHeader(onNavigateToQibla = onNavigateToQibla)
            }

            // City Selector
            item {
                CitySelector(
                    cityName = cityName,
                    onClick = { showCitySelector = !showCitySelector }
                )
            }

            // City dropdown options
            if (showCitySelector) {
                items(fixedCityCoordinates.keys.toList(), key = { it }) { city ->
                    CityOption(
                        city = city,
                        isSelected = cityName == city,
                        onClick = {
                            cityName = city
                            showCitySelector = false
                            fixedCityCoordinates[city]?.let { (latitude, longitude) ->
                                viewModel.loadPrayerTimesByLocation(latitude, longitude)
                            }
                        }
                    )
                }
            }

            // Date Navigation
            item {
                DateNavigationCard(
                    currentDate = currentDate,
                    onPreviousDay = { currentDate = currentDate.minusDays(1) },
                    onNextDay = { currentDate = currentDate.plusDays(1) }
                )
            }

            // Next Prayer Countdown
            item {
                NextPrayerBanner(
                    prayer = nextPrayer,
                    timeRemaining = uiState.timeUntilNextPrayer
                )
            }

            // Prayer Times List
            items(prayerTimes, key = { it.name }) { prayer ->
                PrayerTimeRow(
                    prayer = prayer,
                    isNext = prayer == nextPrayer,
                    isNotificationEnabled = notificationStates[prayer.name] ?: true,
                    onNotificationToggle = {
                        notificationStates = notificationStates.toMutableMap().apply {
                            put(prayer.name, !(get(prayer.name) ?: true))
                        }
                    }
                )
            }

            // Prayer Method Card
            item {
                PrayerMethodCard(
                    method = prayerMethod,
                    onMethodChange = { prayerMethod = it }
                )
            }

            // Saved Cities Section
            item {
                SavedCitiesHeader()
            }

            items(savedCities, key = { it.name }) { savedCity ->
                SavedCityCard(
                    city = savedCity.name,
                    onView = {
                        cityName = savedCity.name
                        showCitySelector = false
                        viewModel.loadPrayerTimesByLocation(savedCity.latitude, savedCity.longitude)
                    },
                    onDelete = { savedCities = savedCities.filter { it.name != savedCity.name } }
                )
            }

            item { Spacer(modifier = Modifier.height(AppDesign.spacing.xxl)) }
        }
    }
}

// ========================================
// HEADER
// ========================================

@Composable
private fun PrayerScreenHeader(onNavigateToQibla: () -> Unit) {
    val colors = AppDesign.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quick action chips
        Row(horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)) {
            QuickActionChip(
                icon = Icons.Default.Notifications,
                label = "صوت النظام",
                chipColor = colors.goldContainer,
                iconTint = colors.gold,
                onClick = { }
            )
            QuickActionChip(
                icon = Icons.Default.Navigation,
                label = "القبلة",
                chipColor = colors.info.copy(alpha = 0.1f),
                iconTint = colors.info,
                onClick = onNavigateToQibla
            )
        }

        Text(
            text = "أوقات الصلاة",
            fontSize = 26.sp,
            fontFamily = AmiriFont,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    chipColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Surface(
        modifier = Modifier
            .clip(AppDesign.radius.input)
            .clickable(onClick = onClick),
        color = chipColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
            Text(label, fontSize = 9.sp, color = colors.textPrimary)
        }
    }
}

// ========================================
// CITY SELECTOR
// ========================================

@Composable
private fun CitySelector(
    cityName: String,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppDesign.radius.input,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "المدينة",
                    tint = colors.gold,
                    modifier = Modifier.size(AppDesign.iconSize.medium)
                )
                Column {
                    Text(
                        text = "المدينة",
                        fontSize = 12.sp,
                        color = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = cityName,
                        fontSize = 16.sp,
                        fontFamily = AmiriFont,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "اختيار",
                tint = colors.gold
            )
        }
    }
}

@Composable
private fun CityOption(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.goldContainer else colors.card
        ),
        border = if (isSelected) BorderStroke(2.dp, colors.gold)
        else BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city,
                fontSize = 14.sp,
                color = if (isSelected) colors.gold else colors.textPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "مختار",
                    tint = colors.gold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ========================================
// DATE NAVIGATION
// ========================================

@Composable
private fun DateNavigationCard(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.input,
        colors = CardDefaults.cardColors(containerColor = colors.goldContainer),
        border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNextDay, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "اليوم التالي",
                    tint = colors.gold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentDate.format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ar"))
                    ),
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentDate.format(
                        DateTimeFormatter.ofPattern("EEEE", Locale("ar"))
                    ),
                    fontSize = 12.sp,
                    fontFamily = AmiriFont,
                    color = colors.gold,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onPreviousDay, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "اليوم السابق",
                    tint = colors.gold
                )
            }
        }
    }
}

// ========================================
// NEXT PRAYER BANNER
// ========================================

@Composable
private fun NextPrayerBanner(
    prayer: PrayerTimeDisplay,
    timeRemaining: String
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.goldContainer),
        border = BorderStroke(2.dp, colors.gold.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.xl)
        ) {
            Text(
                text = "الصلاة القادمة",
                fontSize = 13.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(AppDesign.spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = prayer.arabicName,
                        fontSize = 32.sp,
                        fontFamily = AmiriFont,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = prayer.time,
                        fontSize = 14.sp,
                        color = colors.gold,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(AppDesign.radius.input)
                        .background(colors.surfaceVariant)
                        .padding(AppDesign.spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "متبقي",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = timeRemaining,
                            fontSize = 20.sp,
                            color = colors.gold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ========================================
// PRAYER TIME ROW
// ========================================

@Composable
private fun PrayerTimeRow(
    prayer: PrayerTimeDisplay,
    isNext: Boolean,
    isNotificationEnabled: Boolean,
    onNotificationToggle: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(
            containerColor = if (isNext) colors.goldContainer else colors.card
        ),
        border = if (isNext) BorderStroke(1.5.dp, colors.gold)
        else BorderStroke(1.dp, colors.borderSubtle),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isNext) AppDesign.elevation.card else AppDesign.elevation.subtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prayer name with icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = prayer.icon, fontSize = 22.sp)
                Column {
                    Text(
                        text = prayer.arabicName,
                        fontSize = 15.sp,
                        fontFamily = AmiriFont,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = prayer.name,
                        fontSize = 11.sp,
                        color = colors.textTertiary
                    )
                }
            }

            // Time
            Text(
                text = prayer.time,
                fontSize = 14.sp,
                color = if (isNext) colors.gold else colors.textPrimary,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold
            )

            // Notification toggle
            IconButton(onClick = onNotificationToggle, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = if (isNotificationEnabled) Icons.Default.Notifications
                    else Icons.Default.NotificationsNone,
                    contentDescription = "تنبيهات",
                    tint = if (isNotificationEnabled) colors.gold else colors.disabled,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ========================================
// PRAYER METHOD CARD
// ========================================

@Composable
private fun PrayerMethodCard(
    method: Int,
    onMethodChange: (Int) -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
        ) {
            Text(
                text = "طريقة الحساب",
                fontSize = 13.sp,
                fontFamily = AmiriFont,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
            ) {
                MethodChip("أم القرى", method == 10) { onMethodChange(10) }
                MethodChip("كراتشي", method == 1) { onMethodChange(1) }
                MethodChip("ISNA", method == 2) { onMethodChange(2) }
            }
        }
    }
}

@Composable
private fun MethodChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) colors.gold else colors.gold.copy(alpha = 0.1f)
        ),
        shape = AppDesign.radius.small,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        border = if (isSelected) null else BorderStroke(1.dp, colors.gold.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) {
                colors.goldOnContainer
            } else colors.gold
        )
    }
}

// ========================================
// SAVED CITIES
// ========================================

@Composable
private fun SavedCitiesHeader() {
    val colors = AppDesign.colors

    Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "مدن محفوظة",
            fontSize = 16.sp,
            fontFamily = AmiriFont,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "إضافة مدينة",
                tint = colors.gold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SavedCityCard(
    city: String,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.subtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city,
                fontSize = 14.sp,
                fontFamily = AmiriFont,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onView,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.gold.copy(alpha = 0.15f)
                    ),
                    shape = AppDesign.radius.small,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(text = "عرض", fontSize = 11.sp, color = colors.gold)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "حذف",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ========================================
// UTILITY
// ========================================

private fun mapPrayerType(prayerType: String): Pair<String, String> {
    return when (prayerType.lowercase()) {
        "fajr" -> "الفجر" to "🌙"
        "sunrise" -> "الشروق" to "🌅"
        "dhuhr" -> "الظهر" to "☀️"
        "asr" -> "العصر" to "🌤"
        "maghrib" -> "المغرب" to "🌇"
        "isha" -> "العشاء" to "🌑"
        else -> prayerType to "🕌"
    }
}
