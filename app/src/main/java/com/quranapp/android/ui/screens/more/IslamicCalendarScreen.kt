package com.quranapp.android.ui.screens.more

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import java.util.Calendar
import com.quranapp.android.models.IslamicEvent as IslamicEventModel
import com.quranapp.android.models.HijriDate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

// UI-specific local data classes
data class IslamicMonth(
    val number: Int,
    val nameAr: String,
    val enName: String,
    val daysCount: Int
)

data class IslamicEventDisplay(
    val dateHijri: String,
    val nameAr: String,
    val descriptionAr: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicCalendarScreen(onBack: () -> Unit) {
    val colors = AppDesign.colors
    val islamicMonths = remember {
        listOf(
            IslamicMonth(1, "محرم", "Muharram", 30),
            IslamicMonth(2, "صفر", "Safar", 29),
            IslamicMonth(3, "ربيع الأول", "Rabi' al-awwal", 30),
            IslamicMonth(4, "ربيع الثاني", "Rabi' al-thani", 29),
            IslamicMonth(5, "جمادى الأولى", "Jumada al-awwal", 30),
            IslamicMonth(6, "جمادى الثانية", "Jumada al-thani", 29),
            IslamicMonth(7, "رجب", "Rajab", 30),
            IslamicMonth(8, "شعبان", "Sha'ban", 29),
            IslamicMonth(9, "رمضان", "Ramadan", 30),
            IslamicMonth(10, "شوال", "Shawwal", 29),
            IslamicMonth(11, "ذو القعدة", "Dhu al-Qi'dah", 30),
            IslamicMonth(12, "ذو الحجة", "Dhu al-Hijjah", 29)
        )
    }

    val islamicEvents = remember {
        listOf(
            IslamicEventDisplay("1 محرم", "رأس السنة الهجرية", "بداية السنة الهجرية الجديدة"),
            IslamicEventDisplay("9-10 محرم", "يوم عاشوراء", "صيام يوم عاشوراء من أهم الأيام في الإسلام"),
            IslamicEventDisplay("27 رجب", "الإسراء والمعراج", "ليلة إسراء النبي ومعراجه"),
            IslamicEventDisplay("1-30 رمضان", "شهر رمضان", "شهر الصيام والقيام وقراءة القرآن"),
            IslamicEventDisplay("27 رمضان", "ليلة القدر", "أفضل ليلة في السنة"),
            IslamicEventDisplay("1-3 شوال", "عيد الفطر", "عيد الفطر بعد انتهاء رمضان"),
            IslamicEventDisplay("8-12 ذو الحجة", "عيد الأضحى", "عيد الأضحى ويوم الحج الأكبر")
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "التقويم الهجري",
                    style = TextStyle(
                        fontFamily = AmiriFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.gold
            )
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            containerColor = colors.background,
            contentColor = colors.gold,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(colors.gold)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("التاريخ", fontFamily = AmiriFont) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("الأشهر", fontFamily = AmiriFont) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("الأحداث", fontFamily = AmiriFont) }
            )
        }

        // Content
        when (selectedTab) {
            0 -> DateConverterView()
            1 -> IslamicMonthsView(islamicMonths)
            2 -> IslamicEventsView(islamicEvents)
        }
    }
}

@Composable
fun DateConverterView() {
    val colors = AppDesign.colors
    var gregorianDate by remember { mutableStateOf("") }
    var hijriDate by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "تحويل التواريخ",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.card)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colors.card, colors.gold.copy(0.1f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        "من الميلادي إلى الهجري",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = gregorianDate,
                        onValueChange = { gregorianDate = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("30-03-2026", fontFamily = AmiriFont) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.background,
                            unfocusedContainerColor = colors.background,
                            focusedIndicatorColor = colors.gold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            hijriDate = convertGregorianToHijri(gregorianDate)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("تحويل", fontFamily = AmiriFont, color = colors.textPrimary)
                    }

                    if (hijriDate.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.gold.copy(0.2f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                hijriDate,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.card)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colors.card, colors.gold.copy(0.1f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Current Hijri Date Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.gold.copy(0.2f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "التاريخ الهجري الحالي",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    fontFamily = AmiriFont
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "8 رمضان 1445 هـ",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                "الموافق: 30 مارس 2024 م",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    fontFamily = AmiriFont
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun IslamicMonthsView(months: List<IslamicMonth>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(months.size, key = { months[it].number }) { index ->
            IslamicMonthCard(months[index])
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun IslamicMonthCard(month: IslamicMonth) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(colors.card, colors.gold.copy(0.1f))
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    month.nameAr,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    month.enName,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.gold.copy(0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "${month.daysCount}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.gold,
                        fontFamily = AmiriFont
                    )
                )

                Text(
                    "يوم",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }
        }
    }
}

@Composable
fun IslamicEventsView(events: List<IslamicEventDisplay>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events.size, key = { events[it].dateHijri }) { index ->
            IslamicEventCard(events[index])
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun IslamicEventCard(event: IslamicEventDisplay) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.card, colors.gold.copy(0.1f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    event.nameAr,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.StarRate,
                    contentDescription = "حدث إسلامي",
                    modifier = Modifier.size(24.dp),
                    tint = colors.gold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                event.dateHijri,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                event.descriptionAr,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont,
                    textAlign = TextAlign.Right,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

// Helper function for date conversion
fun convertGregorianToHijri(gregorianDate: String): String {
    return try {
        val parts = gregorianDate.split("-")
        if (parts.size != 3) return ""

        val day = parts[0].toIntOrNull() ?: return ""
        val month = parts[1].toIntOrNull() ?: return ""
        val year = parts[2].toIntOrNull() ?: return ""

        // Simplified conversion (real implementation would use Islamic calendar library)
        val hijriYear = (year - 622) + ((month * 12 + day) / 354)
        val hijriMonth = ((month - 1 + 1) % 12) + 1
        val hijriDay = day

        "$hijriDay/${String.format("%02d", hijriMonth)}/$hijriYear هـ"
    } catch (e: Exception) {
        ""
    }
}
