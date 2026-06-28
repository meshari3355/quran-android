package com.quranapp.android.ui.screens.more

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.models.ServerFatwa
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.FatwaViewModel

data class FatwaCategoryItem(
    val id: String,
    val name: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatwaScreen(onBack: () -> Unit) {
    val viewModel = hiltViewModel<FatwaViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val colors = AppDesign.colors

    val categories = listOf(
        FatwaCategoryItem("", "الكل", Color.Blue),
        FatwaCategoryItem("عبادة", "عبادة", Color(0xFF4C63D2)),
        FatwaCategoryItem("عقيدة", "عقيدة", Color(0xFFFF9500)),
        FatwaCategoryItem("فقه", "فقه", Color(0xFF10B981)),
        FatwaCategoryItem("معاملات", "معاملات", Color(0xFF06B6D4)),
        FatwaCategoryItem("أسرة", "أسرة", Color(0xFFEC4899)),
        FatwaCategoryItem("أخلاق", "أخلاق", Color(0xFFDC2626)),
        FatwaCategoryItem("عام", "عام", Color(0xFF6B7280))
    )

    if (uiState.selectedFatwa != null) {
        FatwaDetailScreen(
            fatwa = uiState.selectedFatwa!!,
            onBack = { viewModel.deselectFatwa() }
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
                        "فتاوى ابن باز",
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

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar
                item {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isEmpty()) {
                                viewModel.clearSearch()
                            } else {
                                viewModel.searchFatwas(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("ابحث في الفتاوى...", fontFamily = AmiriFont) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.card,
                            unfocusedContainerColor = colors.card,
                            focusedIndicatorColor = colors.gold
                        )
                    )
                }

                // Error State
                if (uiState.error != null) {
                    item {
                        ErrorStateCard(
                            message = uiState.error ?: "حدث خطأ",
                            onRetry = { viewModel.retry() }
                        )
                    }
                }

                // Header banner with count
                item {
                    val displayCount = if (uiState.searchQuery.isNotEmpty()) {
                        uiState.searchResults.size
                    } else {
                        uiState.fatwas.size
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = colors.card)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.goldContainer)
                                    .size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = colors.gold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (uiState.searchQuery.isNotEmpty()) "نتائج البحث" else "فتاوى الإمام ابن باز رحمه الله",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontFamily = AmiriFont
                                    )
                                )
                                Text(
                                    "$displayCount فتوى",
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

                // Loading state
                if (uiState.isLoading && uiState.fatwas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            QuranLoadingIndicator(color = colors.gold)
                        }
                    }
                }

                // Fatwas List
                val displayFatwas = if (uiState.searchQuery.isNotEmpty()) {
                    uiState.searchResults
                } else {
                    uiState.fatwas
                }

                if (displayFatwas.isEmpty() && !uiState.isLoading && uiState.error == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "لا توجد فتاوى",
                                fontFamily = AmiriFont,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                items(displayFatwas, key = { it.id }) { fatwa ->
                    FatwaPreviewCard(
                        fatwa = fatwa,
                        onClick = { viewModel.selectFatwa(fatwa) }
                    )
                }

                // Load more button
                if (uiState.hasMore && uiState.searchQuery.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMore() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.gold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "تحميل المزيد",
                                    fontFamily = AmiriFont,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun FatwaPreviewCard(
    fatwa: ServerFatwa,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category icon badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(getCategoryColor(fatwa.category ?: "عام").copy(0.12f))
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getCategoryIcon(fatwa.category ?: "عام"),
                    contentDescription = fatwa.category,
                    modifier = Modifier.size(15.dp),
                    tint = getCategoryColor(fatwa.category ?: "عام")
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 0.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    fatwa.question ?: fatwa.title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(5.dp))

                val preview = fatwa.preview ?: fatwa.answer
                if (!preview.isNullOrEmpty()) {
                    Text(
                        preview.take(80) + "...",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont,
                            textAlign = TextAlign.Right
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    if (!fatwa.category.isNullOrEmpty()) {
                        Text(
                            fatwa.category,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = getCategoryColor(fatwa.category)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(getCategoryColor(fatwa.category).copy(0.1f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "انتقل",
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.Top),
                tint = colors.textSecondary
            )
        }
    }
}

@Composable
private fun ErrorStateCard(
    message: String,
    onRetry: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.error.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.WarningAmber,
                contentDescription = "خطأ",
                tint = colors.error,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "خطأ في تحميل الفتاوى",
                    fontFamily = AmiriFont,
                    fontWeight = FontWeight.Bold,
                    color = colors.error,
                    fontSize = 13.sp
                )
                Text(
                    message,
                    fontFamily = AmiriFont,
                    color = colors.error.copy(0.7f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Button(
                onClick = onRetry,
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.error)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "إعادة محاولة",
                    tint = colors.card,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "عبادة" -> Color(0xFF4C63D2)
        "عقيدة" -> Color(0xFFFF9500)
        "فقه" -> Color(0xFF10B981)
        "معاملات" -> Color(0xFF06B6D4)
        "أسرة" -> Color(0xFFEC4899)
        "أخلاق" -> Color(0xFFDC2626)
        else -> Color(0xFF6B7280)
    }
}

private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        "عبادة" -> Icons.Default.LightMode
        "عقيدة" -> Icons.Default.Star
        "فقه" -> Icons.Default.BookmarkBorder
        "معاملات" -> Icons.Default.Handyman
        "أسرة" -> Icons.Default.People
        "أخلاق" -> Icons.Default.Star
        else -> Icons.Default.FiberManualRecord
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatwaDetailScreen(
    fatwa: ServerFatwa,
    onBack: () -> Unit
) {
    var fontSize by remember { mutableStateOf(16.sp) }
    var isBookmarked by remember { mutableStateOf(false) }
    val colors = AppDesign.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = { Text("فتوى ابن باز", fontFamily = AmiriFont) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
            },
            actions = {
                // Font size menu
                var showFontMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showFontMenu = true }) {
                        Icon(Icons.Default.FormatSize, contentDescription = "حجم الخط")
                    }
                    DropdownMenu(
                        expanded = showFontMenu,
                        onDismissRequest = { showFontMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("صغير", fontFamily = AmiriFont) }, onClick = {
                            fontSize = 14.sp
                            showFontMenu = false
                        })
                        DropdownMenuItem(text = { Text("متوسط", fontFamily = AmiriFont) }, onClick = {
                            fontSize = 16.sp
                            showFontMenu = false
                        })
                        DropdownMenuItem(text = { Text("كبير", fontFamily = AmiriFont) }, onClick = {
                            fontSize = 18.sp
                            showFontMenu = false
                        })
                    }
                }

                // Bookmark button
                IconButton(onClick = { isBookmarked = !isBookmarked }) {
                    Icon(
                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ",
                        tint = if (isBookmarked) colors.gold else colors.textSecondary
                    )
                }

                // Share button
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header card with fatwa info
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.goldContainer)
                                    .size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.gold
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    "سماحة الشيخ",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colors.textSecondary,
                                        fontFamily = AmiriFont
                                    )
                                )
                                Text(
                                    "ابن باز رحمه الله",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.gold,
                                        fontFamily = AmiriFont
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (!fatwa.category.isNullOrEmpty()) {
                                Text(
                                    fatwa.category,
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colors.gold,
                                        fontFamily = AmiriFont
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(colors.goldContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = colors.divider,
                            thickness = 1.dp
                        )

                        Text(
                            fatwa.question ?: fatwa.title,
                            style = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont,
                                textAlign = TextAlign.Right,
                                lineHeight = 24.sp
                            )
                        )
                    }
                }
            }

            // Question section
            item {
                fatwaSection(
                    label = "السؤال",
                    icon = Icons.Outlined.Info,
                    iconColor = colors.info,
                    text = fatwa.question ?: fatwa.title,
                    fontSize = fontSize
                )
            }

            // Answer section
            if (!fatwa.answer.isNullOrEmpty()) {
                item {
                    fatwaSection(
                        label = "الجواب",
                        icon = Icons.Default.Check,
                        iconColor = colors.success,
                        text = fatwa.answer ?: "",
                        fontSize = fontSize
                    )
                }
            }

            // Source section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = "مصدر",
                            modifier = Modifier.size(11.dp),
                            tint = colors.textSecondary
                        )
                        Text(
                            "المصدر: ${fatwa.source ?: "binbaz.org.sa"}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

private fun fatwaSection(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit
): @Composable () -> Unit = {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(iconColor.copy(0.14f))
                        .size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(14.dp),
                        tint = iconColor
                    )
                }
                Text(
                    label,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = iconColor,
                        fontFamily = AmiriFont
                    )
                )
            }

            Divider(
                modifier = Modifier.padding(horizontal = 14.dp),
                color = colors.divider,
                thickness = 1.dp
            )

            Text(
                text,
                style = TextStyle(
                    fontSize = fontSize,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont,
                    textAlign = TextAlign.Right,
                    lineHeight = fontSize * 1.6f
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            )
        }
    }
}
