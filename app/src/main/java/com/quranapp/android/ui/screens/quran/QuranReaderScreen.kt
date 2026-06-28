package com.quranapp.android.ui.screens.quran

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import com.quranapp.android.viewmodels.QuranViewModel
import com.quranapp.android.models.ServerVerse
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import kotlin.math.abs

// ========================================
// DATA MODELS
// ========================================

data class ReaderVerse(
    val number: Int,
    val surahId: Int = 1,
    val text: String,
    val arabicText: String,
    val translation: String = ""
)

// ========================================
// MAIN SCREEN
// ========================================

@Composable
fun QuranReaderScreen(
    surahId: Int = 1,
    surahName: String = "الفاتحة",
    pageNumber: Int = 1,
    juzNumber: Int = 1,
    onNavigateToTafsir: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuranViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Reader-local state
    var fontSize by remember { mutableStateOf(22.sp) }
    var showTranslation by remember { mutableStateOf(false) }
    var selectedVerseNumber by remember { mutableStateOf(-1) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(pageNumber) }
    var currentJuz by remember { mutableStateOf(juzNumber) }
    var showVerseOptions by remember { mutableStateOf<Int?>(null) }
    var isLoadingVerses by remember { mutableStateOf(true) }
    var loadedVerses by remember { mutableStateOf<List<ReaderVerse>>(emptyList()) }
    var currentSurahName by remember { mutableStateOf(surahName) }
    var currentSurahId by remember { mutableStateOf(surahId) }

    // Drag accumulator for page swipe
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    val listState = rememberLazyListState()

    // Check if current page is bookmarked
    val isBookmarked = uiState.bookmarks.any { it.page == currentPage }

    // Load verses from API when page changes
    LaunchedEffect(currentPage) {
        isLoadingVerses = true
        try {
            val result = viewModel.loadPageVerses(currentPage)
            result.onSuccess { serverVerses ->
                loadedVerses = serverVerses.map { sv ->
                    ReaderVerse(
                        number = sv.verseNumber,
                        surahId = sv.suraId,
                        text = sv.textSimple ?: sv.textUthmani,
                        arabicText = sv.textUthmani,
                        translation = sv.translations?.firstOrNull()?.text ?: ""
                    )
                }
                serverVerses.firstOrNull()?.suraNameAr?.let { name ->
                    currentSurahName = name
                }
                serverVerses.firstOrNull()?.suraId?.let { id ->
                    currentSurahId = id
                }
                serverVerses.firstOrNull()?.juz?.let { juz ->
                    currentJuz = juz
                }
            }
        } catch (_: Exception) { }
        isLoadingVerses = false
    }

    // Scroll to top when page changes
    LaunchedEffect(currentPage) {
        listState.scrollToItem(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragAccumulator > 120 && currentPage > 1) {
                            currentPage = maxOf(1, currentPage - 1)
                        } else if (dragAccumulator < -120 && currentPage < 604) {
                            currentPage = minOf(604, currentPage + 1)
                        }
                        dragAccumulator = 0f
                    },
                    onDragCancel = { dragAccumulator = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragAccumulator += dragAmount
                    }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            ReaderTopBar(
                surahName = currentSurahName,
                pageNumber = currentPage,
                juzNumber = currentJuz,
                onNavigateBack = onNavigateBack,
                onSettingsClick = { showSettingsMenu = !showSettingsMenu }
            )

            // Settings Panel (collapsible)
            if (showSettingsMenu) {
                ReaderSettingsPanel(
                    fontSize = fontSize,
                    onFontSizeChange = { fontSize = it },
                    showTranslation = showTranslation,
                    onTranslationToggle = { showTranslation = !showTranslation }
                )
            }

            // Verse Content
            when {
                isLoadingVerses && loadedVerses.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        QuranLoadingIndicator(color = colors.gold)
                    }
                }
                loadedVerses.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد آيات",
                            color = colors.textTertiary,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.md),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
                        contentPadding = PaddingValues(vertical = AppDesign.spacing.sm)
                    ) {
                        items(
                            count = loadedVerses.size,
                            key = { index -> loadedVerses[index].number }
                        ) { index ->
                            val verse = loadedVerses[index]
                            val isSelected = selectedVerseNumber == verse.number

                            VerseCard(
                                verse = verse,
                                isSelected = isSelected,
                                showTranslation = showTranslation,
                                fontSize = fontSize,
                                onVerseClick = {
                                    selectedVerseNumber = if (isSelected) -1 else verse.number
                                    showVerseOptions = null
                                },
                                onTafsirClick = {
                                    onNavigateToTafsir(verse.number)
                                },
                                onShareClick = {
                                    shareVerse(context, verse, currentSurahName)
                                },
                                onAudioClick = {
                                    viewModel.startAudioPlayback(currentSurahId, 1)
                                }
                            )

                            // Verse options expanded menu
                            if (showVerseOptions == verse.number) {
                                VerseExpandedOptions(
                                    verse = verse,
                                    onTafsirClick = {
                                        onNavigateToTafsir(verse.number)
                                        showVerseOptions = null
                                    },
                                    onDismiss = { showVerseOptions = null }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Bar with page navigation and audio
            ReaderBottomBar(
                currentPage = currentPage,
                isAudioPlaying = uiState.audioState.isPlaying,
                audioProgress = uiState.audioState.progress,
                onAudioToggle = {
                    if (uiState.audioState.isPlaying) {
                        viewModel.pauseAudioPlayback()
                    } else {
                        viewModel.startAudioPlayback(currentSurahId, 1)
                    }
                },
                onPreviousPage = { if (currentPage > 1) currentPage-- },
                onNextPage = { if (currentPage < 604) currentPage++ }
            )
        }

        // Bookmark FAB — actually saves/removes
        FloatingActionButton(
            onClick = {
                if (isBookmarked) {
                    val bookmarkToRemove = uiState.bookmarks.find { it.page == currentPage }
                    bookmarkToRemove?.let { viewModel.removeBookmark(it.id) }
                } else {
                    val firstVerse = loadedVerses.firstOrNull()
                    viewModel.addBookmark(
                        surahId = currentSurahId,
                        ayahNumber = firstVerse?.number ?: 1,
                        page = currentPage
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = AppDesign.spacing.lg, bottom = 80.dp),
            containerColor = if (isBookmarked) colors.gold else colors.gold.copy(alpha = 0.7f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "علامة مرجعية",
                tint = colors.goldOnContainer,
                modifier = Modifier.size(AppDesign.iconSize.medium)
            )
        }
    }
}

// ========================================
// TOP BAR
// ========================================

@Composable
private fun ReaderTopBar(
    surahName: String,
    pageNumber: Int,
    juzNumber: Int,
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val colors = AppDesign.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.card,
        shadowElevation = AppDesign.elevation.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = colors.gold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = surahName,
                    fontSize = 18.sp,
                    fontFamily = AmiriFont,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الصفحة $pageNumber",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = colors.gold.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "الجزء $juzNumber",
                        fontSize = 12.sp,
                        color = colors.gold
                    )
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "إعدادات",
                    tint = colors.gold
                )
            }
        }
    }
}

// ========================================
// SETTINGS PANEL
// ========================================

@Composable
private fun ReaderSettingsPanel(
    fontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit,
    showTranslation: Boolean,
    onTranslationToggle: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDesign.spacing.lg, vertical = AppDesign.spacing.sm)
            .animateContentSize(),
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(containerColor = colors.goldContainer),
        border = AppDesign.goldBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.md)
        ) {
            Text(
                text = "إعدادات القراءة",
                fontSize = 14.sp,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )

            // Font size controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حجم الخط",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newSize = (fontSize.value - 2).coerceAtLeast(14f)
                            onFontSizeChange(newSize.sp)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("−", fontSize = 20.sp, color = colors.gold)
                    }
                    Text(
                        text = fontSize.value.toInt().toString(),
                        fontSize = 14.sp,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            val newSize = (fontSize.value + 2).coerceAtMost(40f)
                            onFontSizeChange(newSize.sp)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("+", fontSize = 20.sp, color = colors.gold)
                    }
                }
            }

            // Translation toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الترجمة",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Switch(
                    checked = showTranslation,
                    onCheckedChange = { onTranslationToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.gold,
                        uncheckedThumbColor = colors.disabled
                    )
                )
            }
        }
    }
}

// ========================================
// VERSE CARD
// ========================================

@Composable
private fun VerseCard(
    verse: ReaderVerse,
    isSelected: Boolean,
    showTranslation: Boolean,
    fontSize: TextUnit,
    onVerseClick: () -> Unit,
    onTafsirClick: () -> Unit,
    onShareClick: () -> Unit,
    onAudioClick: () -> Unit
) {
    val colors = AppDesign.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppDesign.radius.badge)
            .background(
                if (isSelected) colors.goldContainer
                else Color.Transparent
            )
            .clickable { onVerseClick() }
            .padding(AppDesign.spacing.sm)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.md),
            verticalAlignment = Alignment.Top
        ) {
            // Verse number badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colors.gold.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = verse.number.toString(),
                    fontSize = 10.sp,
                    color = colors.gold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Verse text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.xs)
            ) {
                Text(
                    text = verse.arabicText,
                    fontSize = fontSize,
                    fontFamily = AmiriFont,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Right,
                    lineHeight = fontSize * 1.8f,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showTranslation && verse.translation.isNotEmpty()) {
                    Divider(
                        color = colors.divider,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = AppDesign.spacing.xxs)
                    )
                    Text(
                        text = verse.translation,
                        fontSize = fontSize * 0.7f,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Start,
                        lineHeight = fontSize * 1.2f,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // Action buttons when selected
        if (isSelected) {
            Spacer(modifier = Modifier.height(AppDesign.spacing.sm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp),
                horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
            ) {
                VerseActionChip(
                    icon = Icons.Default.VolumeUp,
                    label = "استماع",
                    onClick = onAudioClick
                )
                VerseActionChip(
                    icon = Icons.Default.MenuBook,
                    label = "تفسير",
                    onClick = onTafsirClick
                )
                VerseActionChip(
                    icon = Icons.Default.Share,
                    label = "مشاركة",
                    onClick = onShareClick
                )
            }
        }
    }
}

// ========================================
// VERSE ACTION CHIP
// ========================================

@Composable
private fun VerseActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors

    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.gold.copy(alpha = 0.15f)
        ),
        shape = AppDesign.radius.small,
        contentPadding = PaddingValues(horizontal = AppDesign.spacing.sm, vertical = 0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colors.gold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(AppDesign.spacing.xxs))
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textPrimary
        )
    }
}

// ========================================
// VERSE EXPANDED OPTIONS
// ========================================

@Composable
private fun VerseExpandedOptions(
    verse: ReaderVerse,
    onTafsirClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppDesign.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDesign.spacing.sm, vertical = AppDesign.spacing.sm)
            .animateContentSize(),
        shape = AppDesign.radius.badge,
        colors = CardDefaults.cardColors(containerColor = colors.goldContainer),
        border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.md),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "خيارات الآية ${verse.number}",
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Button(
                onClick = onTafsirClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.gold),
                shape = AppDesign.radius.small
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = colors.goldOnContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(AppDesign.spacing.sm))
                Text(
                    text = "عرض التفسير",
                    fontSize = 13.sp,
                    color = colors.goldOnContainer
                )
            }
        }
    }
}

// ========================================
// BOTTOM BAR
// ========================================

@Composable
private fun ReaderBottomBar(
    currentPage: Int,
    isAudioPlaying: Boolean,
    audioProgress: Float,
    onAudioToggle: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    val colors = AppDesign.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.card,
        shadowElevation = AppDesign.elevation.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDesign.spacing.md),
            verticalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm)
        ) {
            // Audio indicator when playing
            if (isAudioPlaying) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(AppDesign.radius.small)
                        .background(colors.goldContainer)
                        .padding(AppDesign.spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(AppDesign.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "الصوت",
                        tint = colors.gold,
                        modifier = Modifier.size(AppDesign.iconSize.small)
                    )
                    Text(
                        text = "جاري التشغيل",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    LinearProgressIndicator(
                        progress = audioProgress,
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(AppDesign.radius.small),
                        color = colors.gold,
                        trackColor = colors.gold.copy(alpha = 0.2f)
                    )
                }
            }

            // Page navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousPage) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = "الصفحة السابقة",
                        tint = if (currentPage > 1) colors.gold else colors.disabled
                    )
                }

                Text(
                    text = "$currentPage / 604",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )

                IconButton(
                    onClick = onAudioToggle,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isAudioPlaying) colors.gold.copy(alpha = 0.2f) else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isAudioPlaying) "إيقاف" else "تشغيل",
                        tint = colors.gold
                    )
                }

                Text(
                    text = "",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )

                IconButton(onClick = onNextPage) {
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = "الصفحة التالية",
                        tint = if (currentPage < 604) colors.gold else colors.disabled
                    )
                }
            }
        }
    }
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

private fun shareVerse(context: android.content.Context, verse: ReaderVerse, surahName: String) {
    val shareText = "${verse.arabicText}\n\n— سورة $surahName، الآية ${verse.number}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة الآية"))
}
