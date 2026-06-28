package com.quranapp.android.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.AmiriFont

/**
 * Islamic Library Screen
 * Access to Tafsir books (Ibn Kathir, As-Sadi, Al-Jalalayn)
 * Browse and read tafsir by surah
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicLibraryScreen(
    onNavigateBack: () -> Unit = {}
) {
    val colors = AppDesign.colors
    var selectedBook by remember { mutableStateOf<TafsirBook?>(null) }

    if (selectedBook != null) {
        TafsirDetailScreen(
            book = selectedBook!!,
            onBack = { selectedBook = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "المكتبة الإسلامية",
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
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tafsir books
                item {
                    Text(
                        "كتب التفسير",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                items(tafsirBooks) { book ->
                    TafsirBookCard(
                        book = book,
                        onClick = { selectedBook = book }
                    )
                }
            }
        }
    }
}

@Composable
private fun TafsirBookCard(
    book: TafsirBook,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = book.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = book.name,
                    tint = book.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = book.author,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Badge(
                        text = "${book.surahCount} سورة",
                        color = book.color
                    )

                    if (book.isDownloaded) {
                        Badge(
                            text = "محفوظ",
                            color = colors.success
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View",
                tint = colors.gold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun Badge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TafsirDetailScreen(
    book: TafsirBook,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    var selectedSurah by remember { mutableStateOf<SurahForTafsir?>(null) }

    if (selectedSurah != null) {
        TafsirReaderScreen(
            book = book,
            surah = selectedSurah!!,
            onBack = { selectedSurah = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            book.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.gold
                            )
                        }
                    },
                    actions = {
                        if (!book.isDownloaded) {
                            IconButton(onClick = { /* Download */ }) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = colors.gold
                                )
                            }
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
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(surahs.chunked(2)) { surahRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        surahRow.forEach { surah ->
                            SurahGridItem(
                                surah = surah,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedSurah = surah }
                            )
                        }

                        if (surahRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahGridItem(
    surah: SurahForTafsir,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = colors.gold,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.number.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = surah.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${surah.ayahs} آية",
                fontSize = 9.sp,
                color = colors.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TafsirReaderScreen(
    book: TafsirBook,
    surah: SurahForTafsir,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            surah.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            book.name,
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "تفسير سورة ${surah.name}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(10) { index ->
                TafsirAyahCard(
                    ayahNumber = index + 1,
                    tafsirText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ هذا تفسير الآية رقم ${index + 1} من سورة ${surah.name} في كتاب ${book.name}. يشرح هذا التفسير معاني الألفاظ ويوضح المقاصد الشرعية."
                )
            }
        }
    }
}

@Composable
private fun TafsirAyahCard(
    ayahNumber: Int,
    tafsirText: String
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colors.gold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "الآية $ayahNumber",
                    fontSize = 10.sp,
                    color = colors.gold,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = tafsirText,
                fontSize = 12.sp,
                color = colors.textPrimary,
                lineHeight = 18.sp
            )
        }
    }
}

// Data classes
data class TafsirBook(
    val name: String,
    val author: String,
    val color: Color,
    val surahCount: Int = 114,
    val isDownloaded: Boolean = false
)

// Local UI wrapper for tafsir selection - different from model.Surah
data class SurahForTafsir(
    val number: Int,
    val name: String,
    val ayahs: Int
)

// Sample data
private val tafsirBooks = listOf(
    TafsirBook(
        name = "تفسير ابن كثير",
        author = "إسماعيل بن عمر بن كثير",
        color = Color(0xFF1a237e),
        isDownloaded = true
    ),
    TafsirBook(
        name = "تفسير السعدي",
        author = "عبدالرحمن ناصر السعدي",
        color = Color(0xFFff6f00)
    ),
    TafsirBook(
        name = "تفسير الجلالين",
        author = "جلال الدين المحلي والسيوطي",
        color = Color(0xFF00796b)
    )
)

private val surahs = (1..114).map { number ->
    SurahForTafsir(
        number = number,
        name = when (number) {
            1 -> "الفاتحة"
            2 -> "البقرة"
            3 -> "آل عمران"
            4 -> "النساء"
            5 -> "المائدة"
            else -> "السورة $number"
        },
        ayahs = when (number) {
            1 -> 7
            2 -> 286
            3 -> 200
            4 -> 176
            5 -> 120
            else -> (100..300).random()
        }
    )
}
