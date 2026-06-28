package com.quranapp.android.ui.screens.more

import com.quranapp.android.ui.components.QuranLoadingIndicator

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quranapp.android.models.HadithPortalCategory
import com.quranapp.android.models.HadithBookSummary
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.HadithViewModel
import com.quranapp.android.viewmodels.HadithEntry

// ========================================
// MAIN SCREEN
// ========================================

@Composable
fun HadithPortalScreen(
    onBack: () -> Unit
) {
    val viewModel: HadithViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedHadith by remember { mutableStateOf<HadithEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadPortalCategories()
    }

    when {
        selectedHadith != null -> {
            HadithDetailView(
                hadith = selectedHadith!!,
                onBack = { selectedHadith = null }
            )
        }
        selectedCategoryId != null -> {
            LaunchedEffect(selectedCategoryId) {
                viewModel.loadBooks(selectedCategoryId!!)
            }
            HadithChapterListView(
                books = uiState.hadithBooks,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onBack = {
                    selectedCategoryId = null
                    viewModel.clearError()
                },
                onSelectHadith = { selectedHadith = it },
                viewModel = viewModel
            )
        }
        else -> {
            HadithPortalMainView(
                categories = uiState.portalCategories,
                isLoading = uiState.isLoading,
                error = uiState.error,
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) viewModel.searchHadiths(it) else viewModel.clearSearch()
                },
                onSelectCategory = { category -> selectedCategoryId = category.id },
                onClearError = { viewModel.clearError() },
                onBack = onBack
            )
        }
    }
}

// ========================================
// MAIN VIEW
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithPortalMainView(
    categories: List<HadithPortalCategory>,
    isLoading: Boolean,
    error: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (HadithPortalCategory) -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "بوابة الحديث النبوي",
                    fontFamily = AmiriFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.goldOnContainer
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = colors.goldOnContainer
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.gold)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    QuranLoadingIndicator(color = colors.gold)
                }
            }
            error != null -> {
                ErrorView(error = error, onRetry = onClearError)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.lg)
                ) {
                    // Search bar
                    item {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = "ابحث في الأحاديث..."
                        )
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatPill(
                                number = "${categories.sumOf { cat -> cat.books.sumOf { it.hadithCount } }}+",
                                label = "حديث",
                                icon = Icons.Default.MenuBook,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                number = "${categories.size}",
                                label = "مجموعة",
                                icon = Icons.Default.GridView,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Section header
                    item {
                        SectionHeader("جميع المجموعات")
                    }

                    // Categories
                    items(categories, key = { it.id }) { category ->
                        HadithCategoryCard(
                            nameAr = category.categoryName,
                            authorAr = "${category.books.size} كتب",
                            count = category.books.sumOf { it.hadithCount },
                            onClick = { onSelectCategory(category) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(AppDesign.spacing.xl)) }
                }
            }
        }
    }
}

// ========================================
// CHAPTER LIST VIEW
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithChapterListView(
    books: List<HadithBookSummary>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onSelectHadith: (HadithEntry) -> Unit,
    viewModel: HadithViewModel
) {
    val colors = AppDesign.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "الكتب",
                    fontFamily = AmiriFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.goldOnContainer
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = colors.goldOnContainer
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.gold)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    QuranLoadingIndicator(color = colors.gold)
                }
            }
            error != null -> {
                ErrorView(error = error, onRetry = { })
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
                ) {
                    items(books, key = { it.id }) { book ->
                        HadithBookRowItem(book = book, onClick = { })
                    }
                }
            }
        }
    }
}

// ========================================
// DETAIL VIEW
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailView(
    hadith: HadithEntry,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    val context = LocalContext.current
    var fontSize by remember { mutableStateOf(16.sp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "تفاصيل الحديث",
                    fontFamily = AmiriFont,
                    color = colors.goldOnContainer
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = colors.goldOnContainer
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    val newSize = (fontSize.value + 1).coerceAtMost(30f)
                    fontSize = newSize.sp
                }) {
                    Icon(Icons.Default.TextIncrease, contentDescription = "تكبير الخط")
                }
                IconButton(onClick = {
                    val newSize = (fontSize.value - 1).coerceAtLeast(12f)
                    fontSize = newSize.sp
                }) {
                    Icon(Icons.Default.TextDecrease, contentDescription = "تصغير الخط")
                }
                IconButton(onClick = {
                    val shareText = "${hadith.text}\n\nالراوي: ${hadith.narrator}\nالتقدير: ${hadith.gradeArabic}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "مشاركة الحديث"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.gold)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDesign.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.lg)
        ) {
            // Hadith text card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppDesign.radius.card,
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.card)
                ) {
                    Text(
                        hadith.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDesign.spacing.lg),
                        fontSize = fontSize,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right,
                        lineHeight = fontSize * 1.6f
                    )
                }
            }

            // Metadata card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppDesign.radius.input,
                    colors = CardDefaults.cardColors(containerColor = colors.goldContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDesign.spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.xxl)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("الراوي", fontSize = 12.sp, color = colors.textTertiary, fontFamily = AmiriFont)
                            Text(
                                hadith.narrator,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.gold,
                                fontFamily = AmiriFont
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("التقدير", fontSize = 12.sp, color = colors.textTertiary, fontFamily = AmiriFont)
                            Text(
                                hadith.gradeArabic,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.gold,
                                fontFamily = AmiriFont
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========================================
// SHARED COMPONENTS
// ========================================

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit) {
    val colors = AppDesign.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDesign.spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "حدث خطأ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            fontFamily = AmiriFont
        )
        Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
        Text(
            error,
            fontSize = 14.sp,
            color = colors.textSecondary,
            fontFamily = AmiriFont,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppDesign.spacing.lg))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = colors.gold)
        ) {
            Text("حاول مجددا", fontFamily = AmiriFont)
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    val colors = AppDesign.colors

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontFamily = AmiriFont, color = colors.textTertiary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = colors.gold) },
        singleLine = true,
        shape = AppDesign.radius.input,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.card,
            unfocusedContainerColor = colors.card,
            focusedIndicatorColor = colors.gold,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colors.gold
        )
    )
}

@Composable
fun StatPill(
    number: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors

    Column(
        modifier = modifier
            .clip(AppDesign.radius.input)
            .background(colors.card)
            .border(1.dp, colors.gold.copy(alpha = 0.3f), AppDesign.radius.input)
            .padding(AppDesign.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(18.dp), tint = colors.gold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 9.sp, color = colors.textSecondary, fontFamily = AmiriFont)
    }
}

@Composable
fun SectionHeader(title: String) {
    val colors = AppDesign.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            fontFamily = AmiriFont
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Star, contentDescription = title, modifier = Modifier.size(12.dp), tint = colors.gold)
    }
}

@Composable
fun HadithBookRowItem(
    book: HadithBookSummary,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AppDesign.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md)
    ) {
        Box(
            modifier = Modifier
                .clip(AppDesign.radius.badge)
                .background(colors.goldContainer)
                .size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Book, contentDescription = book.bookName, modifier = Modifier.size(15.dp), tint = colors.gold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                book.bookName,
                fontSize = 15.sp,
                color = colors.textPrimary,
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text("${book.hadithCount} حديث", fontSize = 12.sp, color = colors.textTertiary, fontFamily = AmiriFont)
        }
        Icon(Icons.Default.ChevronLeft, contentDescription = "انتقل", modifier = Modifier.size(11.dp), tint = colors.textTertiary)
    }
}

@Composable
fun HadithCategoryCard(
    nameAr: String,
    authorAr: String,
    count: Int,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = AppDesign.radius.input,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.lg)
        ) {
            Box(
                modifier = Modifier
                    .clip(AppDesign.radius.badge)
                    .background(colors.goldContainer)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = nameAr, modifier = Modifier.size(17.dp), tint = colors.gold)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(nameAr, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary, fontFamily = AmiriFont)
                Spacer(modifier = Modifier.height(2.dp))
                Text(authorAr, fontSize = 11.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                Spacer(modifier = Modifier.height(2.dp))
                Text("$count حديث", fontSize = 12.sp, color = colors.textTertiary, fontFamily = AmiriFont)
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = "انتقل", modifier = Modifier.size(11.dp), tint = colors.textTertiary)
        }
    }
}

@Composable
fun HadithPreviewCard(
    hadith: HadithEntry,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = AppDesign.radius.input,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDesign.elevation.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.md)
        ) {
            Text(
                "الحديث ${hadith.number}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.gold,
                fontFamily = AmiriFont
            )
            Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
            Text(
                hadith.text.take(150) + if (hadith.text.length > 150) "..." else "",
                fontSize = 14.sp,
                color = colors.textPrimary,
                fontFamily = AmiriFont,
                textAlign = TextAlign.Right,
                lineHeight = 20.sp,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppDesign.radius.small)
                    .background(colors.goldContainer)
                    .padding(AppDesign.spacing.sm),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    hadith.gradeArabic,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.gold,
                    fontFamily = AmiriFont
                )
            }
        }
    }
}
