package com.quranapp.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val QuranShapes = Shapes(
    // Extra Small - for small elements like chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),

    // Small - for small cards, small dialogs
    small = RoundedCornerShape(8.dp),

    // Medium - for standard cards, buttons, and general elements
    medium = RoundedCornerShape(12.dp),

    // Large - for larger containers, expanded bottom sheets
    large = RoundedCornerShape(16.dp),

    // Extra Large - for fullscreen elements
    extraLarge = RoundedCornerShape(28.dp)
)

// Additional shape definitions for custom components
val CardCornerRadius = 12.dp
val ButtonCornerRadius = 10.dp
val DialogCornerRadius = 16.dp
val BottomSheetCornerRadius = 20.dp
val FABCornerRadius = 16.dp

val CardShape = RoundedCornerShape(CardCornerRadius)
val ButtonShape = RoundedCornerShape(ButtonCornerRadius)
val DialogShape = RoundedCornerShape(DialogCornerRadius)
val BottomSheetShape = RoundedCornerShape(BottomSheetCornerRadius)
val FABShape = RoundedCornerShape(FABCornerRadius)
