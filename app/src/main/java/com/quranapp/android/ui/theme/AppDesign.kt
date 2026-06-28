package com.quranapp.android.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Unified Design System for Quran Android App
 *
 * Matches the iOS app design language exactly:
 * - Warm beige/cream backgrounds
 * - Gold accent with configurable alternatives
 * - Consistent spacing, typography, and component styles
 * - Full RTL support
 * - Proper dark mode
 *
 * Usage: AppDesign.colors.gold, AppDesign.spacing.md, etc.
 * All screens MUST use this instead of hardcoded Color() values.
 */

// ========================================
// SPACING SYSTEM (matches iOS point system)
// ========================================
object Spacing {
    val xxxs: Dp = 2.dp
    val xxs: Dp = 4.dp
    val xs: Dp = 6.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 40.dp
    val massive: Dp = 48.dp

    // Specific semantic spacing
    val screenHorizontal: Dp = 16.dp
    val screenVertical: Dp = 8.dp
    val cardPadding: Dp = 16.dp
    val cardGap: Dp = 12.dp
    val sectionGap: Dp = 20.dp
    val listItemGap: Dp = 0.dp
    val iconTextGap: Dp = 8.dp
    val inlineGap: Dp = 6.dp
}

// ========================================
// CORNER RADIUS (matches iOS system)
// ========================================
object Radius {
    val card = RoundedCornerShape(16.dp)
    val cardLarge = RoundedCornerShape(20.dp)
    val input = RoundedCornerShape(12.dp)
    val button = RoundedCornerShape(12.dp)
    val pill = RoundedCornerShape(20.dp)
    val badge = RoundedCornerShape(8.dp)
    val small = RoundedCornerShape(6.dp)
    val circle = CircleShape
}

// ========================================
// ELEVATION
// ========================================
object Elevation {
    val none: Dp = 0.dp
    val subtle: Dp = 1.dp
    val card: Dp = 2.dp
    val raised: Dp = 4.dp
    val floating: Dp = 8.dp
}

// ========================================
// ICON SIZES
// ========================================
object IconSize {
    val tiny: Dp = 14.dp
    val small: Dp = 18.dp
    val medium: Dp = 24.dp
    val large: Dp = 28.dp
    val xlarge: Dp = 32.dp
    val huge: Dp = 40.dp
    val avatar: Dp = 44.dp
    val hero: Dp = 52.dp
}

// ========================================
// SEMANTIC COLORS (light/dark aware)
// ========================================
data class AppColors(
    // Backgrounds
    val background: Color,
    val card: Color,
    val surface: Color,
    val surfaceVariant: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    // Accent (gold by default)
    val gold: Color,
    val goldLight: Color,
    val goldContainer: Color,
    val goldOnContainer: Color,

    // Borders
    val border: Color,
    val borderSubtle: Color,
    val divider: Color,

    // Semantic
    val success: Color,
    val error: Color,
    val warning: Color,
    val info: Color,

    // Special
    val prayerGreen: Color,
    val nightSky: Color,
    val nightSkyCard: Color,
    val moonLight: Color,

    // Interactive
    val ripple: Color,
    val disabled: Color,
    val disabledText: Color,

    val isDark: Boolean
)

val LightAppColors = AppColors(
    // Backgrounds — warm cream (matches iOS)
    background = Color(0xFFFAF6F1),
    card = Color(0xFFFFFBF5),
    surface = Color(0xFFFFFAF0),
    surfaceVariant = Color(0xFFF5EDE0),

    // Text
    textPrimary = Color(0xFF1F1710),
    textSecondary = Color(0xFF73614C),
    textTertiary = Color(0xFFA89880),

    // Gold accent
    gold = Color(0xFFB8840A),
    goldLight = Color(0xFFD4AF37),
    goldContainer = Color(0xFFFFF3DB),
    goldOnContainer = Color(0xFF5C4205),

    // Borders
    border = Color(0xFFE5DDD0),
    borderSubtle = Color(0xFFF0EAE0),
    divider = Color(0xFFEDE6DA),

    // Semantic
    success = Color(0xFF2E7D32),
    error = Color(0xFFC62828),
    warning = Color(0xFFE65100),
    info = Color(0xFF1565C0),

    // Special
    prayerGreen = Color(0xFF4CAF50),
    nightSky = Color(0xFF1A1A2E),
    nightSkyCard = Color(0xFF16213E),
    moonLight = Color(0xFF42A5F5),

    // Interactive
    ripple = Color(0xFFB8840A).copy(alpha = 0.12f),
    disabled = Color(0xFFE0D8CC),
    disabledText = Color(0xFFBBB0A0),

    isDark = false
)

val DarkAppColors = AppColors(
    // Backgrounds
    background = Color(0xFF121212),
    card = Color(0xFF1E1E1E),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),

    // Text
    textPrimary = Color(0xFFF3EDDF),
    textSecondary = Color(0xFFA69F96),
    textTertiary = Color(0xFF7A746C),

    // Gold accent
    gold = Color(0xFFD9B356),
    goldLight = Color(0xFFE6C97A),
    goldContainer = Color(0xFF3D3010),
    goldOnContainer = Color(0xFFFFE8A0),

    // Borders
    border = Color(0xFF3A3530),
    borderSubtle = Color(0xFF2E2A26),
    divider = Color(0xFF333030),

    // Semantic
    success = Color(0xFF66BB6A),
    error = Color(0xFFEF5350),
    warning = Color(0xFFFF9800),
    info = Color(0xFF42A5F5),

    // Special
    prayerGreen = Color(0xFF66BB6A),
    nightSky = Color(0xFF0D0D1A),
    nightSkyCard = Color(0xFF10101F),
    moonLight = Color(0xFF64B5F6),

    // Interactive
    ripple = Color(0xFFD9B356).copy(alpha = 0.12f),
    disabled = Color(0xFF3A3630),
    disabledText = Color(0xFF5C5650),

    isDark = true
)

// ========================================
// MAIN APP DESIGN ENTRY POINT
// ========================================
object AppDesign {

    val spacing = Spacing
    val radius = Radius
    val elevation = Elevation
    val iconSize = IconSize

    /**
     * Get current colors based on theme.
     * Usage: val colors = AppDesign.colors (inside @Composable)
     */
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
            DarkAppColors
        } else {
            LightAppColors
        }

    // Helper to get border stroke for cards
    @Composable
    fun cardBorder(): BorderStroke = BorderStroke(1.dp, colors.border)

    @Composable
    fun subtleBorder(): BorderStroke = BorderStroke(0.5.dp, colors.borderSubtle)

    @Composable
    fun goldBorder(): BorderStroke = BorderStroke(1.5.dp, colors.gold.copy(alpha = 0.3f))
}

// Extension to compute luminance for dark detection
private fun Color.luminance(): Float {
    val r = red * 0.2126f
    val g = green * 0.7152f
    val b = blue * 0.0722f
    return r + g + b
}
