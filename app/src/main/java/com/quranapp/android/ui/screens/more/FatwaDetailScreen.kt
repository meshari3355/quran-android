package com.quranapp.android.ui.screens.more

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.FatwaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatwaDetailScreen(
    fatwaId: Long,
    onBack: () -> Unit,
    viewModel: FatwaViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fatwaId) {
        // Find the fatwa from loaded list or load it
        val fatwa = uiState.fatwas.find { it.id == fatwaId.toInt() }
        if (fatwa != null) {
            viewModel.selectFatwa(fatwa)
        }
    }

    val selectedFatwa = uiState.selectedFatwa

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "تفاصيل الفتوى",
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
                if (selectedFatwa != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Text(
                            selectedFatwa.title,
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Start
                        )

                        // Category
                        if (selectedFatwa.category != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.goldContainer
                                )
                            ) {
                                Text(
                                    selectedFatwa.category,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontFamily = AmiriFont,
                                    fontSize = 14.sp,
                                    color = colors.goldOnContainer
                                )
                            }
                        }

                        Divider()

                        // Question
                        if (selectedFatwa.question != null) {
                            Text(
                                "السؤال",
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.gold
                            )
                            Text(
                                selectedFatwa.question,
                                fontFamily = AmiriFont,
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Start
                            )
                        }

                        // Answer
                        if (selectedFatwa.answer != null) {
                            Divider()
                            Text(
                                "الجواب",
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.gold
                            )
                            Text(
                                selectedFatwa.answer,
                                fontFamily = AmiriFont,
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Start
                            )
                        }

                        // Source
                        if (selectedFatwa.source != null) {
                            Divider()
                            Text(
                                "المصدر: ${selectedFatwa.source}",
                                fontFamily = AmiriFont,
                                fontSize = 14.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                } else if (uiState.isLoading) {
                    QuranLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Text(
                        "لم يتم العثور على الفتوى",
                        modifier = Modifier.align(Alignment.Center),
                        fontFamily = AmiriFont,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
