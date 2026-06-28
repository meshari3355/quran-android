package com.quranapp.android.ui.screens.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.material3.ExperimentalMaterial3Api
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.AmiriFont

/**
 * Reading Statistics Screen
 * Displays user's reading progress with Daily/Weekly/Monthly views
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingStatsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val colors = AppDesign.colors
    var selectedPeriod by remember { mutableStateOf(StatsPeriod.DAILY) }

    val dailyStats = listOf(
        DailyStat(day = "السبت", pages = 5, minutes = 25),
        DailyStat(day = "الأحد", pages = 4, minutes = 20),
        DailyStat(day = "الاثنين", pages = 6, minutes = 30),
        DailyStat(day = "الثلاثاء", pages = 5, minutes = 25),
        DailyStat(day = "الأربعاء", pages = 7, minutes = 35),
        DailyStat(day = "الخميس", pages = 8, minutes = 40),
        DailyStat(day = "الجمعة", pages = 6, minutes = 30)
    )

    val streakDays = 12
    val totalPages = 287
    val totalTime = 1425 // minutes
    val surahs = 15
    val khatmahProgress = 0.35f
    val estimatedDays = 65

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "إحصائيات القراءة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            // Period selector
            item {
                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = { selectedPeriod = it }
                )
            }

            // Streak counter
            item {
                StreakCard(
                    days = streakDays,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Chart based on period
            item {
                when (selectedPeriod) {
                    StatsPeriod.DAILY -> {
                        StatsChart(
                            title = "نشاط اليوم",
                            data = dailyStats.map { it.pages },
                            labels = dailyStats.map { it.day.substring(0, 1) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    StatsPeriod.WEEKLY -> {
                        StatsChart(
                            title = "إحصائيات أسبوعية",
                            data = listOf(28, 35, 42, 38, 45, 32, 40),
                            labels = listOf("ن", "ث", "ع", "خ", "ج", "س", "ح"),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    StatsPeriod.MONTHLY -> {
                        StatsChart(
                            title = "إحصائيات شهرية",
                            data = listOf(245, 260, 280, 310, 295),
                            labels = listOf("الأسبوع 1", "الأسبوع 2", "الأسبوع 3", "الأسبوع 4", "الأسبوع 5"),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Total statistics
            item {
                TotalStatsSection(
                    totalPages = totalPages,
                    totalTime = totalTime,
                    surahs = surahs,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Khatmah progress
            item {
                KhatmahProgressCard(
                    progress = khatmahProgress,
                    estimatedDays = estimatedDays,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Goals section
            item {
                GoalsSection(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodChange: (StatsPeriod) -> Unit
) {
    val colors = AppDesign.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatsPeriod.values().forEach { period ->
            Button(
                onClick = { onPeriodChange(period) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPeriod == period) colors.gold
                    else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = period.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedPeriod == period) Color.White else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun StreakCard(
    days: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.warning.copy(alpha = 0.15f),
                            colors.warning.copy(alpha = 0.5f).copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Streak",
                tint = colors.warning,
                modifier = Modifier.size(40.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "متتابعة القراءة",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = days.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.warning
                    )

                    Text(
                        text = "يوم",
                        fontSize = 14.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(
                    color = colors.warning.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ).padding(8.dp)
            ) {
                Text(
                    text = "رائع!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.warning
                )
                Text(
                    text = "استمر",
                    fontSize = 9.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun StatsChart(
    title: String,
    data: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val maxValue = data.maxOrNull() ?: 10

                data.forEachIndexed { index, value ->
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
                                .fillMaxHeight((value.toFloat() / maxValue).coerceIn(0.1f, 1f))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            colors.gold,
                                            colors.gold.copy(alpha = 0.6f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )

                        Text(
                            text = labels[index],
                            fontSize = 8.sp,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalStatsSection(
    totalPages: Int,
    totalTime: Int,
    surahs: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "الإحصائيات الإجمالية",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    icon = Icons.Filled.MenuBook,
                    label = "إجمالي الصفحات",
                    value = totalPages.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    icon = Icons.Default.AccessTime,
                    label = "إجمالي الوقت",
                    value = "${totalTime / 60}h",
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    icon = Icons.Default.ListAlt,
                    label = "السور المكتملة",
                    value = surahs.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Column(
        modifier = modifier
            .background(
                color = colors.gold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colors.gold,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Text(
            text = label,
            fontSize = 9.sp,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun KhatmahProgressCard(
    progress: Float,
    estimatedDays: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تقدم الختمة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.gold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = colors.gold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colors.gold,
                                    colors.gold.copy(alpha = 0.6f)
                                )
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colors.gold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "وقت الانتهاء المتوقع",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )

                Text(
                    text = "خلال $estimatedDays يوم",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.gold
                )
            }
        }
    }
}

@Composable
private fun GoalsSection(
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "أهدافك",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            GoalItem(
                icon = Icons.Default.Star,
                title = "قراءة 5 صفحات يومياً",
                status = "مكتمل اليوم"
            )

            GoalItem(
                icon = Icons.Default.Grade,
                title = "الحفاظ على المتتابعة",
                status = "12 يوم متتالي"
            )

            GoalItem(
                icon = Icons.Default.Favorite,
                title = "إكمال الختمة",
                status = "35% مكتمل"
            )
        }
    }
}

@Composable
private fun GoalItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    status: String
) {
    val colors = AppDesign.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.gold.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colors.gold,
            modifier = Modifier.size(20.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )

            Text(
                text = status,
                fontSize = 9.sp,
                color = colors.textSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Completed",
            tint = colors.success,
            modifier = Modifier.size(16.dp)
        )
    }
}

enum class StatsPeriod(val displayName: String) {
    DAILY("يومي"),
    WEEKLY("أسبوعي"),
    MONTHLY("شهري")
}

data class DailyStat(
    val day: String,
    val pages: Int,
    val minutes: Int
)
