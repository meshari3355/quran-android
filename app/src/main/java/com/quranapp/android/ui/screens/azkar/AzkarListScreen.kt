package com.quranapp.android.ui.screens.azkar

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.quranapp.android.models.ZikrCategoryType
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.viewmodels.AzkarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarListScreen(
    categoryId: Int,
    onBack: () -> Unit,
    viewModel: AzkarViewModel = hiltViewModel()
) {
    val colors = AppDesign.colors
    val uiState by viewModel.uiState.collectAsState()

    // Map categoryId to ZikrCategoryType
    val categoryType = ZikrCategoryType.entries.getOrNull(categoryId)
    val categoryName = categoryType?.arabicName ?: "الأذكار"

    LaunchedEffect(categoryId) {
        categoryType?.let {
            viewModel.loadCategoryZikrs(it.name)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            categoryName,
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
                    uiState.isLoading -> {
                        QuranLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = colors.gold
                        )
                    }
                    uiState.zikrs.isEmpty() -> {
                        Text(
                            "لا توجد أذكار في هذا القسم",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = AmiriFont,
                            fontSize = 16.sp,
                            color = colors.textSecondary
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.zikrs) { zikr ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = colors.card)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            zikr.text,
                                            fontFamily = AmiriFont,
                                            fontSize = 18.sp,
                                            lineHeight = 32.sp,
                                            textAlign = TextAlign.Start,
                                            color = colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "التكرار: ${zikr.count}",
                                                fontFamily = AmiriFont,
                                                fontSize = 14.sp,
                                                color = colors.gold
                                            )
                                            if (zikr.source.isNotEmpty()) {
                                                Text(
                                                    zikr.source,
                                                    fontFamily = AmiriFont,
                                                    fontSize = 12.sp,
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
}
