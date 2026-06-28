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
import com.quranapp.android.viewmodels.HadithViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NawawiDetailScreen(
    hadithId: Long,
    onBack: () -> Unit,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(hadithId) {
        viewModel.loadHadith(hadithId.toString())
    }

    val hadith = uiState.currentHadith

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (hadith != null) "الحديث رقم ${hadith.number}" else "تفاصيل الحديث",
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
                if (hadith != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Hadith number badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colors.goldContainer
                            )
                        ) {
                            Text(
                                "الحديث النووي رقم ${hadith.number}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colors.gold
                            )
                        }

                        // Narrator
                        if (hadith.narrator.isNotEmpty()) {
                            Text(
                                hadith.narrator,
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colors.gold,
                                textAlign = TextAlign.Start
                            )
                        }

                        Divider()

                        // Hadith text
                        Text(
                            hadith.text,
                            fontFamily = AmiriFont,
                            fontSize = 20.sp,
                            lineHeight = 38.sp,
                            textAlign = TextAlign.Start
                        )

                        // Grade
                        if (hadith.gradeArabic.isNotEmpty()) {
                            Divider()
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.goldContainer
                                )
                            ) {
                                Text(
                                    "الدرجة: ${hadith.gradeArabic}",
                                    modifier = Modifier.padding(12.dp),
                                    fontFamily = AmiriFont,
                                    fontSize = 14.sp,
                                    color = colors.gold
                                )
                            }
                        }

                        // Explanation
                        if (hadith.explanation.isNotEmpty()) {
                            Divider()
                            Text(
                                "الشرح",
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.gold
                            )
                            Text(
                                hadith.explanation,
                                fontFamily = AmiriFont,
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Start
                            )
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
