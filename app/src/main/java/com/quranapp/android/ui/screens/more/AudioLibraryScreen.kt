package com.quranapp.android.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.models.Reciter
import com.quranapp.android.models.Surah

data class ReciterDisplay(
    val id: Int,
    val nameAr: String,
    val country: String,
    val narrationStyle: String
)

data class SurahDisplay(
    val number: Int,
    val nameAr: String,
    val enName: String,
    val versesCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLibraryScreen(onBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val colors = AppDesign.colors
        // Load real reciters from local data
        val realReciters = remember {
            Reciter.getAllReciters().mapIndexed { index, reciter ->
                ReciterDisplay(
                    id = reciter.id,
                    nameAr = reciter.nameAr,
                    country = when {
                        reciter.nameAr.contains("السديس") -> "السعودية"
                        reciter.nameAr.contains("الشريم") -> "السعودية"
                        reciter.nameAr.contains("الجليل") -> "السعودية"
                        reciter.nameAr.contains("العفاسي") -> "الكويت"
                        reciter.nameAr.contains("الدسوقي") -> "مصر"
                        reciter.nameAr.contains("الجزائري") -> "الجزائر"
                        reciter.nameAr.contains("الدواني") -> "السعودية"
                        reciter.nameAr.contains("البنا") -> "مصر"
                        reciter.nameAr.contains("الرفاعي") -> "مصر"
                        else -> "متعدد"
                    },
                    narrationStyle = if (index % 2 == 0) "مجود" else "ترتيل"
                )
            }
        }

        val surahs = remember {
            Surah.getAllSurahs().take(20).mapIndexed { index, surah ->
                SurahDisplay(
                    number = surah.id,
                    nameAr = surah.nameAr,
                    enName = surah.nameEn,
                    versesCount = surah.versesCount
                )
            }
        }

        val selectedReciter = remember { mutableStateOf<ReciterDisplay?>(null) }

        if (selectedReciter.value != null) {
            ReciterSurahListScreen(
                reciter = selectedReciter.value!!,
                surahs = surahs,
                onBack = { selectedReciter.value = null }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ) {
                // Header
                TopAppBar(
                    title = {
                        Text(
                            "المكتبة الصوتية",
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

                // Subtitle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.gold.copy(0.1f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "اختر مقرئ القرآن المفضل لديك",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        )
                    )
                }

                // Reciters Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(realReciters, key = { it.id }) { reciter ->
                        ReciterCard(
                            reciter = reciter,
                            onClick = { selectedReciter.value = reciter }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReciterCard(
    reciter: ReciterDisplay,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.card, colors.gold.copy(0.15f))
                    )
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = colors.gold.copy(0.25f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "تشغيل",
                        modifier = Modifier.size(36.dp),
                        tint = colors.gold
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    reciter.nameAr,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    reciter.country,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.gold.copy(0.2f))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    reciter.narrationStyle,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.gold,
                        fontFamily = AmiriFont
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterSurahListScreen(
    reciter: ReciterDisplay,
    surahs: List<SurahDisplay>,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    val selectedSurah = remember { mutableStateOf<SurahDisplay?>(null) }
    val isPlaying = remember { mutableStateOf(false) }
    val playingIndex = remember { mutableStateOf(-1) }

    if (selectedSurah.value != null) {
        SurahPlayerScreen(
            reciter = reciter,
            surah = selectedSurah.value!!,
            onBack = { selectedSurah.value = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            TopAppBar(
                title = {
                    Text(
                        reciter.nameAr,
                        style = TextStyle(
                            fontFamily = AmiriFont,
                            fontSize = 16.sp,
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(surahs.size, key = { surahs[it].number }) { index ->
                    val surah = surahs[index]
                    SurahListItem(
                        surah = surah,
                        isPlaying = playingIndex.value == index && isPlaying.value,
                        onClick = {
                            selectedSurah.value = surah
                            playingIndex.value = index
                            isPlaying.value = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun SurahListItem(
    surah: SurahDisplay,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) colors.gold.copy(0.15f) else colors.card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    color = colors.gold.copy(0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "تشغيل",
                            modifier = Modifier.size(20.dp),
                            tint = colors.gold
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        surah.nameAr,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont,
                            textAlign = TextAlign.Right
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        "${surah.versesCount} آية",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "تحميل",
                        modifier = Modifier.size(18.dp),
                        tint = colors.textSecondary
                    )
                }

                Text(
                    "${surah.number}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.gold,
                        fontFamily = AmiriFont
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahPlayerScreen(
    reciter: ReciterDisplay,
    surah: SurahDisplay,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    val isPlaying = remember { mutableStateOf(true) }
    val playbackProgress = remember { mutableFloatStateOf(0.35f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = { Text(surah.nameAr, fontFamily = AmiriFont) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.gold
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        colors.gold.copy(0.2f),
                                        colors.gold.copy(0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "تشغيل",
                                modifier = Modifier.size(64.dp),
                                tint = colors.gold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                surah.nameAr,
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                reciter.nameAr,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = colors.textSecondary,
                                    fontFamily = AmiriFont,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        "التقدم",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = playbackProgress.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = colors.gold,
                        trackColor = colors.divider
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "4:35",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        )

                        Text(
                            "12:20",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "التالي",
                            modifier = Modifier.size(24.dp),
                            tint = colors.gold
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        color = colors.gold
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { isPlaying.value = !isPlaying.value }
                        ) {
                            Icon(
                                if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "تشغيل/إيقاف",
                                modifier = Modifier.size(32.dp),
                                tint = colors.textPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "السابق",
                            modifier = Modifier.size(24.dp),
                            tint = colors.gold
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "تكرار",
                            modifier = Modifier.size(18.dp),
                            tint = colors.gold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("كرر", fontFamily = AmiriFont, color = colors.gold)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "تحميل",
                            modifier = Modifier.size(18.dp),
                            tint = colors.gold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تحميل", fontFamily = AmiriFont, color = colors.gold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
