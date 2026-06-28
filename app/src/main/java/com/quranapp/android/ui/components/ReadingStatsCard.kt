package com.quranapp.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reading statistics card displaying user's Quran reading progress
 */
@Composable
fun ReadingStatsCard(
    pagesReadToday: Int = 5,
    currentStreak: Int = 12,
    totalPagesRead: Int = 287,
    khatmahProgress: Float = 0.35f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFAF0E6),
                        Color(0xFFF5E6D3)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "إحصائيات قراءتك",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )

        // Statistics grid
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatBox(
                icon = Icons.Default.Star,
                label = "اليوم",
                value = pagesReadToday.toString(),
                unit = "صفحة",
                modifier = Modifier.weight(1f)
            )

            StatBox(
                icon = Icons.Default.Favorite,
                label = "متتابعة",
                value = currentStreak.toString(),
                unit = "يوم",
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFFFF6F00)
            )

            StatBox(
                icon = Icons.Default.KeyboardArrowUp,
                label = "المجموع",
                value = totalPagesRead.toString(),
                unit = "صفحة",
                modifier = Modifier.weight(1f)
            )
        }

        // Mini bar chart
        ReadingChartPreview(modifier = Modifier.fillMaxWidth())

        // Khatmah progress
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFD4A574).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الختمة الحالية",
                    color = Color(0xFF3E2723),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(khatmahProgress * 100).toInt()}%",
                    color = Color(0xFFD4A574),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = Color(0xFFD4A574).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(khatmahProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFD4A574),
                                    Color(0xFFC19A6B)
                                )
                            ),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun StatBox(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFFD4A574)
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            fontSize = 9.sp,
            color = Color(0xFF795548)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = unit,
                fontSize = 8.sp,
                color = Color(0xFF795548)
            )
        }
    }
}

/**
 * Mini bar chart showing last 7 days reading activity
 */
@Composable
private fun ReadingChartPreview(
    modifier: Modifier = Modifier,
    daysData: List<Int> = listOf(4, 6, 5, 8, 3, 7, 5)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "آخر 7 أيام",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3E2723)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val maxPages = daysData.maxOrNull() ?: 10

            daysData.forEachIndexed { index, pages ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((pages.toFloat() / maxPages).coerceIn(0f, 1f))
                            .background(
                                color = Color(0xFFD4A574),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )

                    Text(
                        text = getDayName(index),
                        fontSize = 8.sp,
                        color = Color(0xFF795548),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun getDayName(index: Int): String {
    val days = listOf("ح", "ن", "ث", "ع", "خ", "ج", "س")
    return if (index < days.size) days[index] else ""
}

/**
 * Expanded reading statistics section
 */
@Composable
fun ExpandedReadingStats(
    weeklyPages: List<Int> = listOf(4, 6, 5, 8, 3, 7, 5),
    monthlyPages: List<Int> = listOf(28, 35, 42, 38, 45, 32),
    pagesPerDay: Float = 5.5f,
    estimatedKhatmahDays: Int = 65,
    totalSurahs: Int = 15,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFAF0E6),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "إحصائيات مفصلة",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )

        // Average pages per day
        StatDetailCard(
            label = "متوسط الصفحات يومياً",
            value = "%.1f".format(pagesPerDay),
            unit = "صفحة",
            icon = Icons.Default.KeyboardArrowUp
        )

        // Surahs completed
        StatDetailCard(
            label = "السور المكتملة",
            value = totalSurahs.toString(),
            unit = "سورة",
            icon = Icons.Default.Star
        )

        // Estimated completion
        StatDetailCard(
            label = "وقت الختم المتوقع",
            value = estimatedKhatmahDays.toString(),
            unit = "يوم",
            icon = Icons.Default.KeyboardArrowUp
        )

        // Weekly chart
        ReadingChart(
            title = "الإحصائيات الأسبوعية",
            data = weeklyPages,
            modifier = Modifier.fillMaxWidth()
        )

        // Monthly chart
        ReadingChart(
            title = "الإحصائيات الشهرية",
            data = monthlyPages,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatDetailCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFFD4A574),
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF795548)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = unit,
                    fontSize = 11.sp,
                    color = Color(0xFF795548)
                )
            }
        }
    }
}

/**
 * Bar chart for reading statistics
 */
@Composable
private fun ReadingChart(
    title: String,
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3E2723)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val maxValue = data.maxOrNull() ?: 10

            data.forEachIndexed { _, value ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((value.toFloat() / maxValue).coerceIn(0f, 1f))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFD4A574),
                                        Color(0xFFC19A6B)
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )

                    Text(
                        text = value.toString(),
                        fontSize = 9.sp,
                        color = Color(0xFF795548),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
