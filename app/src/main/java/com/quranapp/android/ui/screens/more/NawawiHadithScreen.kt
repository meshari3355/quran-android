package com.quranapp.android.ui.screens.more

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.HadithViewModel
import com.quranapp.android.viewmodels.HadithEntry

/**
 * Nawawi Hadiths Screen
 * Displays 40+ Hadith Nawawi collection with real API data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NawawiHadithScreen(
    onNavigateBack: () -> Unit = {}
) {
    val colors = AppDesign.colors
    val viewModel: HadithViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedHadith by remember { mutableStateOf<HadithEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var fontSize by remember { mutableStateOf(14.sp) }

    // Load Nawawi hadiths (collection ID 76 based on available data)
    // Nawawi collection ID is typically 76 or can be identified from collections list
    LaunchedEffect(Unit) {
        viewModel.loadBooks("4") // Nawawi category ID from HadithBookSummary.getAllCategories()
    }

    val filteredHadiths = if (searchQuery.isNotEmpty()) {
        uiState.searchResults
    } else {
        uiState.searchResults.ifEmpty { emptyList() }
    }

    if (selectedHadith != null) {
        HadithDetailScreen(
            hadith = selectedHadith!!,
            fontSize = fontSize,
            onFontSizeChange = { fontSize = it },
            onBack = { selectedHadith = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "أربعين النووي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = colors.gold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.gold
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .padding(paddingValues)
            ) {
                // Search bar
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isNotEmpty()) {
                            viewModel.searchHadiths(it)
                        } else {
                            viewModel.clearSearch()
                        }
                    },
                    placeholder = { Text("ابحث عن حديث...", fontFamily = AmiriFont) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.card,
                        unfocusedContainerColor = colors.card,
                        focusedIndicatorColor = colors.gold
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") }
                )

                // Loading state
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        QuranLoadingIndicator(color = colors.gold)
                    }
                }
                // Error state
                else if (uiState.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "حدث خطأ",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                uiState.error ?: "خطأ غير معروف",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = colors.textSecondary,
                                    fontFamily = AmiriFont
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.clearError() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.gold
                                )
                            ) {
                                Text("حاول مجددا", fontFamily = AmiriFont)
                            }
                        }
                    }
                }
                // Hadiths list
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        if (filteredHadiths.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "لا توجد أحاديث",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            color = colors.textSecondary,
                                            fontFamily = AmiriFont
                                        )
                                    )
                                }
                            }
                        } else {
                            items(filteredHadiths) { hadith ->
                                HadithNawawiCard(
                                    hadith = hadith,
                                    onClick = { selectedHadith = hadith }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HadithNawawiCard(
    hadith: HadithEntry,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colors.gold,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = hadith.number.toString(),
                        color = colors.card,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AmiriFont
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = hadith.text.take(80),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )

                    Text(
                        text = hadith.text.take(150),
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = AmiriFont,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Right
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colors.goldContainer,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hadith.narrator,
                    fontSize = 10.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AmiriFont
                )

                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "عرض التفاصيل",
                    tint = colors.gold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HadithDetailScreen(
    hadith: HadithEntry,
    fontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "الحديث ${hadith.number}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = colors.gold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = colors.gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.gold
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
            // Narrator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colors.card,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "الراوي",
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "الراوي",
                            fontSize = 10.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        )
                        Text(
                            text = hadith.narrator,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont
                        )
                    }
                }
            }

            // Grade
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colors.card,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "التقدير",
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "التقدير",
                            fontSize = 10.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        )
                        Text(
                            text = hadith.gradeArabic,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.gold,
                            fontFamily = AmiriFont
                        )
                    }
                }
            }

            // Font size control
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colors.card,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (fontSize.value > 12) onFontSizeChange((fontSize.value - 2).sp) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "تصغير الخط",
                            tint = colors.gold
                        )
                    }

                    Text(
                        text = "${fontSize.value.toInt()}",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = AmiriFont
                    )

                    IconButton(
                        onClick = { if (fontSize.value < 24) onFontSizeChange((fontSize.value + 2).sp) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تكبير الخط",
                            tint = colors.gold
                        )
                    }
                }
            }

            // Full hadith text
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.card
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    SelectionContainer {
                        Text(
                            text = hadith.text,
                            modifier = Modifier.padding(16.dp),
                            fontSize = fontSize,
                            color = colors.textPrimary,
                            lineHeight = (fontSize.value * 1.8).sp,
                            fontFamily = AmiriFont,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Right
                        )
                    }
                }
            }

            // English translation if available
            if (hadith.textEn.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.card.copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "الترجمة الإنجليزية",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )

                            Text(
                                text = hadith.textEn,
                                fontSize = fontSize,
                                color = colors.textSecondary,
                                lineHeight = (fontSize.value * 1.6).sp
                            )
                        }
                    }
                }
            }

            // Share button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = colors.card,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            "مشاركة",
                            color = colors.card,
                            fontSize = 12.sp,
                            fontFamily = AmiriFont
                        )
                    }

                    Button(
                        onClick = { /* Copy */ },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.goldContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            tint = colors.gold,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            "نسخ",
                            color = colors.gold,
                            fontSize = 12.sp,
                            fontFamily = AmiriFont
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
