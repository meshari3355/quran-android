package com.quranapp.android.ui.screens.azkar

import com.quranapp.android.ui.components.QuranLoadingIndicator

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

data class DhikrOption(
    val id: Int,
    val nameAr: String,
    val textAr: String,
    val color: Color
)

data class CompletedRound(
    val dhikrText: String,
    val count: Int,
    val timestamp: Long
)

@Composable
fun TasbihScreen() {
    val colors = AppDesign.colors
    val dhikrOptions = remember {
        listOf(
            DhikrOption(1, "سبحان الله", "سبحان الله", Color(0xFFE3B4A2)),
            DhikrOption(2, "الحمد لله", "الحمد لله", Color(0xFFD4AF37)),
            DhikrOption(3, "الله أكبر", "الله أكبر", Color(0xFFD4958D)),
            DhikrOption(4, "لا إله إلا الله", "لا إله إلا الله", Color(0xFFB8956F)),
            DhikrOption(5, "أستغفر الله", "أستغفر الله", Color(0xFFE8C9B8))
        )
    }

    var currentCount by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(33) }
    var selectedDhikr by remember { mutableStateOf(dhikrOptions[0]) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var customTarget by remember { mutableStateOf("") }
    var completedRounds by remember { mutableStateOf<List<CompletedRound>>(emptyList()) }
    var showAnimation by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            selectedDhikr.color,
                            selectedDhikr.color.copy(0.8f)
                        )
                    )
                )
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "التسبيح الرقمي",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )
                )
                Text(
                    "اضغط لتزيد العداد",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = colors.textPrimary.copy(0.7f),
                        fontFamily = AmiriFont
                    )
                )
            }
        }

        // Dhikr Selection Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dhikrOptions, key = { it.id }) { option ->
                DhikrOptionChip(
                    option = option,
                    isSelected = selectedDhikr.id == option.id,
                    onClick = {
                        selectedDhikr = option
                        currentCount = 0
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Counter Circle
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.CenterHorizontally)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            selectedDhikr.color.copy(0.2f),
                            selectedDhikr.color.copy(0.05f)
                        )
                    )
                )
                .clickable {
                    if (currentCount < targetCount) {
                        currentCount++
                        showAnimation = true
                        scope.launch {
                            delay(100)
                            showAnimation = false
                        }
                    }
                    if (currentCount == targetCount) {
                        completedRounds = completedRounds + CompletedRound(
                            selectedDhikr.textAr,
                            targetCount,
                            System.currentTimeMillis()
                        )
                    }
                }
                .scale(if (showAnimation) 1.05f else 1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    selectedDhikr.textAr,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedDhikr.color,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = selectedDhikr.color.copy(0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        currentCount.toString(),
                        style = TextStyle(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedDhikr.color,
                            fontFamily = AmiriFont
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "من $targetCount",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )

                // Progress Ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QuranLoadingIndicator(
                        progress = if (targetCount > 0) currentCount.toFloat() / targetCount else 0f,
                        modifier = Modifier
                            .size(160.dp)
                            .scale(-1f, 1f),
                        color = selectedDhikr.color,
                        trackColor = colors.borderSubtle,
                        strokeWidth = 6.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Target and Reset Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showTargetDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "أيقونة", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تعديل العدد", fontFamily = AmiriFont)
            }

            Button(
                onClick = {
                    currentCount = 0
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedDhikr.color.copy(0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "أيقونة", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "إعادة تعيين",
                    fontFamily = AmiriFont,
                    color = selectedDhikr.color
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Completion Status
        if (currentCount == targetCount && currentCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.card)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    selectedDhikr.color.copy(0.15f),
                                    selectedDhikr.color.copy(0.05f)
                                )
                            )
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "أيقونة",
                        modifier = Modifier.size(28.dp),
                        tint = selectedDhikr.color
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "أكملت $targetCount مرة بارك الله فيك",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedDhikr.color,
                            fontFamily = AmiriFont
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History Section
        if (completedRounds.isNotEmpty()) {
            Text(
                "السجل",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(completedRounds, key = { it.timestamp }) { round ->
                    CompletedRoundCard(round)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Target Dialog
    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("تعديل العدد المستهدف", fontFamily = AmiriFont) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(33, 100, 200, 1000).forEach { value ->
                        Button(
                            onClick = {
                                targetCount = value
                                currentCount = 0
                                showTargetDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (targetCount == value) colors.gold else colors.card
                            )
                        ) {
                            Text(
                                value.toString(),
                                color = if (targetCount == value) colors.textPrimary else colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        }
                    }

                    TextField(
                        value = customTarget,
                        onValueChange = { customTarget = it },
                        label = { Text("عدد مخصص", fontFamily = AmiriFont) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.card,
                            unfocusedContainerColor = colors.card
                        )
                    )

                    if (customTarget.isNotEmpty()) {
                        Button(
                            onClick = {
                                targetCount = customTarget.toIntOrNull() ?: 33
                                currentCount = 0
                                customTarget = ""
                                showTargetDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.gold
                            )
                        ) {
                            Text("تطبيق", fontFamily = AmiriFont, color = colors.textPrimary)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showTargetDialog = false }) {
                    Text("إغلاق", fontFamily = AmiriFont)
                }
            }
        )
    }
}

@Composable
fun DhikrOptionChip(
    option: DhikrOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) option.color else colors.card
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) option.color else colors.borderSubtle
        )
    ) {
        Text(
            option.nameAr,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) colors.textPrimary else option.color,
                fontFamily = AmiriFont
            )
        )
    }
}

@Composable
fun CompletedRoundCard(round: CompletedRound) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    round.dhikrText,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    )
                )
                Text(
                    "اليوم في ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(round.timestamp)}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colors.goldContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    round.count.toString(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.gold,
                        fontFamily = AmiriFont
                    )
                )
            }
        }
    }
}
