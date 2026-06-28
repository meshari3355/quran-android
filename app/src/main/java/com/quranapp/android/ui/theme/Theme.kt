package com.quranapp.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Theme Mode Enum
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// Data class for accent color configuration
data class AccentColorConfig(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color
)

// CompositionLocal for accent color
val LocalAccentColor = compositionLocalOf<AccentColorConfig> {
    error("AccentColorConfig not provided")
}

fun createAccentColorConfig(accentColor: AccentColor, isDark: Boolean): AccentColorConfig {
    val primaryColor = getAccentColorForTheme(accentColor, isDark)
    val containerColor = getAccentContainerColorForTheme(accentColor, isDark)

    return AccentColorConfig(
        primary = primaryColor,
        onPrimary = if (isDark) Color.Black else Color.White,
        primaryContainer = containerColor,
        onPrimaryContainer = if (isDark) Color.White else primaryColor,
        secondary = primaryColor.copy(alpha = 0.8f),
        onSecondary = if (isDark) Color.Black else Color.White,
        secondaryContainer = containerColor.copy(alpha = 0.3f),
        onSecondaryContainer = if (isDark) Color.White else Color.Black,
        tertiary = primaryColor.copy(alpha = 0.6f),
        onTertiary = if (isDark) Color.Black else Color.White,
        tertiaryContainer = containerColor.copy(alpha = 0.15f),
        onTertiaryContainer = if (isDark) Color.White else Color.Black
    )
}

@Composable
private fun createLightColorScheme(
    accentConfig: AccentColorConfig,
    isDynamic: Boolean
): ColorScheme {
    val context = LocalContext.current
    val baseLightScheme = if (isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        lightColorScheme()
    }

    return baseLightScheme.copy(
        primary = accentConfig.primary,
        onPrimary = accentConfig.onPrimary,
        primaryContainer = accentConfig.primaryContainer,
        onPrimaryContainer = accentConfig.onPrimaryContainer,
        secondary = accentConfig.secondary,
        onSecondary = accentConfig.onSecondary,
        secondaryContainer = accentConfig.secondaryContainer,
        onSecondaryContainer = accentConfig.onSecondaryContainer,
        tertiary = accentConfig.tertiary,
        onTertiary = accentConfig.onTertiary,
        tertiaryContainer = accentConfig.tertiaryContainer,
        onTertiaryContainer = accentConfig.onTertiaryContainer,
        background = LightColors.background,
        onBackground = LightColors.text,
        surface = LightColors.surface,
        onSurface = LightColors.text,
        surfaceVariant = LightColors.surfaceVariant,
        onSurfaceVariant = LightColors.secondaryText,
        outline = LightColors.border
    )
}

@Composable
private fun createDarkColorScheme(
    accentConfig: AccentColorConfig,
    isDynamic: Boolean
): ColorScheme {
    val context = LocalContext.current
    val baseDarkScheme = if (isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        darkColorScheme()
    }

    return baseDarkScheme.copy(
        primary = accentConfig.primary,
        onPrimary = accentConfig.onPrimary,
        primaryContainer = accentConfig.primaryContainer,
        onPrimaryContainer = accentConfig.onPrimaryContainer,
        secondary = accentConfig.secondary,
        onSecondary = accentConfig.onSecondary,
        secondaryContainer = accentConfig.secondaryContainer,
        onSecondaryContainer = accentConfig.onSecondaryContainer,
        tertiary = accentConfig.tertiary,
        onTertiary = accentConfig.onTertiary,
        tertiaryContainer = accentConfig.tertiaryContainer,
        onTertiaryContainer = accentConfig.onTertiaryContainer,
        background = DarkColors.background,
        onBackground = DarkColors.text,
        surface = DarkColors.surface,
        onSurface = DarkColors.text,
        surfaceVariant = DarkColors.surfaceVariant,
        onSurfaceVariant = DarkColors.secondaryText,
        outline = DarkColors.border
    )
}

@Composable
fun QuranTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.GOLD,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val accentConfig = createAccentColorConfig(accentColor, isDark)
    val isDynamicAllowed = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = if (isDark) {
        createDarkColorScheme(accentConfig, isDynamicAllowed)
    } else {
        createLightColorScheme(accentConfig, isDynamicAllowed)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QuranTypography,
        shapes = QuranShapes
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAccentColor provides accentConfig,
            content = content
        )
    }
}
