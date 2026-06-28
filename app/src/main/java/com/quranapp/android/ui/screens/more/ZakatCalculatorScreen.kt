package com.quranapp.android.ui.screens.more

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(onBack: () -> Unit) {
    val colors = AppDesign.colors
    var selectedTab by remember { mutableIntStateOf(0) }
    var goldWeight by remember { mutableStateOf("") }
    var goldKarat by remember { mutableStateOf("24") }
    var silverWeight by remember { mutableStateOf("") }
    var savingsAmount by remember { mutableStateOf("") }
    var investmentsAmount by remember { mutableStateOf("") }
    var cashAmount by remember { mutableStateOf("") }
    var tradeGoodsValue by remember { mutableStateOf("") }
    var livestockType by remember { mutableStateOf("الإبل") }
    var livestockCount by remember { mutableStateOf("") }
    var goldPricePerGram by remember { mutableStateOf("250") } // SR per gram
    var silverPricePerGram by remember { mutableStateOf("15") } // SR per gram

    val tabs = listOf("الذهب", "الفضة", "النقود", "التجارة", "المواشي")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "حاسبة الزكاة",
                    style = TextStyle(
                        fontFamily = AmiriFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "أيقونة")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.gold
            )
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            containerColor = colors.background,
            contentColor = colors.gold,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(colors.gold)
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tab, fontFamily = AmiriFont, fontSize = 12.sp) }
                )
            }
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Gold Tab
                    item {
                        Text(
                            "وزن الذهب بالجرام",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = goldWeight,
                            onValueChange = { goldWeight = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل الوزن", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Text(
                            "عيار الذهب",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("24", "22", "21", "18").forEach { karat ->
                                Button(
                                    onClick = { goldKarat = karat },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (goldKarat == karat) colors.gold else colors.card
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        karat,
                                        color = if (goldKarat == karat) colors.textPrimary else colors.textSecondary,
                                        fontFamily = AmiriFont
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "سعر الجرام الحالي: $goldPricePerGram ر.س",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        CalculateZakatButton(
                            goldWeight = goldWeight,
                            goldKarat = goldKarat,
                            pricePerGram = goldPricePerGram
                        )
                    }
                }

                1 -> {
                    // Silver Tab
                    item {
                        Text(
                            "وزن الفضة بالجرام",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = silverWeight,
                            onValueChange = { silverWeight = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل الوزن", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Text(
                            "سعر الجرام الحالي: $silverPricePerGram ر.س",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        CalculateSilverZakatButton(silverWeight, silverPricePerGram)
                    }
                }

                2 -> {
                    // Cash Tab
                    item {
                        Text(
                            "المدخرات",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = savingsAmount,
                            onValueChange = { savingsAmount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل المبلغ", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Text(
                            "الاستثمارات",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = investmentsAmount,
                            onValueChange = { investmentsAmount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل المبلغ", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Text(
                            "النقد في اليد",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = cashAmount,
                            onValueChange = { cashAmount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل المبلغ", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        CalculateCashZakatButton(
                            savingsAmount,
                            investmentsAmount,
                            cashAmount
                        )
                    }
                }

                3 -> {
                    // Trade Tab
                    item {
                        Text(
                            "قيمة البضاعة",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = tradeGoodsValue,
                            onValueChange = { tradeGoodsValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل القيمة", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        CalculateTradeZakatButton(tradeGoodsValue)
                    }
                }

                4 -> {
                    // Livestock Tab
                    item {
                        Text(
                            "نوع الماشية",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("الإبل", "البقر", "الغنم").forEach { type ->
                                Button(
                                    onClick = { livestockType = type },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (livestockType == type) colors.gold else colors.card
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        type,
                                        color = if (livestockType == type) colors.textPrimary else colors.textSecondary,
                                        fontFamily = AmiriFont,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "العدد",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )
                    }

                    item {
                        TextField(
                            value = livestockCount,
                            onValueChange = { livestockCount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = { Text("أدخل العدد", fontFamily = AmiriFont) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.card,
                                unfocusedContainerColor = colors.card,
                                focusedIndicatorColor = colors.gold
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        CalculateLivestockZakatButton(livestockType, livestockCount)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CalculateZakatButton(
    goldWeight: String,
    goldKarat: String,
    pricePerGram: String
) {
    val colors = AppDesign.colors
    val weight = goldWeight.toDoubleOrNull() ?: 0.0
    val karatValue = goldKarat.toDoubleOrNull() ?: 24.0
    val price = pricePerGram.toDoubleOrNull() ?: 0.0

    val totalValue = (weight * (karatValue / 24.0)) * price
    val zakatAmount = totalValue * 0.025

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.gold.copy(0.15f), colors.gold.copy(0.05f))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "النتائج",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("القيمة الإجمالية", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", totalValue), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("الزكاة (2.5%)", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", zakatAmount), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("احسبها", fontFamily = AmiriFont, color = colors.textPrimary)
            }
        }
    }
}

@Composable
fun CalculateSilverZakatButton(
    silverWeight: String,
    pricePerGram: String
) {
    val colors = AppDesign.colors
    val weight = silverWeight.toDoubleOrNull() ?: 0.0
    val price = pricePerGram.toDoubleOrNull() ?: 0.0

    val totalValue = weight * price
    val zakatAmount = totalValue * 0.025

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.gold.copy(0.15f), colors.gold.copy(0.05f))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "النتائج",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("القيمة الإجمالية", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", totalValue), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("الزكاة (2.5%)", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", zakatAmount), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("احسبها", fontFamily = AmiriFont, color = colors.textPrimary)
            }
        }
    }
}

@Composable
fun CalculateCashZakatButton(
    savingsAmount: String,
    investmentsAmount: String,
    cashAmount: String
) {
    val colors = AppDesign.colors
    val savings = savingsAmount.toDoubleOrNull() ?: 0.0
    val investments = investmentsAmount.toDoubleOrNull() ?: 0.0
    val cash = cashAmount.toDoubleOrNull() ?: 0.0

    val totalValue = savings + investments + cash
    val zakatAmount = totalValue * 0.025

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.gold.copy(0.15f), colors.gold.copy(0.05f))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "النتائج",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("الإجمالي", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", totalValue), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("الزكاة (2.5%)", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", zakatAmount), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("احسبها", fontFamily = AmiriFont, color = colors.textPrimary)
            }
        }
    }
}

@Composable
fun CalculateTradeZakatButton(tradeGoodsValue: String) {
    val colors = AppDesign.colors
    val value = tradeGoodsValue.toDoubleOrNull() ?: 0.0
    val zakatAmount = value * 0.025

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.gold.copy(0.15f), colors.gold.copy(0.05f))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "النتائج",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("القيمة", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", value), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("الزكاة (2.5%)", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text(String.format("%.2f ر.س", zakatAmount), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("احسبها", fontFamily = AmiriFont, color = colors.textPrimary)
            }
        }
    }
}

@Composable
fun CalculateLivestockZakatButton(livestockType: String, livestockCount: String) {
    val colors = AppDesign.colors
    val count = livestockCount.toIntOrNull() ?: 0

    val (nisab, zakatInfo) = when (livestockType) {
        "الإبل" -> Pair(5, calculateCamelZakat(count))
        "البقر" -> Pair(30, calculateCattleZakat(count))
        else -> Pair(40, calculateSheepZakat(count))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.gold.copy(0.15f), colors.gold.copy(0.05f))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "النتائج",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("النصاب", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text("$nisab", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontFamily = AmiriFont))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("الزكاة الواجبة", style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont))
                    Text("$zakatInfo", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("احسبها", fontFamily = AmiriFont, color = colors.textPrimary)
            }
        }
    }
}

fun calculateCamelZakat(count: Int): String {
    return when {
        count < 5 -> "لا زكاة"
        count <= 9 -> "شاة واحدة"
        count <= 14 -> "شاتان"
        count <= 19 -> "3 شياه"
        count <= 24 -> "4 شياه"
        count <= 30 -> "بنت مخاضة"
        count <= 40 -> "بنت لبون"
        count <= 50 -> "حقة"
        count <= 60 -> "جذعة"
        else -> "شاة لكل 5"
    }
}

fun calculateCattleZakat(count: Int): String {
    return when {
        count < 30 -> "لا زكاة"
        count <= 39 -> "تبيع أو تبيعة"
        count <= 50 -> "مسنة"
        count <= 60 -> "تبيعان أو تبيعتان"
        else -> "مسنة لكل 30 + تبيع لكل 40"
    }
}

fun calculateSheepZakat(count: Int): String {
    return when {
        count < 40 -> "لا زكاة"
        count <= 120 -> "شاة واحدة"
        count <= 200 -> "شاتان"
        count <= 300 -> "3 شياه"
        else -> "شاة لكل 100"
    }
}
