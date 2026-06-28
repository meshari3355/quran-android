package com.quranapp.android.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import com.quranapp.android.viewmodels.QuranViewModel
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bookmarks and Last Read Screen
 * Displays user's bookmarked verses, pages, and reading history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onNavigateToReader: (surah: String, ayah: Int, page: Int) -> Unit = { _, _, _ -> },
    onNavigateBack: () -> Unit = {},
    viewModel: QuranViewModel = hiltViewModel()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val colors = AppDesign.colors
        val uiState by viewModel.uiState.collectAsState()
        var selectedTab by remember { mutableStateOf(BookmarkTab.BOOKMARKS) }

        LaunchedEffect(Unit) {
            viewModel.loadBookmarks()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "العلامات والتاريخ",
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
                        containerColor = colors.background
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
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = colors.background,
                    contentColor = colors.gold
                ) {
                    BookmarkTab.values().forEachIndexed { _, tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab }
                        ) {
                            Text(
                                text = tab.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = AmiriFont
                            )
                        }
                    }
                }

                // Content
                when (selectedTab) {
                    BookmarkTab.BOOKMARKS -> {
                        BookmarksTabContent(
                            bookmarks = uiState.bookmarks,
                            surahList = uiState.surahList,
                            onBookmarkDelete = { bookmarkId ->
                                viewModel.removeBookmark(bookmarkId)
                            },
                            onBookmarkClick = { bookmark ->
                                val surah = uiState.surahList.find { it.id == bookmark.surahId }
                                val surahName = surah?.nameAr ?: "سورة"
                                onNavigateToReader(
                                    surahName,
                                    bookmark.ayahNumber,
                                    bookmark.page
                                )
                            }
                        )
                    }
                    BookmarkTab.LAST_READ -> {
                        LastReadTabContent(
                            surahList = uiState.surahList,
                            onItemClick = { surah ->
                                onNavigateToReader(
                                    surah.nameAr,
                                    1,
                                    surah.pageNumber
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarksTabContent(
    bookmarks: List<com.quranapp.android.viewmodels.Bookmark>,
    surahList: List<com.quranapp.android.models.Surah>,
    onBookmarkDelete: (Long) -> Unit,
    onBookmarkClick: (com.quranapp.android.viewmodels.Bookmark) -> Unit
) {
    if (bookmarks.isEmpty()) {
        EmptyState(
            icon = Icons.Default.BookmarkBorder,
            title = "لا توجد علامات",
            message = "لم تقم بحفظ أي آيات أو صفحات بعد"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = bookmarks,
                key = { it.id }
            ) { bookmark ->
                val surah = surahList.find { it.id == bookmark.surahId }
                BookmarkCard(
                    bookmark = bookmark,
                    surahName = surah?.nameAr ?: "سورة",
                    onDelete = { onBookmarkDelete(bookmark.id) },
                    onClick = { onBookmarkClick(bookmark) }
                )
            }
        }
    }
}

@Composable
private fun BookmarkCard(
    bookmark: com.quranapp.android.viewmodels.Bookmark,
    surahName: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    val formattedDate = dateFormat.format(Date(bookmark.timestamp))

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
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = surahName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )

                    Text(
                        text = "آية ${bookmark.ayahNumber}",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )

                    if (bookmark.notes.isNotEmpty()) {
                        Text(
                            text = bookmark.notes,
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                            fontFamily = AmiriFont
                        )
                    }
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "حذف",
                        tint = Color(0xFFE53935)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colors.gold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الصفحة ${bookmark.page}",
                    fontSize = 10.sp,
                    color = colors.gold,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AmiriFont
                )

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = colors.textSecondary,
                    fontFamily = AmiriFont
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف العلامة", fontFamily = AmiriFont) },
            text = { Text("هل تريد حذف هذه العلامة؟", fontFamily = AmiriFont) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("حذف", fontFamily = AmiriFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("إلغاء", fontFamily = AmiriFont)
                }
            }
        )
    }
}

@Composable
private fun LastReadTabContent(
    surahList: List<com.quranapp.android.models.Surah>,
    onItemClick: (com.quranapp.android.models.Surah) -> Unit
) {
    if (surahList.isEmpty()) {
        EmptyState(
            icon = Icons.Default.HistoryEdu,
            title = "لا يوجد سجل",
            message = "لم تقم بقراءة القرآن بعد"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = surahList.take(10),
                key = { it.id }
            ) { surah ->
                LastReadCard(
                    surah = surah,
                    onClick = { onItemClick(surah) }
                )
            }
        }
    }
}

@Composable
private fun LastReadCard(
    surah: com.quranapp.android.models.Surah,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    val progress = (surah.versesCount / 6236f).coerceIn(0f, 1f)

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
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = surah.nameAr,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )

                    Text(
                        text = "الصفحة ${surah.pageNumber}",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )

                    Text(
                        text = "${surah.versesCount} آية",
                        fontSize = 10.sp,
                        color = Color(0xFF9E8B7B),
                        fontFamily = AmiriFont
                    )
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = colors.gold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(2.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            color = colors.gold,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            Text(
                text = "${(progress * 100).toInt()}% من القرآن الكريم",
                fontSize = 10.sp,
                color = colors.gold,
                fontWeight = FontWeight.SemiBold,
                fontFamily = AmiriFont
            )
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    val colors = AppDesign.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colors.gold.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            fontFamily = AmiriFont
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            color = colors.textSecondary,
            modifier = Modifier.padding(horizontal = 24.dp),
            fontFamily = AmiriFont
        )
    }
}

enum class BookmarkTab(val displayName: String) {
    BOOKMARKS("العلامات"),
    LAST_READ("آخر مقروء")
}
