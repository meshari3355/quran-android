package com.quranapp.android.ui.screens.more

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

data class ToolItem(
    val id: Int,
    val nameAr: String,
    val subtitleAr: String,
    val icon: ImageVector,
    val color: Color,
    val route: String = ""
)

data class AudioCategory(
    val id: Int,
    val nameAr: String,
    val subtitleAr: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun MuslimToolsScreen(
    onNavigate: (String) -> Unit
) {
    val colors = AppDesign.colors

    val tools = remember {
        listOf(
            ToolItem(1, "التقويم الإسلامي", "المناسبات والأعياد", Icons.Default.DateRange, Color(0xFF4CAF50), route = "islamic_calendar"),
            ToolItem(2, "اتجاه القبلة", "بوصلة دقيقة للقبلة", Icons.Default.Navigation, Color(0xFFE8A87C), route = "qibla"),
            ToolItem(3, "إحصائيات القراءة", "تتبع تقدمك اليومي", Icons.Default.BarChart, Color(0xFF9C88D5), route = "reading_stats"),
            ToolItem(4, "حاسبة الزكاة", "احسب زكاتك بسهولة", Icons.Default.ShoppingBag, Color(0xFFFF7043), route = "zakat_calculator"),
            ToolItem(5, "كتب الحديث", "189 كتاب • 427,373 حديث", Icons.Default.MenuBook, Color(0xFF8D4925), route = "hadith_portal"),
            ToolItem(6, "الفتاوى", "فتاوى إسلامية موثوقة", Icons.Default.Help, Color(0xFF4CAF50), route = "fatwa_list"),
            ToolItem(7, "كتب التفسير", "ابن كثير • السعدي • الجلالين", Icons.Default.AutoStories, Color(0xFF7E57C2), route = "islamic_library"),
            ToolItem(8, "المساجد القريبة", "ابحث عن أقرب مسجد", Icons.Default.LocationOn, Color(0xFF26A69A), route = "nearby_mosques")
        )
    }

    val audioCategories = remember {
        listOf(
            AudioCategory(1, "التلاوات المرتّلة", "ماهر المعيقلي • مشاري العفاسي • السديس", Icons.Default.Mic, Color(0xFFD4AF37)),
            AudioCategory(2, "التلاوات المجوّدة", "عبد الباسط • الحصري بأحكام التجويد", Icons.Default.Equalizer, Color(0xFF9C27B0)),
            AudioCategory(3, "تلاوات الحرمين", "أئمة المسجد الحرام والمسجد النبوي", Icons.Default.Home, Color(0xFF26A69A)),
            AudioCategory(4, "التلاوات التعليمية", "الحصري التعليمي مع الترجمة", Icons.Default.School, Color(0xFF4CAF50))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tools Grid - 2 columns
            val rows = tools.chunked(2)
            items(rows.size) { rowIndex ->
                val rowTools = rows[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTools.forEach { tool ->
                        ToolCard(
                            tool = tool,
                            onClick = { onNavigate(tool.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty space if odd number
                    if (rowTools.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Audio Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "الصوتيات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AmiriFont,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = colors.gold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Audio Categories
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    border = BorderStroke(1.dp, colors.divider)
                ) {
                    Column {
                        audioCategories.forEachIndexed { _, category ->
                            AudioCategoryRow(
                                category = category,
                                onClick = { onNavigate("audio_library") }
                            )
                            if (audioCategories.indexOf(category) < audioCategories.size - 1) {
                                Divider(
                                    color = colors.divider,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: ToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.divider)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Icon circle
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = tool.color.copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.nameAr,
                        modifier = Modifier.size(26.dp),
                        tint = tool.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                tool.nameAr,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AmiriFont,
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                tool.subtitleAr,
                fontSize = 12.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AudioCategoryRow(
    category: AudioCategory,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chevron on the left
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = colors.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Text on the right
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                category.nameAr,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AmiriFont,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                category.subtitleAr,
                fontSize = 12.sp,
                color = colors.textSecondary,
                maxLines = 1
            )
        }

        // Icon circle on the right
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
            color = category.color.copy(alpha = 0.15f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.nameAr,
                    modifier = Modifier.size(24.dp),
                    tint = category.color
                )
            }
        }
    }
}
