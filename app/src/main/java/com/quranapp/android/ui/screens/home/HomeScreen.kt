package com.quranapp.android.ui.screens.home

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.models.Surah
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.viewmodels.Bookmark
import com.quranapp.android.viewmodels.PrayerViewModel
import com.quranapp.android.viewmodels.PrayerUiState
import com.quranapp.android.viewmodels.QuranViewModel
import java.time.LocalDateTime

// ============================================================
// HomeScreen — Main Dashboard
// Matches iOS HomeView design language exactly
// ============================================================

@Composable
fun HomeScreen(
    onNavigateToQuran: () -> Unit,
    onNavigateToPrayer: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToSettings: () -> Unit,
    quranViewModel: QuranViewModel = hiltViewModel(),
    prayerViewModel: PrayerViewModel = hiltViewModel()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val quranUiState by quranViewModel.uiState.collectAsState()
        val prayerUiState by prayerViewModel.uiState.collectAsState()
        val colors = AppDesign.colors

        // Load data on first composition
        LaunchedEffect(Unit) {
            quranViewModel.loadSurahList()
            quranViewModel.loadBookmarks()
            prayerViewModel.loadPrayerTimes()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(horizontal = AppDesign.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.lg)
        ) {
            item { Spacer(modifier = Modifier.height(AppDesign.spacing.sm)) }

            // 1. Header with greeting
            item { HomeHeader() }

            // 2. Next Prayer countdown
            item {
                NextPrayerCard(
                    prayerState = prayerUiState,
                    onClick = onNavigateToPrayer
                )
            }

            // 3. Qibla mini card
            item {
                QiblaMiniCard(onClick = onNavigateToQibla)
            }

            // 4. Last reading position
            item {
                val lastBookmark = quranUiState.bookmarks.firstOrNull()
                val surahName = lastBookmark?.let { bk ->
                    quranUiState.surahList.find { it.id == bk.surahId }?.nameAr
                        ?: Surah.getSurahById(bk.surahId)?.nameAr
                }
                LastReadingCard(
                    bookmark = lastBookmark,
                    surahName = surahName,
                    isLoading = quranUiState.isLoading,
                    onClick = onNavigateToQuran
                )
            }

            // 5. Moon phase
            item { MoonPhaseCard() }

            // 6. Reading stats
            item {
                ReadingStatsCard(surahCount = quranUiState.surahList.size)
            }

            item { Spacer(modifier = Modifier.height(AppDesign.spacing.xxl)) }
        }
    }
}

// ============================================================
// Header — Greeting + Hijri date + notification
// ============================================================
@Composable
private fun HomeHeader() {
    val colors = AppDesign.colors
    val hour = remember { LocalDateTime.now().hour }
    val greeting = when (hour) {
        in 4..11 -> "صباح الخير"
        in 12..16 -> "مساء النور"
        in 17..19 -> "مساء الخير"
        else -> "تصبح على خير"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppDesign.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Right side: greeting + date
        Column(modifier = Modifier.weight(1f).padding(end = AppDesign.spacing.md)) {
            Text(
                text = greeting,
                fontSize = 24.sp,
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
                color = colors.gold
            )
            Spacer(modifier = Modifier.height(AppDesign.spacing.xxs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "11 شوال 1447هـ",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.width(AppDesign.spacing.xs))
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = colors.gold,
                    modifier = Modifier.size(AppDesign.iconSize.tiny)
                )
            }
        }

        // Left side: bell + logo
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp).clip(CircleShape),
                color = colors.gold.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.Notifications, contentDescription = "إشعارات", tint = colors.gold, modifier = Modifier.size(AppDesign.iconSize.small))
                }
            }
            Surface(
                modifier = Modifier.size(44.dp).clip(CircleShape),
                color = colors.gold.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = colors.gold, modifier = Modifier.size(AppDesign.iconSize.medium))
                }
            }
        }
    }
}

// ============================================================
// Next Prayer Card — countdown + name + time
// ============================================================
@Composable
private fun NextPrayerCard(prayerState: PrayerUiState, onClick: () -> Unit) {
    val colors = AppDesign.colors
    val hasPrayerData = prayerState.currentPrayerTimes.isNotEmpty()

    // Section label
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = AppDesign.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text("الصلاة القادمة", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontFamily = AmiriFont)
        Spacer(modifier = Modifier.width(AppDesign.spacing.xs))
        Box(modifier = Modifier.size(8.dp).background(colors.prayerGreen, CircleShape))
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = AppDesign.cardBorder()
    ) {
        if (prayerState.isLoadingPrayers) {
            Box(modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.huge), contentAlignment = Alignment.Center) {
                QuranLoadingIndicator(color = colors.gold, modifier = Modifier.size(AppDesign.iconSize.xlarge))
            }
        } else if (hasPrayerData) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.xl),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: prayer icon + name
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(modifier = Modifier.size(AppDesign.iconSize.hero).clip(CircleShape), color = colors.goldContainer) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = colors.gold, modifier = Modifier.size(AppDesign.iconSize.large))
                        }
                    }
                    Spacer(modifier = Modifier.height(AppDesign.spacing.xs))
                    Text(prayerState.nextPrayerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont)
                    Text(prayerState.nextPrayerTime, fontSize = 11.sp, color = colors.textSecondary)
                }

                // Right: countdown
                Column(horizontalAlignment = Alignment.End) {
                    Text("بقي على الصلاة", fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                    Spacer(modifier = Modifier.height(AppDesign.spacing.xxs))
                    Text(
                        prayerState.timeUntilNextPrayer,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.gold
                    )
                    Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
                    LinearProgressIndicator(
                        progress = 0.45f,
                        modifier = Modifier.width(180.dp).height(6.dp).clip(AppDesign.radius.small),
                        color = colors.gold,
                        trackColor = colors.border
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.huge), contentAlignment = Alignment.Center) {
                Text("لم تتمكن من تحميل أوقات الصلاة", fontSize = 14.sp, color = colors.textSecondary, fontFamily = AmiriFont)
            }
        }
    }
}

// ============================================================
// Qibla Mini Card
// ============================================================
@Composable
private fun QiblaMiniCard(onClick: () -> Unit) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = AppDesign.subtleBorder()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(AppDesign.iconSize.small))
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = AppDesign.spacing.md)) {
                Text("البوصلة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont)
                Text("اتجاه القبلة — حرّك الجهاز", fontSize = 11.sp, color = colors.textSecondary, fontFamily = AmiriFont)
            }
            Surface(modifier = Modifier.size(AppDesign.iconSize.hero).clip(CircleShape), color = colors.prayerGreen.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = colors.prayerGreen, modifier = Modifier.size(AppDesign.iconSize.large))
                }
            }
        }
    }
}

// ============================================================
// Last Reading Card
// ============================================================
@Composable
private fun LastReadingCard(
    bookmark: Bookmark?,
    surahName: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = AppDesign.cardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.cardPadding),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("آخر موقف قراءة", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontFamily = AmiriFont)
                Spacer(modifier = Modifier.width(AppDesign.spacing.xs))
                Icon(Icons.Filled.MenuBook, contentDescription = null, tint = colors.gold, modifier = Modifier.size(AppDesign.iconSize.small))
            }

            Divider(color = colors.divider, thickness = 0.5.dp)

            // Content
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.huge), contentAlignment = Alignment.Center) {
                    QuranLoadingIndicator(color = colors.gold, modifier = Modifier.size(AppDesign.iconSize.xlarge))
                }
            } else if (bookmark != null && surahName != null) {
                Column(modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.xl), horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ChromeReaderMode, contentDescription = null, tint = colors.gold.copy(alpha = 0.7f), modifier = Modifier.size(AppDesign.iconSize.xlarge))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(surahName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontFamily = AmiriFont)
                            Text("آية ${bookmark.ayahNumber} • صفحة ${bookmark.page}", fontSize = 12.sp, color = colors.textSecondary)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.xl),
                    horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ChromeReaderMode, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(AppDesign.iconSize.xlarge))
                    Text("لم تبدأ القراءة بعد — افتح أي سورة لتبدأ", fontSize = 13.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                }
            }
        }
    }
}

// ============================================================
// Moon Phase Card
// ============================================================
@Composable
private fun MoonPhaseCard() {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.nightSky)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDesign.spacing.xl),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Moon icon
            Surface(modifier = Modifier.size(64.dp).clip(CircleShape), color = Color.White.copy(alpha = 0.15f)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
            // Text
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = AppDesign.spacing.lg)) {
                Text("حالة القمر", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), fontFamily = AmiriFont)
                Spacer(modifier = Modifier.height(AppDesign.spacing.xxs))
                Text("أحدب متزايد", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = AmiriFont)
                Text("Waxing Gibbous", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.lg)) {
                    Column { Text("11", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White); Text("يوم هجري", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = AmiriFont) }
                    Column { Text("92%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White); Text("الإضاءة", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = AmiriFont) }
                }
                Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
                LinearProgressIndicator(
                    progress = 0.92f,
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(AppDesign.radius.small),
                    color = colors.moonLight,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
            }
        }
    }
}

// ============================================================
// Reading Stats Card
// ============================================================
@Composable
private fun ReadingStatsCard(surahCount: Int) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppDesign.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = AppDesign.cardBorder()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = AppDesign.spacing.md),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("0", "صفحات اليوم", Icons.Default.WbSunny, colors.warning)
            Divider(color = colors.border, modifier = Modifier.width(1.dp).height(50.dp))
            StatItem("0", "هذا الأسبوع", Icons.Default.DateRange, colors.info)
            Divider(color = colors.border, modifier = Modifier.width(1.dp).height(50.dp))
            StatItem("0", "أيام متتالية", Icons.Default.Whatshot, colors.error)
            Divider(color = colors.border, modifier = Modifier.width(1.dp).height(50.dp))
            StatItem(surahCount.toString(), "إجمالي", Icons.Default.BarChart, colors.gold)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, icon: ImageVector, color: Color) {
    val colors = AppDesign.colors

    Column(
        modifier = Modifier.padding(horizontal = AppDesign.spacing.xxs, vertical = AppDesign.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(AppDesign.iconSize.small))
        Spacer(modifier = Modifier.height(AppDesign.spacing.xs))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        Spacer(modifier = Modifier.height(AppDesign.spacing.xxxs))
        Text(label, fontSize = 9.sp, color = colors.textSecondary, textAlign = TextAlign.Center, fontFamily = AmiriFont)
    }
}
