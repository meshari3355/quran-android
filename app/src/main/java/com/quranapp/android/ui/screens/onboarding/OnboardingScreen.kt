package com.quranapp.android.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.*
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
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.AmiriFont

data class OnboardingPage(
    val titleAr: String,
    val descriptionAr: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val backgroundColor: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onCompleted: () -> Unit
) {
    val colors = AppDesign.colors
    val pages = listOf(
        OnboardingPage(
            "تطبيق القرآن الكريم",
            "اقرأ القرآن الكريم بيسر وسهولة\nمع تفسيرات وترجمات معتمدة",
            Icons.Filled.MenuBook,
            Color(0xFFE3B4A2)
        ),
        OnboardingPage(
            "ميزات القرآن",
            "اقرأ السور كاملة مع التشكيل\nواستمع لأفضل المقرئين\nشارك الآيات مع أصدقائك",
            Icons.Default.Book,
            colors.gold
        ),
        OnboardingPage(
            "أوقات الصلاة",
            "احصل على أوقات الصلاة بدقة\nتنبيهات للصلوات المفروضة\nأذان جميل وصوت عالي الجودة",
            Icons.Default.AccessTime,
            Color(0xFFD4958D)
        ),
        OnboardingPage(
            "أدوات إسلامية",
            "تقويم هجري وتحويل التواريخ\nحاسبة الزكاة وموقع الجيبة\nأحاديث نبوية وفتاوى إسلامية",
            Icons.Default.Build,
            Color(0xFFB8956F)
        ),
        OnboardingPage(
            "الأذكار والأدعية",
            "أذكار يومية وأدعية مأثورة\nعداد تسبيح رقمي\nتذكيرات يومية للأذكار",
            Icons.Default.Favorite,
            Color(0xFFE8C9B8)
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }
    var showPermissions by remember { mutableStateOf(false) }

    if (showPermissions) {
        PermissionsPage(onCompleted = onCompleted)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Page Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() with
                        slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "onboarding"
                ) { page ->
                    OnboardingPageContent(pages[page])
                }
            }

            // Page Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    Surface(
                        modifier = Modifier
                            .size(if (currentPage == index) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .clickable { currentPage = index },
                        color = if (currentPage == index) colors.gold else colors.gold.copy(0.3f)
                    ) {}

                    if (index < pages.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentPage > 0) {
                    Button(
                        onClick = { currentPage-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.card
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("السابق", color = colors.textPrimary)
                    }
                } else {
                    Button(
                        onClick = { currentPage = pages.size },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.card
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تخطي", color = colors.textPrimary)
                    }
                }

                Button(
                    onClick = {
                        if (currentPage == pages.size - 1) {
                            showPermissions = true
                        } else {
                            currentPage++
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.gold
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (currentPage == pages.size - 1) "ابدأ" else "التالي",
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    val colors = AppDesign.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Decorative Pattern
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 20.dp)
        ) {
            // Draw Islamic geometric pattern
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2.5f

            drawCircle(
                color = page.backgroundColor.copy(0.2f),
                radius = radius
            )

            drawCircle(
                color = page.backgroundColor.copy(0.1f),
                radius = radius * 0.7f
            )
        }

        // Icon
        Surface(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            color = page.backgroundColor.copy(0.25f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = "أيقونة",
                    modifier = Modifier.size(44.dp),
                    tint = page.backgroundColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            page.titleAr,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                fontFamily = AmiriFont
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            page.descriptionAr,
            style = TextStyle(
                fontSize = 16.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                fontFamily = AmiriFont
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PermissionsPage(onCompleted: () -> Unit) {
    val colors = AppDesign.colors
    var locationGranted by remember { mutableStateOf(false) }
    var notificationsGranted by remember { mutableStateOf(false) }

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
                        colors = listOf(colors.gold, colors.gold.copy(0.8f))
                    )
                )
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "الأذونات المطلوبة",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "لتحسين تجربتك في التطبيق",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = colors.textPrimary.copy(0.7f),
                        fontFamily = AmiriFont
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Permissions Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PermissionCard(
                title = "الموقع الجغرافي",
                description = "للحصول على أوقات صلاة دقيقة\nومساجد قريبة منك",
                icon = Icons.Default.LocationOn,
                granted = locationGranted,
                onGrant = { locationGranted = true }
            )

            PermissionCard(
                title = "الإشعارات",
                description = "لتنبيهات أوقات الصلاة والأذكار\nوتذكيرات القراءة",
                icon = Icons.Default.Notifications,
                granted = notificationsGranted,
                onGrant = { notificationsGranted = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Complete Button
        Button(
            onClick = onCompleted,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.gold
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "انتهيت من الإعدادات",
                fontSize = 16.sp,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = AmiriFont
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Skip Text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onCompleted)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "تخطي هذه الخطوة",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = colors.gold,
                    fontFamily = AmiriFont
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    granted: Boolean,
    onGrant: () -> Unit
) {
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
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(colors.card, colors.gold.copy(0.1f))
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = colors.goldContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "أيقونة",
                            modifier = Modifier.size(24.dp),
                            tint = colors.gold
                        )
                    }
                }

                Column {
                    Text(
                        title,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = AmiriFont
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        description,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 16.sp,
                            fontFamily = AmiriFont
                        )
                    )
                }
            }

            Button(
                onClick = onGrant,
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (granted) colors.gold else colors.gold.copy(0.3f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                if (granted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "أيقونة",
                        modifier = Modifier.size(16.dp),
                        tint = colors.textPrimary
                    )
                } else {
                    Text(
                        "سماح",
                        fontSize = 11.sp,
                        color = colors.gold,
                        fontFamily = AmiriFont
                    )
                }
            }
        }
    }
}
