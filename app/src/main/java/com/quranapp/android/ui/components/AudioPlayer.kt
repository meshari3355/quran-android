package com.quranapp.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quranapp.android.services.RepeatMode
import com.quranapp.android.models.Reciter
import com.quranapp.android.models.Surah

/**
 * Reusable audio player component for Quran recitation
 * Supports compact bar and expanded player modes
 */
@Composable
fun AudioPlayer(
    reciter: Reciter? = null,
    surah: Surah? = null,
    reciterName: String = "عبد الباسط",
    surahName: String = "سورة الفاتحة",
    ayahNumber: Int = 1,
    isPlaying: Boolean = false,
    progress: Float = 0.3f,
    totalDuration: Long = 120,
    currentTime: Long = 36,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableStateOf(RepeatMode.NONE) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Use model data if available, otherwise use string fallbacks
    val displayReciterName = reciter?.nameAr ?: reciterName
    val displaySurahName = surah?.nameAr ?: surahName

    AnimatedVisibility(
        visible = true,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        if (isExpanded) {
            ExpandedAudioPlayer(
                reciterName = displayReciterName,
                surahName = displaySurahName,
                ayahNumber = ayahNumber,
                isPlaying = isPlaying,
                progress = progress,
                totalDuration = totalDuration,
                currentTime = currentTime,
                repeatMode = repeatMode,
                playbackSpeed = playbackSpeed,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onClose = {
                    isExpanded = false
                    onClose()
                },
                onRepeatModeChange = { repeatMode = it },
                onPlaybackSpeedChange = { playbackSpeed = it },
                modifier = modifier
            )
        } else {
            CompactAudioPlayer(
                reciterName = displayReciterName,
                surahName = displaySurahName,
                ayahNumber = ayahNumber,
                isPlaying = isPlaying,
                progress = progress,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onClose = onClose,
                onExpand = { isExpanded = true },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CompactAudioPlayer(
    reciterName: String,
    surahName: String,
    ayahNumber: Int,
    isPlaying: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5E6D3),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Slider(
            value = progress,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFD4A574),
                activeTrackColor = Color(0xFFD4A574)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpand)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reciter and Surah info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onExpand)
            ) {
                Text(
                    text = reciterName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5D4037),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$surahName - الآية $ayahNumber",
                    fontSize = 10.sp,
                    color = Color(0xFF795548),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Previous button
            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Play/Pause button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFD4A574),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Next button
            IconButton(
                onClick = onNext,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ExpandedAudioPlayer(
    reciterName: String,
    surahName: String,
    ayahNumber: Int,
    isPlaying: Boolean,
    progress: Float,
    totalDuration: Long,
    currentTime: Long,
    repeatMode: RepeatMode,
    playbackSpeed: Float,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    onRepeatModeChange: (RepeatMode) -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5E6D3),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Collapse",
                    tint = Color(0xFFD4A574)
                )
            }
            Text(
                text = "تشغيل القرآن",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color(0xFFD4A574)
                )
            }
        }

        // Surah and Ayah info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = surahName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = "الآية $ayahNumber",
                fontSize = 14.sp,
                color = Color(0xFF795548),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Reciter
        Text(
            text = reciterName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5D4037)
        )

        // Animated Equalizer
        AnimatedEqualizer(
            isPlaying = isPlaying,
            modifier = Modifier.height(40.dp)
        )

        // Progress slider
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = progress,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFD4A574),
                    activeTrackColor = Color(0xFFD4A574),
                    inactiveTrackColor = Color(0xFFD4A574).copy(alpha = 0.3f)
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentTime),
                    fontSize = 10.sp,
                    color = Color(0xFF795548)
                )
                Text(
                    text = formatDuration(totalDuration),
                    fontSize = 10.sp,
                    color = Color(0xFF795548)
                )
            }
        }

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onRepeatModeChange(repeatMode.next()) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.NONE -> Icons.Default.RepeatOneOn
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                        RepeatMode.ALL -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode == RepeatMode.NONE) Color(0xFFD4A574).copy(alpha = 0.5f)
                    else Color(0xFFD4A574),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFD4A574),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier.size(24.dp)
                )
            }

            SpeedButton(
                currentSpeed = playbackSpeed,
                onSpeedChange = onPlaybackSpeedChange,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun SpeedButton(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedMenu by remember { mutableStateOf(false) }
    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { showSpeedMenu = !showSpeedMenu },
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                Color(0xFFD4A574)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "%.2fx".format(currentSpeed),
                fontSize = 10.sp,
                color = Color(0xFFD4A574),
                fontWeight = FontWeight.Bold
            )
        }

        DropdownMenu(
            expanded = showSpeedMenu,
            onDismissRequest = { showSpeedMenu = false }
        ) {
            speeds.forEach { speed ->
                DropdownMenuItem(
                    text = { Text("%.2fx".format(speed)) },
                    onClick = {
                        onSpeedChange(speed)
                        showSpeedMenu = false
                    }
                )
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return "%02d:%02d".format(minutes, seconds)
}
