package com.quranapp.android.ui.screens.more

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign

data class Mosque(
    val id: Int,
    val nameAr: String,
    val distance: Double,
    val address: String,
    val phone: String,
    val prayerTimes: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMosquesScreen(onBack: () -> Unit) {
    val colors = AppDesign.colors
    val mosques = remember {
        listOf(
            Mosque(1, "مسجد الملك عبدالعزيز", 0.8, "شارع الملك فهد، الرياض", "+966 11 4814140", "الفجر: 05:15 | الظهر: 12:30 | العصر: 15:45 | المغرب: 18:15 | العشاء: 19:45"),
            Mosque(2, "المسجد الحرام", 2.5, "أم القرى، الرياض", "+966 11 4614670", "الفجر: 05:20 | الظهر: 12:35 | العصر: 15:50 | المغرب: 18:20 | العشاء: 19:50"),
            Mosque(3, "مسجد الراجحي", 1.2, "شارع الأمير محمد بن عبدالعزيز", "+966 11 4745892", "الفجر: 05:18 | الظهر: 12:32 | العصر: 15:47 | المغرب: 18:17 | العشاء: 19:47"),
            Mosque(4, "مسجد الفيصل", 3.1, "حي الملز، الرياض", "+966 11 4618923", "الفجر: 05:25 | الظهر: 12:40 | العصر: 15:55 | المغرب: 18:25 | العشاء: 19:55"),
            Mosque(5, "مسجد الدعوة والإرشاد", 1.8, "شارع الأمير سلطان، الرياض", "+966 11 4672345", "الفجر: 05:22 | الظهر: 12:37 | العصر: 15:52 | المغرب: 18:22 | العشاء: 19:52"),
            Mosque(6, "مسجد قباء", 2.2, "حي الروضة، الرياض", "+966 11 4856234", "الفجر: 05:20 | الظهر: 12:35 | العصر: 15:50 | المغرب: 18:20 | العشاء: 19:50")
        )
    }

    var viewMode by remember { mutableIntStateOf(0) } // 0 = List, 1 = Map
    var searchQuery by remember { mutableStateOf("") }
    var selectedMosque by remember { mutableStateOf<Mosque?>(null) }

    if (selectedMosque != null) {
        MosqueDetailScreen(
            mosque = selectedMosque!!,
            onBack = { selectedMosque = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        "المساجد القريبة",
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
                    IconButton(onClick = { viewMode = 1 - viewMode }) {
                        Icon(
                            if (viewMode == 0) Icons.Default.Map else Icons.Default.List,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.gold
                )
            )

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("ابحث عن مسجد...", fontFamily = AmiriFont) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "أيقونة") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.card,
                    unfocusedContainerColor = colors.card,
                    focusedIndicatorColor = colors.gold
                )
            )

            // Content
            if (viewMode == 0) {
                // List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mosques, key = { it.id }) { mosque ->
                        MosqueListCard(
                            mosque = mosque,
                            onClick = { selectedMosque = mosque }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            } else {
                // Map View (Placeholder)
                MosqueMapView(mosques)
            }
        }
    }
}

@Composable
fun MosqueListCard(
    mosque: Mosque,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.card, colors.gold.copy(0.1f))
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    mosque.nameAr,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont,
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp),
                    color = colors.gold.copy(0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "أيقونة",
                            modifier = Modifier.size(20.dp),
                            tint = colors.gold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Navigation,
                    contentDescription = "أيقونة",
                    modifier = Modifier.size(16.dp),
                    tint = colors.textSecondary
                )

                Text(
                    "${String.format("%.2f", mosque.distance)} كم",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "أيقونة",
                    modifier = Modifier.size(16.dp),
                    tint = colors.textSecondary
                )

                Text(
                    mosque.address,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.gold.copy(0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "أيقونة", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصل", fontFamily = AmiriFont, fontSize = 11.sp, color = colors.gold)
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.gold
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = "أيقونة", modifier = Modifier.size(14.dp), tint = colors.textPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتجاهات", fontFamily = AmiriFont, fontSize = 11.sp, color = colors.textPrimary)
                }
            }
        }
    }
}

@Composable
fun MosqueMapView(mosques: List<Mosque>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Placeholder for Google Maps
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Map,
                contentDescription = "أيقونة",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "خريطة المساجد القريبة",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontFamily = AmiriFont
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "يتم عرض ${mosques.size} مساجد بالقرب منك",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = AmiriFont
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueDetailScreen(
    mosque: Mosque,
    onBack: () -> Unit
) {
    val colors = AppDesign.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TopAppBar(
            title = { Text(mosque.nameAr, fontFamily = AmiriFont) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "أيقونة")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.gold
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
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
                                    colors = listOf(colors.card, colors.gold.copy(0.15f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${String.format("%.2f", mosque.distance)} كم",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.gold,
                                    fontFamily = AmiriFont
                                )
                            )

                            Text(
                                mosque.nameAr,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.gold.copy(0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "أيقونة",
                                modifier = Modifier.size(18.dp),
                                tint = colors.gold
                            )

                            Text(
                                mosque.address,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont,
                                    textAlign = TextAlign.Right
                                )
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "أوقات الصلاة",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            mosque.prayerTimes,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "رقم الهاتف",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontFamily = AmiriFont
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            mosque.phone,
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = colors.gold,
                                fontFamily = AmiriFont
                            )
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "أيقونة")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اتصل", fontFamily = AmiriFont, color = colors.textPrimary)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.gold.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = "أيقونة", tint = colors.gold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اتجاهات", fontFamily = AmiriFont, color = colors.gold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
