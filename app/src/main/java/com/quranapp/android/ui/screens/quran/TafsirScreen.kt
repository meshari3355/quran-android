package com.quranapp.android.ui.screens.quran

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.data.repository.TafsirRepository
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.QuranViewModel

data class TafsirSource(
    val name: String,
    val author: String,
    val content: String
)

@Composable
fun TafsirScreen(
    verseNumber: Int = 2,
    surahName: String = "البقرة",
    surahId: Int = 2,
    onNavigateBack: () -> Unit
) {
    val colors = AppDesign.colors
    val viewModel = hiltViewModel<QuranViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTafsir by remember { mutableStateOf(TafsirRepository.TAFSIR_IBN_KATHIR) }
    var fontSize by remember { mutableStateOf(16.sp) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(surahId, verseNumber, selectedTafsir) {
        viewModel.loadTafsir(surahId, verseNumber)
    }

    val amiriFont = AmiriFont
    val systemFont = FontFamily.SansSerif

    val tafsirSources = getTafsirSources(selectedTafsir, uiState.tafsirData)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top Bar
        TafsirTopBar(
            surahName = surahName,
            verseNumber = verseNumber,
            colors = colors,
            systemFont = systemFont,
            amiriFont = amiriFont,
            onNavigateBack = onNavigateBack
        )

        // Source Selector Tabs
        TafsirSourceSelector(
            selectedTafsir = selectedTafsir,
            onTafsirSelected = {
                selectedTafsir = it
                hasError = false
            },
            colors = colors
        )

        // Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.error != null && uiState.tafsirData == null -> {
                    ErrorState(
                        message = uiState.error ?: "خطأ في تحميل التفسير",
                        colors = colors,
                        systemFont = systemFont,
                        onRetry = {
                            hasError = false
                            viewModel.loadTafsir(surahId, verseNumber)
                        }
                    )
                }
                uiState.tafsirData?.tafsirText?.isNotEmpty() == true -> {
                    val tafsirData = uiState.tafsirData!!
                    TafsirContent(
                        tafsirText = tafsirData.tafsirText,
                        author = tafsirData.author,
                        fontSize = fontSize,
                        colors = colors,
                        systemFont = systemFont,
                        amiriFont = amiriFont
                    )
                }
                else -> {
                    LoadingShimmer(colors)
                }
            }
        }

        // Bottom Actions Bar
        TafsirBottomBar(
            fontSize = fontSize,
            onFontSizeChange = { fontSize = it },
            onShare = { },
            colors = colors,
            systemFont = systemFont
        )
    }
}

@Composable
private fun TafsirTopBar(
    surahName: String,
    verseNumber: Int,
    colors: com.quranapp.android.ui.theme.AppColors,
    systemFont: FontFamily,
    amiriFont: FontFamily,
    onNavigateBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = colors.gold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تفسير",
                        fontSize = 20.sp,
                        fontFamily = amiriFont,
                        color = colors.textPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "$surahName:$verseNumber",
                        fontSize = 14.sp,
                        fontFamily = systemFont,
                        color = colors.textPrimary.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "خيارات",
                        tint = colors.gold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TafsirSourceSelector(
    selectedTafsir: Int,
    onTafsirSelected: (Int) -> Unit,
    colors: com.quranapp.android.ui.theme.AppColors
) {
    val sources = listOf(
        "تفسير ابن كثير" to TafsirRepository.TAFSIR_IBN_KATHIR,
        "تفسير السعدي" to TafsirRepository.TAFSIR_SAADI,
        "تفسير الجلالين" to TafsirRepository.TAFSIR_JALALYN
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            sources.forEach { (name, id) ->
                Button(
                    onClick = { onTafsirSelected(id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTafsir == id) {
                            colors.gold
                        } else {
                            colors.goldContainer
                        }
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 11.sp,
                        fontFamily = AmiriFont,
                        color = if (selectedTafsir == id) {
                            colors.textPrimary
                        } else {
                            colors.textPrimary.copy(alpha = 0.7f)
                        },
                        fontWeight = if (selectedTafsir == id) {
                            androidx.compose.ui.text.font.FontWeight.Bold
                        } else {
                            androidx.compose.ui.text.font.FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TafsirContent(
    tafsirText: String,
    author: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    colors: com.quranapp.android.ui.theme.AppColors,
    systemFont: FontFamily,
    amiriFont: FontFamily
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.goldContainer
                ),
                border = BorderStroke(
                    1.dp,
                    colors.gold.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = author.ifEmpty { "تفسير ابن كثير" },
                        fontSize = 14.sp,
                        fontFamily = amiriFont,
                        color = colors.gold,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "من تفسير الحافظ ابن كثير",
                        fontSize = 12.sp,
                        fontFamily = systemFont,
                        color = colors.textPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Text(
                text = tafsirText,
                fontSize = fontSize,
                fontFamily = amiriFont,
                color = colors.textPrimary,
                textAlign = TextAlign.Right,
                lineHeight = (fontSize.value * 1.8).sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LoadingShimmer(
    colors: com.quranapp.android.ui.theme.AppColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.gold.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    colors: com.quranapp.android.ui.theme.AppColors,
    systemFont: FontFamily,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = "خطأ",
            tint = colors.gold,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "حدث خطأ في تحميل التفسير",
            fontSize = 16.sp,
            fontFamily = systemFont,
            color = colors.textPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            fontFamily = systemFont,
            color = colors.textPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = colors.gold),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "إعادة محاولة",
                tint = colors.textPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إعادة محاولة",
                fontSize = 14.sp,
                fontFamily = systemFont,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun TafsirBottomBar(
    fontSize: androidx.compose.ui.unit.TextUnit,
    onFontSizeChange: (androidx.compose.ui.unit.TextUnit) -> Unit,
    onShare: () -> Unit,
    colors: com.quranapp.android.ui.theme.AppColors,
    systemFont: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
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
            // Font Size Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { onFontSizeChange((fontSize.value - 2).sp) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        color = colors.gold
                    )
                }

                Text(
                    text = fontSize.value.toInt().toString(),
                    fontSize = 12.sp,
                    fontFamily = systemFont,
                    color = colors.textPrimary,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { onFontSizeChange((fontSize.value + 2).sp) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        color = colors.gold
                    )
                }
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp),
                color = colors.gold.copy(alpha = 0.2f)
            )

            // Share Button
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "مشاركة",
                    tint = colors.gold,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Bookmark Button
            IconButton(
                onClick = { },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "حفظ",
                    tint = colors.gold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun getTafsirSources(
    selectedTafsir: Int,
    tafsirData: com.quranapp.android.viewmodels.TafsirData?
): List<TafsirSource> {
    // If we have real API data, use it; otherwise use fallback sources
    return if (tafsirData != null) {
        listOf(
            TafsirSource(
                name = getTafsirNameById(selectedTafsir),
                author = tafsirData.author,
                content = tafsirData.tafsirText
            )
        )
    } else {
        // Fallback mock data
        listOf(
            TafsirSource(
                name = "ابن كثير",
                author = "الإمام إسماعيل بن كثير",
                content = "جاري تحميل التفسير من الخادم..."
            )
        )
    }
}

private fun getTafsirNameById(id: Int): String {
    return when (id) {
        TafsirRepository.TAFSIR_IBN_KATHIR -> "تفسير ابن كثير"
        TafsirRepository.TAFSIR_SAADI -> "تفسير السعدي"
        TafsirRepository.TAFSIR_JALALYN -> "تفسير الجلالين"
        else -> "تفسير"
    }
}
