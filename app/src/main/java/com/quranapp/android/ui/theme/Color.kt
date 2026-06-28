package com.quranapp.android.ui.theme

import androidx.compose.ui.graphics.Color

// Light Mode Colors
object LightColors {
    val background = Color(0xFFF8EFE0)
    val card = Color(0xFFFFFAF0)
    val text = Color(0xFF1F1614)
    val secondaryText = Color(0xFF7C625E)
    val border = Color(0xFFB8840A).copy(alpha = 0.25f)
    val surface = Color(0xFFFFFAF0)
    val surfaceVariant = Color(0xFFF0E8DC)
}

// Dark Mode Colors
object DarkColors {
    val background = Color(0xFF171717)
    val card = Color(0xFF242424)
    val text = Color(0xFFF3EDDF)
    val secondaryText = Color(0xFFA69F96)
    val border = Color(0xFFD9B356).copy(alpha = 0.25f)
    val surface = Color(0xFF242424)
    val surfaceVariant = Color(0xFF3A3A3A)
}

// Accent Color Palettes
object AccentPalettes {
    // Gold Palette
    data class GoldPalette(
        val light: Color = Color(0xFFB8840A),
        val dark: Color = Color(0xFFD9B356),
        val lightContainer: Color = Color(0xFFFFDDB3),
        val darkContainer: Color = Color(0xFF9A6B00)
    )

    // Teal Palette
    data class TealPalette(
        val light: Color = Color(0xFF0D9488),
        val dark: Color = Color(0xFF2DD4BF),
        val lightContainer: Color = Color(0xFF99F1E9),
        val darkContainer: Color = Color(0xFF086B65)
    )

    // Indigo Palette
    data class IndigoPalette(
        val light: Color = Color(0xFF4F46E5),
        val dark: Color = Color(0xFF818CF8),
        val lightContainer: Color = Color(0xFFE0E7FF),
        val darkContainer: Color = Color(0xFF312E81)
    )

    // Emerald Palette
    data class EmeraldPalette(
        val light: Color = Color(0xFF059669),
        val dark: Color = Color(0xFF34D399),
        val lightContainer: Color = Color(0xFFA7F3D0),
        val darkContainer: Color = Color(0xFF04663A)
    )

    // Rose Palette
    data class RosePalette(
        val light: Color = Color(0xFFE11D48),
        val dark: Color = Color(0xFFFB7185),
        val lightContainer: Color = Color(0xFFFFDAE8),
        val darkContainer: Color = Color(0xFF880E38)
    )

    // Purple Palette
    data class PurplePalette(
        val light: Color = Color(0xFF7C3AED),
        val dark: Color = Color(0xFFA78BFA),
        val lightContainer: Color = Color(0xFFEDE9FE),
        val darkContainer: Color = Color(0xFF5B21B6)
    )

    fun getGold() = GoldPalette()
    fun getTeal() = TealPalette()
    fun getIndigo() = IndigoPalette()
    fun getEmerald() = EmeraldPalette()
    fun getRose() = RosePalette()
    fun getPurple() = PurplePalette()
}

// Accent color enum for easy reference
enum class AccentColor {
    GOLD,
    TEAL,
    INDIGO,
    EMERALD,
    ROSE,
    PURPLE
}

// Helper function to get accent palette by enum
fun getAccentPalette(accentColor: AccentColor): Any {
    return when (accentColor) {
        AccentColor.GOLD -> AccentPalettes.getGold()
        AccentColor.TEAL -> AccentPalettes.getTeal()
        AccentColor.INDIGO -> AccentPalettes.getIndigo()
        AccentColor.EMERALD -> AccentPalettes.getEmerald()
        AccentColor.ROSE -> AccentPalettes.getRose()
        AccentColor.PURPLE -> AccentPalettes.getPurple()
    }
}

// Helper function to get primary color based on accent and theme
fun getAccentColorForTheme(accentColor: AccentColor, isDark: Boolean): Color {
    return when (accentColor) {
        AccentColor.GOLD -> if (isDark) Color(0xFFD9B356) else Color(0xFFB8840A)
        AccentColor.TEAL -> if (isDark) Color(0xFF2DD4BF) else Color(0xFF0D9488)
        AccentColor.INDIGO -> if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5)
        AccentColor.EMERALD -> if (isDark) Color(0xFF34D399) else Color(0xFF059669)
        AccentColor.ROSE -> if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48)
        AccentColor.PURPLE -> if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED)
    }
}

// Helper function to get container color based on accent and theme
fun getAccentContainerColorForTheme(accentColor: AccentColor, isDark: Boolean): Color {
    return when (accentColor) {
        AccentColor.GOLD -> if (isDark) Color(0xFF9A6B00) else Color(0xFFFFDDB3)
        AccentColor.TEAL -> if (isDark) Color(0xFF086B65) else Color(0xFF99F1E9)
        AccentColor.INDIGO -> if (isDark) Color(0xFF312E81) else Color(0xFFE0E7FF)
        AccentColor.EMERALD -> if (isDark) Color(0xFF04663A) else Color(0xFFA7F3D0)
        AccentColor.ROSE -> if (isDark) Color(0xFF880E38) else Color(0xFFFFDAE8)
        AccentColor.PURPLE -> if (isDark) Color(0xFF5B21B6) else Color(0xFFEDE9FE)
    }
}
