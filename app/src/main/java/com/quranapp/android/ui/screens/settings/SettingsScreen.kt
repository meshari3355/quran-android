package com.quranapp.android.ui.screens.settings

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import com.quranapp.android.ui.theme.AmiriFont
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.theme.ThemeMode
import com.quranapp.android.ui.theme.AccentColor
import com.quranapp.android.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val uiState by viewModel.uiState.collectAsState()
        val colors = AppDesign.colors

        val accentColors = mapOf(
            AccentColor.PURPLE to Color.Magenta.copy(0.8f),
            AccentColor.ROSE to Color(0xFFEC4899),
            AccentColor.EMERALD to Color(0xFF10B981),
            AccentColor.INDIGO to Color(0xFF4F46E5),
            AccentColor.TEAL to Color(0xFF14B8A6),
            AccentColor.GOLD to colors.gold
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        "الإعدادات",
                        style = TextStyle(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.gold,
                            fontFamily = AmiriFont
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )

            // Settings List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // MARK: Appearance Section
                item {
                    SettingsSectionLabel("المظهر والتخصيص")
                }

                item {
                    SettingsSectionCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Display mode picker
                            SettingRowHeader("وضع العرض", Icons.Default.DarkMode)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "فاتح" to ThemeMode.LIGHT,
                                    "غامق" to ThemeMode.DARK,
                                    "النظام" to ThemeMode.SYSTEM
                                ).forEach { (label, value) ->
                                    Button(
                                        onClick = { viewModel.setThemeMode(value) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.themeMode == value) colors.gold else colors.card
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            label,
                                            color = if (uiState.themeMode == value) colors.textPrimary else colors.textSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = AmiriFont
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = colors.divider, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Accent color picker
                            SettingRowHeader("لون التطبيق", Icons.Default.Palette)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                accentColors.forEach { (colorEnum, color) ->
                                    Surface(
                                        modifier = Modifier
                                            .size(if (uiState.accentColor == colorEnum) 52.dp else 48.dp)
                                            .clip(CircleShape)
                                            .clickable { viewModel.setAccentColor(colorEnum) },
                                        color = color
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            if (uiState.accentColor == colorEnum) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "تم",
                                                    modifier = Modifier.size(24.dp),
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = colors.divider, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Language
                            SettingRowHeader("اللغة", Icons.Default.Language)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("العربية" to "ar", "English" to "en").forEach { (label, value) ->
                                    Button(
                                        onClick = { viewModel.setLanguage(value) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.language == value) colors.gold else colors.card
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            label,
                                            color = if (uiState.language == value) colors.textPrimary else colors.textSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = AmiriFont
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // MARK: Notifications Section
                item {
                    SettingsSectionLabel("الإشعارات")
                }

                item {
                    SettingsSectionCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingToggleItem(
                                icon = "bell.fill",
                                title = "أوقات الصلاة",
                                subtitle = if (uiState.enablePrayerNotifications) "مفعّل" else "معطّل",
                                value = uiState.enablePrayerNotifications,
                                onValueChange = { viewModel.setPrayerNotificationsEnabled(it) }
                            )

                            Divider(modifier = Modifier.padding(start = 66.dp), color = colors.divider)

                            SettingToggleItem(
                                icon = "heart.fill",
                                title = "تذكيرات الأذكار",
                                subtitle = "الصباح • المساء • النوم • بعد الصلوات",
                                value = uiState.enableAzkarReminders,
                                onValueChange = { viewModel.setAzkarRemindersEnabled(it) }
                            )
                        }
                    }
                }

                // MARK: Display Section
                item {
                    SettingsSectionLabel("العرض")
                }

                item {
                    SettingsSectionCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "حجم الخط (${String.format("%.1f", uiState.fontSize)}x)",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontFamily = AmiriFont
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Slider(
                                value = uiState.fontSize,
                                onValueChange = { viewModel.setFontSize(it) },
                                modifier = Modifier.fillMaxWidth(),
                                valueRange = 0.8f..1.2f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.gold,
                                    activeTrackColor = colors.gold,
                                    inactiveTrackColor = colors.divider
                                )
                            )

                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = colors.divider)

                            SettingToggleItem(
                                icon = "moon.fill",
                                title = "وضع الليل",
                                subtitle = "تقليل إجهاد العين",
                                value = uiState.enableNightMode,
                                onValueChange = { viewModel.setNightModeEnabled(it) }
                            )
                        }
                    }
                }

                // MARK: About App Section
                item {
                    SettingsSectionLabel("معلومات التطبيق")
                }

                item {
                    SettingsSectionCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingsOptionItem(
                                title = "النسخة",
                                value = uiState.appVersion,
                                icon = Icons.Default.Info,
                                onClick = { }
                            )

                            Divider(modifier = Modifier.padding(start = 66.dp), color = colors.divider)

                            SettingsOptionItem(
                                title = "حول التطبيق",
                                value = "عن القرآن الكريم",
                                icon = Icons.Default.LibraryBooks,
                                onClick = { }
                            )

                            Divider(modifier = Modifier.padding(start = 66.dp), color = colors.divider)

                            SettingsOptionItem(
                                title = "قيّم التطبيق",
                                value = "شارك رأيك معنا",
                                icon = Icons.Default.Star,
                                onClick = { }
                            )

                            Divider(modifier = Modifier.padding(start = 66.dp), color = colors.divider)

                            SettingsOptionItem(
                                title = "شارك التطبيق",
                                value = "أخبر أصدقاءك",
                                icon = Icons.Default.Share,
                                onClick = { }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "القرآن الكريم • الإصدار ${uiState.appVersion}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            fontFamily = AmiriFont
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSectionLabel(title: String) {
    val colors = AppDesign.colors
    Text(
        title,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            fontFamily = AmiriFont
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsSectionCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppDesign.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun SettingRowHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val colors = AppDesign.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(18.dp),
            tint = colors.gold
        )
        Text(
            title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                fontFamily = AmiriFont
            )
        )
    }
}

@Composable
fun SettingToggleItem(
    icon: String,
    title: String,
    subtitle: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    val colors = AppDesign.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.goldContainer)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = title,
                    modifier = Modifier.size(17.dp),
                    tint = colors.gold
                )
            }

            Column {
                Text(
                    title,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )
                )
                Text(
                    subtitle,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }
        }

        Switch(
            checked = value,
            onCheckedChange = onValueChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.gold,
                checkedTrackColor = colors.gold.copy(0.5f)
            )
        )
    }
}

@Composable
fun SettingsOptionItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val colors = AppDesign.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.goldContainer)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(17.dp),
                    tint = colors.gold
                )
            }

            Column {
                Text(
                    title,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        fontFamily = AmiriFont
                    )
                )
                Text(
                    value,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = AmiriFont
                    )
                )
            }
        }

        Icon(
            if (true) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
            contentDescription = "الانتقال",
            modifier = Modifier.size(24.dp),
            tint = colors.textSecondary
        )
    }
}
