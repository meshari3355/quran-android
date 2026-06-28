package com.quranapp.android.ui.screens.prayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Canvas
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import java.util.Locale
import kotlin.math.*

private const val DEFAULT_RIYADH_LATITUDE = 24.7136
private const val DEFAULT_RIYADH_LONGITUDE = 46.6753

@Composable
fun QiblaScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val colors = AppDesign.colors

    // Real sensor data
    var compassHeading by remember { mutableFloatStateOf(0f) }
    var qiblaBearing by remember { mutableStateOf(0.0) }
    var distanceToKaaba by remember { mutableStateOf(0.0) }
    var isCalibrated by remember { mutableStateOf(true) }
    var sensorAccuracy by remember { mutableStateOf(SensorManager.SENSOR_STATUS_ACCURACY_HIGH) }
    var hasLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
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

    // Kaaba coordinates
    val kaabaLat = 21.4225
    val kaabaLon = 39.8262

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    // Get user location
    LaunchedEffect(hasLocationPermission) {
        fun useDefaultLocation(message: String) {
            qiblaBearing = calculateQiblaBearing(DEFAULT_RIYADH_LATITUDE, DEFAULT_RIYADH_LONGITUDE, kaabaLat, kaabaLon)
            distanceToKaaba = calculateDistance(DEFAULT_RIYADH_LATITUDE, DEFAULT_RIYADH_LONGITUDE, kaabaLat, kaabaLon)
            hasLocation = true
            locationError = message
        }

        if (!hasLocationPermission) {
            useDefaultLocation("يتم استخدام موقع الرياض الافتراضي - امنح الموقع لدقة أعلى")
            return@LaunchedEffect
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fun loadQiblaFromLocation(latitude: Double, longitude: Double) {
                qiblaBearing = calculateQiblaBearing(latitude, longitude, kaabaLat, kaabaLon)
                distanceToKaaba = calculateDistance(latitude, longitude, kaabaLat, kaabaLon)
                hasLocation = true
                locationError = null
            }

            fun loadLastKnownOrDefault() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        loadQiblaFromLocation(location.latitude, location.longitude)
                    } else {
                        useDefaultLocation("يتم استخدام موقع الرياض الافتراضي - امنح الموقع لدقة أعلى")
                    }
                }.addOnFailureListener {
                    useDefaultLocation("يتم استخدام موقع الرياض الافتراضي - امنح الموقع لدقة أعلى")
                }
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    loadQiblaFromLocation(location.latitude, location.longitude)
                } else {
                    loadLastKnownOrDefault()
                }
            }.addOnFailureListener {
                loadLastKnownOrDefault()
            }
        } catch (e: SecurityException) {
            useDefaultLocation("يتم استخدام موقع الرياض الافتراضي - امنح الموقع لدقة أعلى")
        }
    }

    // Real compass sensor
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val accelerometerData = FloatArray(3)
        val magnetometerData = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> System.arraycopy(event.values, 0, accelerometerData, 0, 3)
                    Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, magnetometerData, 0, 3)
                }

                val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData)
                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val heading = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    compassHeading = (heading + 360) % 360
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                sensorAccuracy = accuracy
                isCalibrated = accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE
            }
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Calculate the rotation for the qibla indicator
    val qiblaRotation = (qiblaBearing - compassHeading).toFloat()
    val animatedQiblaRotation by animateFloatAsState(
        targetValue = qiblaRotation,
        animationSpec = tween(durationMillis = 300),
        label = "qibla_rotation"
    )
    val animatedCompassRotation by animateFloatAsState(
        targetValue = -compassHeading,
        animationSpec = tween(durationMillis = 300),
        label = "compass_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.goldContainer.copy(alpha = 0.5f),
                        colors.background,
                        colors.goldContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = colors.gold, modifier = Modifier.size(24.dp))
                    }
                    Text("القبلة", fontSize = 22.sp, fontFamily = AmiriFont, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Info, "معلومات", tint = colors.gold, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Compass Area
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                ) {
                    // Compass
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .background(colors.card, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Compass ring with degree marks - rotates with device heading
                        Canvas(modifier = Modifier.size(280.dp).rotate(animatedCompassRotation)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val radius = size.width / 2 - 20f

                            // Outer circle
                            drawCircle(
                                color = colors.gold.copy(alpha = 0.2f),
                                radius = radius,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                            )
                            // Inner circle
                            drawCircle(
                                color = colors.goldContainer.copy(alpha = 0.4f),
                                radius = radius * 0.7f,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f)
                            )
                            // Degree marks
                            for (i in 0..359 step 10) {
                                val angleRad = i * (Math.PI / 180f)
                                val startR = radius - 5f
                                val endR = radius
                                val startX = centerX + (startR * sin(angleRad)).toFloat()
                                val startY = centerY - (startR * cos(angleRad)).toFloat()
                                val endX = centerX + (endR * sin(angleRad)).toFloat()
                                val endY = centerY - (endR * cos(angleRad)).toFloat()
                                drawLine(
                                    color = if (i % 30 == 0) colors.gold else colors.gold.copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(startX, startY),
                                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                                    strokeWidth = if (i % 30 == 0) 2f else 1f
                                )
                            }
                            // Center circle
                            drawCircle(color = colors.gold, radius = 8f, center = androidx.compose.ui.geometry.Offset(centerX, centerY))
                        }

                        // Cardinal directions - rotate with compass
                        Box(modifier = Modifier.size(280.dp).rotate(animatedCompassRotation)) {
                            Text("شمال", fontSize = 12.sp, color = colors.error, fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp))
                            Text("شرق", fontSize = 12.sp, color = colors.textSecondary.copy(alpha = 0.8f), fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
                            Text("جنوب", fontSize = 12.sp, color = colors.textSecondary.copy(alpha = 0.8f), fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp))
                            Text("غرب", fontSize = 12.sp, color = colors.textSecondary.copy(alpha = 0.8f), fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp))
                        }

                        // Qibla needle - points toward Qibla (rotates relative to heading)
                        Box(modifier = Modifier.size(180.dp).rotate(animatedQiblaRotation)) {
                            // Gold needle pointing to Qibla
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(80.dp)
                                    .background(colors.gold, RoundedCornerShape(2.dp))
                                    .align(Alignment.TopCenter)
                            )
                            // Kaaba indicator at the tip
                            Icon(
                                Icons.Default.Place,
                                contentDescription = "الكعبة",
                                tint = colors.gold,
                                modifier = Modifier.size(20.dp).align(Alignment.TopCenter).offset(y = (-10).dp)
                            )
                        }

                        // North indicator needle (red) - rotates opposite to heading
                        Box(modifier = Modifier.size(180.dp).rotate(animatedCompassRotation)) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(40.dp)
                                    .background(colors.error, RoundedCornerShape(2.dp))
                                    .align(Alignment.TopCenter)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Degree Display
                    Card(
                        modifier = Modifier.animateContentSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.goldContainer),
                        border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.3f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("اتجاه القبلة", fontSize = 12.sp, color = colors.textSecondary.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (hasLocation) String.format(Locale.US, "%.1f°", qiblaBearing) else "...",
                                fontSize = 28.sp, color = colors.gold, fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Distance to Kaaba
                    Card(
                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.goldContainer.copy(alpha = 0.7f)),
                        border = BorderStroke(1.5.dp, colors.gold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, "المسافة", tint = colors.gold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("المسافة إلى الكعبة المشرفة", fontSize = 11.sp, color = colors.textSecondary.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (hasLocation) String.format(Locale.US, "%.0f كم", distanceToKaaba) else "...",
                                    fontSize = 16.sp, color = colors.gold, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom info cards
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isCalibrated) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.warning.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, colors.warning.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, "تنبيه", tint = colors.warning, modifier = Modifier.size(18.dp))
                            Text("يرجى معايرة البوصلة بتحريك الجهاز في شكل رقم 8", fontSize = 11.sp, color = colors.textPrimary, modifier = Modifier.weight(1f))
                        }
                    }
                }

                locationError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.warning.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOff, "موقع", tint = colors.warning, modifier = Modifier.size(18.dp))
                            Text(error, fontSize = 11.sp, color = colors.warning, modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Accuracy indicator
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "الموقع", tint = colors.gold, modifier = Modifier.size(18.dp))
                            Column {
                                Text("دقة البوصلة", fontSize = 10.sp, color = colors.textSecondary.copy(alpha = 0.7f))
                                Text(
                                    text = when (sensorAccuracy) {
                                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "عالية"
                                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "متوسطة"
                                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "منخفضة"
                                        else -> "تحتاج معايرة"
                                    },
                                    fontSize = 12.sp, color = colors.textPrimary, fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                            color = when (sensorAccuracy) {
                                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> colors.success.copy(alpha = 0.12f)
                                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> colors.gold.copy(alpha = 0.12f)
                                else -> colors.error.copy(alpha = 0.12f)
                            }
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.0f°", compassHeading),
                                fontSize = 11.sp, color = colors.gold, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun calculateQiblaBearing(userLat: Double, userLon: Double, kaabaLat: Double, kaabaLon: Double): Double {
    val dLon = Math.toRadians(kaabaLon - userLon)
    val lat1Rad = Math.toRadians(userLat)
    val lat2Rad = Math.toRadians(kaabaLat)
    val y = sin(dLon) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
    val bearing = Math.toDegrees(atan2(y, x))
    return (bearing + 360) % 360
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * asin(sqrt(a))
    return earthRadiusKm * c
}
