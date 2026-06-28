package com.quranapp.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Moon phase visualization based on Hijri calendar day
 * Shows different phases: crescent, half, gibbous, full moon
 */
@Composable
fun MoonPhaseView(
    hijriDay: Int = 15,
    modifier: Modifier = Modifier
) {
    val phase = calculateMoonPhase(hijriDay)
    val phaseName = getMoonPhaseName(phase)
    val phaseArabic = getMoonPhaseArabic(hijriDay)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFAF0E6),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "مراحل القمر",
            color = Color(0xFF3E2723),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        MoonPhaseCanvas(
            phase = phase,
            modifier = Modifier
                .size(80.dp)
                .background(Color(0x1A1a237e), CircleShape)
        )

        Text(
            text = phaseArabic,
            color = Color(0xFF5D4037),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "اليوم الهجري: $hijriDay",
            color = Color(0xFF795548),
            fontSize = 10.sp
        )
    }
}

/**
 * Canvas-based moon phase drawing
 */
@Composable
fun MoonPhaseCanvas(
    phase: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = modifier) {
            drawMoonPhase(phase)
        }
    }
}

private fun DrawScope.drawMoonPhase(phase: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.width / 2 - 2

    // Dark background (night sky)
    drawCircle(
        color = Color(0xFF0d1854),
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )

    // Moon phase calculation
    // phase: 0 = new moon, 0.25 = crescent, 0.5 = half, 0.75 = gibbous, 1 = full
    when {
        phase < 0.25 -> {
            // New to crescent
            drawCrescentMoon(centerX, centerY, radius, phase / 0.25f)
        }
        phase < 0.5 -> {
            // Crescent to half
            drawHalfMoon(centerX, centerY, radius, (phase - 0.25f) / 0.25f)
        }
        phase < 0.75 -> {
            // Half to gibbous
            drawGibbousMoon(centerX, centerY, radius, (phase - 0.5f) / 0.25f)
        }
        else -> {
            // Gibbous to full
            drawFullMoon(centerX, centerY, radius, (phase - 0.75f) / 0.25f)
        }
    }
}

private fun DrawScope.drawCrescentMoon(
    centerX: Float,
    centerY: Float,
    radius: Float,
    progress: Float
) {
    val moonRadius = radius * 0.7f
    val shadowOffset = moonRadius * progress

    // Draw main moon circle
    drawCircle(
        color = Color(0xFFFFF8DC),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )

    // Draw shadow for crescent effect
    drawCircle(
        color = Color(0xFF0d1854),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX + shadowOffset, centerY)
    )
}

private fun DrawScope.drawHalfMoon(
    centerX: Float,
    centerY: Float,
    radius: Float,
    progress: Float
) {
    val moonRadius = radius * 0.7f
    val shadowOffset = moonRadius * progress

    // Draw main moon circle
    drawCircle(
        color = Color(0xFFFFF8DC),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )

    // Draw shadow for half moon effect
    drawCircle(
        color = Color(0xFF0d1854),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX + shadowOffset * 2, centerY)
    )
}

private fun DrawScope.drawGibbousMoon(
    centerX: Float,
    centerY: Float,
    radius: Float,
    progress: Float
) {
    val moonRadius = radius * 0.7f
    val shadowOffset = moonRadius * (1 - progress)

    // Draw main moon circle
    drawCircle(
        color = Color(0xFFFFF8DC),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )

    // Draw shadow for gibbous effect (decreasing)
    if (shadowOffset > 0) {
        drawCircle(
            color = Color(0xFF0d1854),
            radius = moonRadius,
            center = androidx.compose.ui.geometry.Offset(centerX + shadowOffset * 0.5f, centerY)
        )
    }
}

private fun DrawScope.drawFullMoon(
    centerX: Float,
    centerY: Float,
    radius: Float,
    progress: Float
) {
    val moonRadius = radius * 0.7f

    // Draw full moon
    drawCircle(
        color = Color(0xFFFFF8DC),
        radius = moonRadius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )

    // Add subtle craters/details
    drawCircle(
        color = Color(0xFFFFE082),
        radius = moonRadius * 0.15f,
        center = androidx.compose.ui.geometry.Offset(centerX - moonRadius * 0.3f, centerY - moonRadius * 0.2f)
    )

    drawCircle(
        color = Color(0xFFFFE082),
        radius = moonRadius * 0.1f,
        center = androidx.compose.ui.geometry.Offset(centerX + moonRadius * 0.2f, centerY + moonRadius * 0.3f)
    )
}

private fun calculateMoonPhase(hijriDay: Int): Float {
    // Calculate lunar phase based on hijri day (29-30 day lunar month)
    // Day 1-2: New moon, Day 7: Crescent, Day 15: Full moon, Day 23: Waning gibbous
    return (hijriDay % 30) / 30f
}

private fun getMoonPhaseName(phase: Float): String {
    return when {
        phase < 0.1f -> "محاق (New Moon)"
        phase < 0.3f -> "هلال (Crescent)"
        phase < 0.45f -> "تربيع أول (First Quarter)"
        phase < 0.55f -> "بدر (Full Moon)"
        phase < 0.7f -> "تربيع ثاني (Last Quarter)"
        else -> "هلال (Crescent)"
    }
}

private fun getMoonPhaseArabic(hijriDay: Int): String {
    return when (hijriDay) {
        in 1..2 -> "المحاق (القمر الجديد)"
        in 3..6 -> "الهلال"
        in 7..14 -> "التربيع الأول"
        15 -> "البدر (القمر الكامل)"
        in 16..22 -> "التربيع الثاني"
        in 23..29 -> "الهلال (الآفل)"
        30 -> "المحاق"
        else -> "غير معروف"
    }
}

/**
 * Expanded moon phase info card with detailed information
 */
@Composable
fun MoonPhaseInfoCard(
    hijriDay: Int = 15,
    hijriMonth: String = "محرم",
    hijriYear: Int = 1445,
    moonIllumination: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1a237e).copy(alpha = 0.9f),
                        Color(0xFF0d1854)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "معلومات القمر الهجري",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "اليوم الهجري",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Text(
                    text = hijriDay.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = hijriMonth,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Text(
                    text = hijriYear.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = getMoonPhaseArabic(hijriDay),
                color = Color(0xFFFFD700),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(moonIllumination)
                        .background(
                            color = Color(0xFFFFD700),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                        )
                )
            }

            Text(
                text = "إضاءة القمر: ${(moonIllumination * 100).toInt()}%",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}
