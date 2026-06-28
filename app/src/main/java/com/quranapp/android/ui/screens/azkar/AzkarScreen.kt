package com.quranapp.android.ui.screens.azkar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

data class AzkarCategory(
    val id: Int,
    val nameAr: String,
    val itemCount: Int,
    val color: Color
)

data class ZikrItem(
    val id: Int,
    val textAr: String,
    val repetitions: Int,
    val currentCount: Int = 0,
    val source: String,
    val completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen() {
    val colors = AppDesign.colors
    val azkarCategories = remember {
        listOf(
            AzkarCategory(1, "أذكار الصباح", 33, Color(0xFFE3B4A2)),
            AzkarCategory(2, "أذكار المساء", 28, Color(0xFFD4958D)),
            AzkarCategory(3, "أذكار النوم", 25, Color(0xFFB8956F)),
            AzkarCategory(4, "أذكار الاستيقاظ", 20, Color(0xFFE8C9B8)),
            AzkarCategory(5, "أذكار الطعام", 15, Color(0xFFF4D4A8)),
            AzkarCategory(6, "أذكار الشراب", 12, Color(0xFFE8B8A8)),
            AzkarCategory(7, "أذكار الدخول", 18, Color(0xFFD9B8A8)),
            AzkarCategory(8, "أذكار الخروج", 16, Color(0xFFC9A898)),
            AzkarCategory(9, "أذكار الصلاة", 22, Color(0xFFE8D4B8)),
            AzkarCategory(10, "أذكار المسجد", 19, Color(0xFFD8C4A8)),
            AzkarCategory(11, "أذكار السفر", 24, Color(0xFFE0B8A0)),
            AzkarCategory(12, "أذكار الرضا", 21, Color(0xFFC8B098))
        )
    }

    val duaCategories = remember {
        listOf(
            AzkarCategory(13, "الدعاء للعلم", 15, Color(0xFFD4AF37)),
            AzkarCategory(14, "الدعاء للرزق", 12, Color(0xFFE8C5A0)),
            AzkarCategory(15, "الدعاء للشفاء", 18, Color(0xFFF0D5B0)),
            AzkarCategory(16, "دعاء الهم والحزن", 14, Color(0xFFD8B8A8)),
            AzkarCategory(17, "الدعاء للعائلة", 16, Color(0xFFF8D8A8)),
            AzkarCategory(18, "الدعاء قبل النوم", 11, Color(0xFFC8A888)),
            AzkarCategory(19, "الدعاء في القرآن", 20, Color(0xFFE0C8B0)),
            AzkarCategory(20, "أدعية مأثورة", 25, Color(0xFFD0A880)),
            AzkarCategory(21, "دعاء التوبة", 13, Color(0xFFC8B8A0))
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<AzkarCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    if (selectedCategory != null) {
        AzkarDetailScreen(
            category = selectedCategory!!,
            onBack = { selectedCategory = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(colors.gold, colors.gold.copy(0.8f))
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
                        "الأذكار والأدعية",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont
                        )
                    )
                    Text(
                        "ذكر الله يطمئن القلب",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = colors.textPrimary.copy(0.7f),
                            fontFamily = AmiriFont
                        )
                    )
                }
            }

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("ابحث عن أذكار...", fontFamily = AmiriFont) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "أيقونة") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.card,
                    unfocusedContainerColor = colors.card,
                    focusedIndicatorColor = colors.gold
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
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("أذكار", fontFamily = AmiriFont) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("أدعية", fontFamily = AmiriFont) }
                )
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> AzkarCategoryGrid(
                        categories = azkarCategories,
                        onSelectCategory = { selectedCategory = it }
                    )
                    1 -> AzkarCategoryGrid(
                        categories = duaCategories,
                        onSelectCategory = { selectedCategory = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AzkarCategoryGrid(
    categories: List<AzkarCategory>,
    onSelectCategory: (AzkarCategory) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        gridItems(categories, key = { it.id }) { category ->
            AzkarCategoryCard(
                category = category,
                onClick = { onSelectCategory(category) }
            )
        }
    }
}

@Composable
fun AzkarCategoryCard(
    category: AzkarCategory,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.card, category.color.copy(0.15f))
                    )
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = category.color.copy(0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "أيقونة",
                    modifier = Modifier.size(28.dp),
                    tint = category.color
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    category.nameAr,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = 0.6f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = category.color,
                    trackColor = colors.borderSubtle
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${category.itemCount} عنصر",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarDetailScreen(
    category: AzkarCategory,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    val zikrItems = remember {
        mutableStateListOf(
            ZikrItem(1, "سبحان الله", 33, 0, "سنة مأثورة"),
            ZikrItem(2, "الحمد لله", 33, 0, "سنة مأثورة"),
            ZikrItem(3, "الله أكبر", 33, 0, "سنة مأثورة"),
            ZikrItem(4, "لا إله إلا الله", 100, 0, "سنة مأثورة"),
            ZikrItem(5, "استغفر الله", 70, 0, "سنة مأثورة"),
            ZikrItem(6, "سبحان الله وبحمده", 100, 0, "صحيح البخاري"),
            ZikrItem(7, "سبحان الله العظيم", 50, 0, "سنة مأثورة"),
            ZikrItem(8, "ما شاء الله لا قوة إلا بالله", 1, 0, "سنة مأثورة"),
            ZikrItem(9, "الحمد لله حمداً كثيراً طيباً مباركاً فيه", 33, 0, "صحيح البخاري"),
            ZikrItem(10, "سبحانك اللهم وبحمدك لا إله إلا أنت", 1, 0, "سنة مأثورة"),
            ZikrItem(11, "لا حول ولا قوة إلا بالله", 100, 0, "سنة مأثورة"),
            ZikrItem(12, "يا حي يا قيوم", 5, 0, "سنة مأثورة")
        )
    }

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    category.nameAr,
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
            actions = {
                IconButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, contentDescription = "إعادة تعيين")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.gold
            )
        )

        // Statistics Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(category.color.copy(0.2f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "الإجمالي",
                    style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                )
                Text(
                    "${zikrItems.size}",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = category.color, fontFamily = AmiriFont)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "المكتمل",
                    style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                )
                Text(
                    "${zikrItems.count { it.completed }}",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.gold, fontFamily = AmiriFont)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "النسبة",
                    style = TextStyle(fontSize = 12.sp, color = colors.textSecondary, fontFamily = AmiriFont)
                )
                Text(
                    "${if (zikrItems.isNotEmpty()) zikrItems.count { it.completed } * 100 / zikrItems.size else 0}%",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = category.color, fontFamily = AmiriFont)
                )
            }
        }

        // Zikr Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(zikrItems, key = { it.id }) { item ->
                ZikrItemCard(
                    item = item,
                    categoryColor = category.color,
                    onCountChanged = { newCount ->
                        val itemIndex = zikrItems.indexOfFirst { it.id == item.id }
                        if (itemIndex >= 0) {
                            if (newCount >= zikrItems[itemIndex].repetitions) {
                                zikrItems[itemIndex] = zikrItems[itemIndex].copy(
                                    currentCount = newCount,
                                    completed = true
                                )
                            } else {
                                zikrItems[itemIndex] = zikrItems[itemIndex].copy(currentCount = newCount)
                            }
                        }
                    }
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة تعيين", fontFamily = AmiriFont) },
            text = { Text("هل تريد إعادة تعيين جميع الأذكار؟", fontFamily = AmiriFont) },
            confirmButton = {
                Button(
                    onClick = {
                        zikrItems.forEachIndexed { index, _ ->
                            zikrItems[index] = zikrItems[index].copy(currentCount = 0, completed = false)
                        }
                        showResetDialog = false
                    }
                ) {
                    Text("نعم", fontFamily = AmiriFont)
                }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) {
                    Text("لا", fontFamily = AmiriFont)
                }
            }
        )
    }
}

@Composable
fun ZikrItemCard(
    item: ZikrItem,
    categoryColor: Color,
    onCountChanged: (Int) -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (item.completed) colors.card.copy(0.5f) else colors.card
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.textAr,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.completed) colors.textSecondary else colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (item.completed) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "أيقونة",
                        tint = categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.source,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            if (item.currentCount < item.repetitions) {
                                onCountChanged(item.currentCount + 1)
                            }
                        }
                ) {
                    Text(
                        "${item.currentCount}/${item.repetitions}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            fontFamily = AmiriFont
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = if (item.repetitions > 0) item.currentCount.toFloat() / item.repetitions else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = categoryColor,
                trackColor = colors.borderSubtle
            )
        }
    }
}
