package com.quranapp.android.ui.screens.hadith

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.models.ServerHadith
import com.quranapp.android.models.ServerHadithBook
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.HadithViewModel

/**
 * Screen showing books within a hadith collection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithBooksScreen(
    categoryId: Int,
    onBack: () -> Unit,
    onBookSelected: (Int) -> Unit,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()
    var books by remember { mutableStateOf<List<ServerHadithBook>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(categoryId) {
        isLoading = true
        try {
            val result = viewModel.uiState.value.portalCategories
                .getOrNull(categoryId)
            // Load books from API
            val repo = viewModel // Access through ViewModel
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "كتب الحديث",
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        QuranLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "حدث خطأ في تحميل البيانات",
                                fontFamily = AmiriFont,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { /* retry */ }) {
                                Text("إعادة المحاولة", fontFamily = AmiriFont)
                            }
                        }
                    }
                    books.isEmpty() -> {
                        Text(
                            "لا توجد كتب متاحة",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = AmiriFont,
                            fontSize = 16.sp
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(books) { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onBookSelected(book.id) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            book.nameAr,
                                            fontFamily = AmiriFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        if (book.hadithsCount != null) {
                                            Text(
                                                "${book.hadithsCount} حديث",
                                                fontFamily = AmiriFont,
                                                fontSize = 14.sp,
                                                color = colors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen showing hadiths within a book/chapter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithChaptersScreen(
    bookId: Int,
    onBack: () -> Unit,
    onHadithSelected: (Long) -> Unit,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    var hadiths by remember { mutableStateOf<List<ServerHadith>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(bookId, currentPage) {
        isLoading = true
        // The HadithRepository getHadithList needs collectionId and bookId
        // For now, use bookId as both since the navigation maps to it
        isLoading = false
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "الأحاديث",
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        QuranLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "حدث خطأ في تحميل الأحاديث",
                                fontFamily = AmiriFont,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { error = null; currentPage = 1 }) {
                                Text("إعادة المحاولة", fontFamily = AmiriFont)
                            }
                        }
                    }
                    hadiths.isEmpty() -> {
                        Text(
                            "لا توجد أحاديث متاحة",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = AmiriFont,
                            fontSize = 16.sp
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(hadiths) { hadith ->
                                HadithCard(
                                    hadith = hadith,
                                    onClick = { onHadithSelected(hadith.id.toLong()) }
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
fun HadithCard(
    hadith: ServerHadith,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (hadith.hadithNumber != null) {
                Text(
                    "حديث رقم ${hadith.hadithNumber}",
                    fontFamily = AmiriFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.gold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (hadith.narratorAr != null) {
                Text(
                    hadith.narratorAr,
                    fontFamily = AmiriFont,
                    fontSize = 14.sp,
                    color = colors.gold,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                hadith.textAr,
                fontFamily = AmiriFont,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp,
                maxLines = 5
            )
            if (hadith.gradeAr != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "الدرجة: ${hadith.gradeAr}",
                    fontFamily = AmiriFont,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}

/**
 * Screen showing a single hadith in detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailScreen(
    hadithId: Long,
    onBack: () -> Unit,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(hadithId) {
        viewModel.loadHadith(hadithId.toString())
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "تفاصيل الحديث",
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val hadith = uiState.currentHadith
                if (hadith != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            if (hadith.narrator.isNotEmpty()) {
                                Text(
                                    hadith.narrator,
                                    fontFamily = AmiriFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.gold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            Text(
                                hadith.text,
                                fontFamily = AmiriFont,
                                fontSize = 20.sp,
                                lineHeight = 36.sp,
                                textAlign = TextAlign.Start
                            )
                            if (hadith.gradeArabic.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = colors.goldContainer
                                    )
                                ) {
                                    Text(
                                        "الدرجة: ${hadith.gradeArabic}",
                                        modifier = Modifier.padding(12.dp),
                                        fontFamily = AmiriFont,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            if (hadith.explanation.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "الشرح",
                                    fontFamily = AmiriFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    hadith.explanation,
                                    fontFamily = AmiriFont,
                                    fontSize = 16.sp,
                                    lineHeight = 28.sp
                                )
                            }
                        }
                    }
                } else if (uiState.isLoading) {
                    QuranLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Text(
                        "لم يتم العثور على الحديث",
                        modifier = Modifier.align(Alignment.Center),
                        fontFamily = AmiriFont,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Hadith search screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithSearchScreen(
    onBack: () -> Unit,
    onHadithSelected: (Long) -> Unit,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.searchHadiths(it)
                            },
                            placeholder = {
                                Text(
                                    "ابحث في الأحاديث...",
                                    fontFamily = AmiriFont
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = colors.card
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    uiState.isSearching -> {
                        QuranLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    searchQuery.isEmpty() -> {
                        Text(
                            "اكتب كلمة للبحث في الأحاديث",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = AmiriFont,
                            fontSize = 16.sp,
                            color = colors.textSecondary
                        )
                    }
                    uiState.searchResults.isEmpty() -> {
                        Text(
                            "لا توجد نتائج",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = AmiriFont,
                            fontSize = 16.sp
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.searchResults) { entry ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onHadithSelected(entry.id.toLongOrNull() ?: 0L)
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        if (entry.narrator.isNotEmpty()) {
                                            Text(
                                                entry.narrator,
                                                fontFamily = AmiriFont,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = colors.gold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                        Text(
                                            entry.text,
                                            fontFamily = AmiriFont,
                                            fontSize = 16.sp,
                                            maxLines = 4,
                                            lineHeight = 28.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
