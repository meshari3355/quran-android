package com.quranapp.android.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.AmiriFont

/**
 * Offline Downloads Management Screen
 * Manage Quran text, audio, hadiths, and tafsir offline downloads
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDownloadsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val colors = AppDesign.colors
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "التحميلات الدون لاين",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Storage info
            item {
                StorageInfoCard(modifier = Modifier.fillMaxWidth())
            }

            // Quran Text
            item {
                DownloadCategoryCard(
                    title = "نص القرآن الكريم",
                    icon = Icons.Filled.MenuBook,
                    status = "تم التحميل",
                    size = "2.3 MB",
                    isDownloaded = true,
                    progress = 1f,
                    onDownload = {},
                    onDelete = {}
                )
            }

            // Audio recitations
            item {
                Text(
                    "تلاوات صوتية",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            items(audioRecitations) { recitation ->
                AudioRecitationCard(
                    recitation = recitation,
                    onDownload = {},
                    onDelete = {}
                )
            }

            // Hadiths
            item {
                DownloadCategoryCard(
                    title = "الأحاديث النبوية",
                    icon = Icons.Default.ListAlt,
                    status = "جاهز للتحميل",
                    size = "5.2 MB",
                    isDownloaded = false,
                    progress = 0f,
                    onDownload = {},
                    onDelete = {}
                )
            }

            // Tafsir books
            item {
                Text(
                    "كتب التفسير",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            items(tafsirDownloads) { tafsir ->
                TafsirDownloadCard(
                    tafsir = tafsir,
                    onDownload = {},
                    onDelete = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StorageInfoCard(
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "معلومات التخزين",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StorageItem(
                    label = "المستخدم",
                    value = "245 MB",
                    modifier = Modifier.weight(1f)
                )

                StorageItem(
                    label = "المتاح",
                    value = "1.2 GB",
                    modifier = Modifier.weight(1f)
                )

                StorageItem(
                    label = "الإجمالي",
                    value = "4 GB",
                    modifier = Modifier.weight(1f)
                )
            }

            // Storage bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = colors.gold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(245f / 4000f)
                        .background(
                            color = colors.gold,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }

            Text(
                "استخدام التخزين: 245 من 4000 MB (6%)",
                fontSize = 10.sp,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun StorageItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = AppDesign.colors
    Column(
        modifier = modifier
            .background(
                color = colors.gold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.gold
        )
    }
}

@Composable
private fun DownloadCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    status: String,
    size: String,
    isDownloaded: Boolean,
    progress: Float,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = colors.gold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = colors.gold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 10.sp,
                            color = if (isDownloaded) colors.success else colors.textSecondary
                        )

                        Text(
                            text = size,
                            fontSize = 10.sp,
                            color = colors.textSecondary.copy(alpha = 0.7f)
                        )
                    }
                }

                if (isDownloaded) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = colors.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = colors.gold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = progress in 0.01f..0.99f) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 9.sp,
                        color = colors.gold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioRecitationCard(
    recitation: AudioRecitation,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFF1a237e).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = recitation.reciterName,
                    tint = Color(0xFF1a237e),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = recitation.reciterName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = recitation.size,
                        fontSize = 9.sp,
                        color = colors.textSecondary
                    )

                    if (recitation.isDownloaded) {
                        Text(
                            text = "محفوظ",
                            fontSize = 9.sp,
                            color = colors.success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (recitation.isDownloaded) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = colors.gold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TafsirDownloadCard(
    tafsir: TafsirDownload,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = tafsir.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryBooks,
                    contentDescription = tafsir.name,
                    tint = tafsir.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = tafsir.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = tafsir.size,
                        fontSize = 9.sp,
                        color = colors.textSecondary
                    )

                    if (tafsir.isDownloaded) {
                        Text(
                            text = "محفوظ",
                            fontSize = 9.sp,
                            color = colors.success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (tafsir.isDownloaded) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = colors.gold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Data classes
data class AudioRecitation(
    val reciterName: String,
    val size: String,
    val isDownloaded: Boolean = false
)

data class TafsirDownload(
    val name: String,
    val size: String,
    val color: Color,
    val isDownloaded: Boolean = false
)

// Sample data
private val audioRecitations = listOf(
    AudioRecitation("عبد الباسط", "125 MB", isDownloaded = true),
    AudioRecitation("محمود الحصري", "118 MB", isDownloaded = true),
    AudioRecitation("ياسين الجزائري", "110 MB"),
    AudioRecitation("خالد الجليل", "122 MB"),
    AudioRecitation("سعد الغامدي", "116 MB")
)

private val tafsirDownloads = listOf(
    TafsirDownload("تفسير ابن كثير", "45 MB", Color(0xFF1a237e), isDownloaded = true),
    TafsirDownload("تفسير السعدي", "38 MB", Color(0xFFff6f00)),
    TafsirDownload("تفسير الجلالين", "22 MB", Color(0xFF00796b))
)
