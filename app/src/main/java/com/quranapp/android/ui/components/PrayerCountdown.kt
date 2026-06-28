package com.quranapp.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quranapp.android.models.PrayerName
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

/**
 * Prayer countdown component showing next prayer time with animated timer
 * Supports compact and expanded modes with prayer-specific theming
 */
@Composable
fun PrayerCountdown(
    nextPrayerName: String = "العصر",
    timeUntilPrayer: Long = 3661,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    var seconds by remember { mutableStateOf(timeUntilPrayer) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(isPlaying) {
        while (isPlaying && seconds > 0) {
            delay(1000)
            seconds--
        }
    }

    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    val secs = (seconds % 60).toInt()

    val prayerColor = getPrayerColor(nextPrayerName)
    val animatedColor by animateColorAsState(prayerColor)

    if (isExpanded) {
        ExpandedPrayerCountdown(
            prayerName = nextPrayerName,
            hours = hours,
            minutes = minutes,
            seconds = secs,
            backgroundColor = animatedColor,
            modifier = modifier
        )
    } else {
        CompactPrayerCountdown(
            prayerName = nextPrayerName,
            hours = hours,
            minutes = minutes,
            seconds = secs,
            backgroundColor = animatedColor,
            modifier = modifier,
            onTap = onTap
        )
    }
}

@Composable
private fun CompactPrayerCountdown(
    prayerName: String,
    hours: Int,
    minutes: Int,
    seconds: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.9f),
                        backgroundColor.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "الصلاة القادمة",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = prayerName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ExpandedPrayerCountdown(
    prayerName: String,
    hours: Int,
    minutes: Int,
    seconds: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.95f),
                        backgroundColor.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "الصلاة القادمة",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = prayerName,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TimerUnit(value = hours, label = "ساعة")
                TimerUnit(value = minutes, label = "دقيقة")
                TimerUnit(value = seconds, label = "ثانية")
            }
        }
    }
}

@Composable
private fun TimerUnit(
    value: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun getPrayerColor(prayerName: String): Color {
    return when (prayerName.trim()) {
        "الفجر" -> Color(0xFF1a237e) // Dark blue
        "الظهر" -> Color(0xFFffc107) // Bright yellow/gold
        "العصر" -> Color(0xFFff8a50) // Warm orange
        "المغرب" -> Color(0xFFff6f00) // Deep orange
        "العشاء" -> Color(0xFF0d1854) // Very dark blue
        else -> Color(0xFF1a237e)
    }
}

@Composable
fun AnimatedEqualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val bars = remember { mutableStateOf(listOf(20, 40, 60, 40, 20)) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            bars.value = (1..5).map { (Math.random() * 100).toInt() }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        bars.value.forEach { height ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((height / 4).dp)
                    .background(
                        color = Color(0xFFFFB74D),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
