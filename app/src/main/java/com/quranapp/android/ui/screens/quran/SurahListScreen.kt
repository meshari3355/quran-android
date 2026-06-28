package com.quranapp.android.ui.screens.quran

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.models.Surah
import com.quranapp.android.viewmodels.QuranViewModel
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

@Composable
fun SurahListScreen(
    onSurahSelected: (Int, Int) -> Unit, // surahId, pageNumber
    onNavigateToReader: () -> Unit,
    viewModel: QuranViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }

    val uiState by viewModel.uiState.collectAsState()

    // Load surahs from API on first launch
    LaunchedEffect(Unit) {
        if (uiState.surahList.isEmpty()) {
            viewModel.loadSurahList()
        }
    }

    // Filter surahs based on search and filter
    val filteredSurahs by remember(searchQuery, selectedFilter, uiState.surahList) {
        derivedStateOf {
            uiState.surahList.filter { surah ->
                val matchesSearch = searchQuery.isEmpty() ||
                        surah.nameAr.contains(searchQuery) ||
                        surah.nameEn.contains(searchQuery, ignoreCase = true) ||
                        surah.id.toString() == searchQuery
                val matchesFilter = when (selectedFilter) {
                    "مكية" -> surah.type == "مكية"
                    "مدنية" -> surah.type == "مدنية"
                    else -> true
                }
                matchesSearch && matchesFilter
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.card)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "القرآن الكريم",
                fontSize = 28.sp,
                fontFamily = AmiriFont,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "ابحث عن سورة...",
                        fontSize = 14.sp,
                        color = colors.textPrimary.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.gold,
                    unfocusedBorderColor = colors.gold.copy(alpha = 0.3f),
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("الكل", "مكية", "مدنية").forEach { label ->
                    val isSelected = selectedFilter == label
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = label }
                            .animateContentSize(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) colors.goldContainer else colors.card
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) colors.gold else colors.gold.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = if (isSelected) colors.gold else colors.textPrimary.copy(alpha = 0.7f),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Loading indicator
        if (uiState.isLoading && uiState.surahList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                QuranLoadingIndicator(color = colors.gold)
            }
        }

        // Error message
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = colors.warning.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "خطأ في التحميل: $error",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = colors.warning,
                    textAlign = TextAlign.Right
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Surahs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredSurahs, key = { it.id }) { surah ->
                SurahCard(
                    surah = surah,
                    colors = colors,
                    onClick = {
                        onSurahSelected(surah.id, surah.pageNumber)
                    }
                )
            }
        }
    }
}

@Composable
private fun SurahCard(
    surah: Surah,
    colors: com.quranapp.android.ui.theme.AppColors,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number circle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = colors.goldContainer,
                        shape = CircleShape
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = surah.id.toString(),
                    fontSize = 18.sp,
                    color = colors.gold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Names and Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.nameAr,
                    fontSize = 16.sp,
                    fontFamily = AmiriFont,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = surah.nameEn,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type badge
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                        color = colors.goldContainer
                    ) {
                        Text(
                            text = surah.type,
                            fontSize = 10.sp,
                            color = colors.gold,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${surah.versesCount} آيات",
                        fontSize = 11.sp,
                        color = colors.textPrimary.copy(alpha = 0.6f)
                    )
                }
            }

            // Arrow
            Text(
                text = "›",
                fontSize = 24.sp,
                color = colors.gold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
