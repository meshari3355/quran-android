package com.quranapp.android.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============ QuranLoadingIndicator Component ============
/**
 * Lightweight static loading mark that avoids Material3 progress animation
 * compatibility issues on older Compose runtime combinations.
 */
@Composable
fun QuranLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFD4A574),
    strokeWidth: Dp = 3.dp,
    progress: Float? = null,
    trackColor: Color = color.copy(alpha = 0.28f)
) {
    val dotAlpha = progress?.coerceIn(0f, 1f)?.coerceAtLeast(0.35f) ?: 1f
    Box(
        modifier = modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .border(strokeWidth, trackColor, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = dotAlpha))
        )
    }
}

// ============ QuranCard Component ============
/**
 * Themed card with warm beige/dark surface for Quran content
 */
@Composable
fun QuranCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFAF0E6),
    borderColor: Color = Color(0xFFD4A574).copy(alpha = 0.3f),
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        color = backgroundColor,
        shadowElevation = elevation,
        shape = RoundedCornerShape(12.dp)
    ) {
        content()
    }
}

// ============ GoldButton Component ============
/**
 * Gold gradient button with customizable styling
 */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD4A574),
            disabledContainerColor = Color(0xFFD4A574).copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            QuranLoadingIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "أيقونة",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
        }
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

// ============ SurahBadge Component ============
/**
 * Decorated surah number in star/circle shape
 */
@Composable
fun SurahBadge(
    number: Int,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD4A574),
                        Color(0xFFC19A6B)
                    )
                ),
                shape = CircleShape
            )
            .border(2.dp, Color(0xFFFFF8DC), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isBookmarked) {
            Icon(
                imageVector = Icons.Default.BookmarkAdded,
                contentDescription = "Bookmarked",
                tint = Color(0xFFFFD700),
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset((-4).dp, (-4).dp)
            )
        }
    }
}

// ============ SearchBar Component ============
/**
 * Themed search bar with Arabic placeholder
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "بحث...",
    onSearch: (String) -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text(placeholder, color = Color(0xFF9E8B7B)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFD4A574)
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color(0xFFD4A574),
                    modifier = Modifier
                        .clickable { onValueChange("") }
                        .padding(8.dp)
                )
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAF0E6),
            unfocusedContainerColor = Color(0xFFFAF0E6),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(0xFF3E2723),
            unfocusedTextColor = Color(0xFF5D4037)
        ),
        singleLine = true
    )
}

// ============ FilterChip Component ============
/**
 * Category filter chip
 */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelectedChange(!selected) }
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFFD4A574) else Color(0xFFD4A574).copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (selected) Color(0xFFD4A574).copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) Color(0xFFD4A574) else Color(0xFF5D4037),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ============ LoadingShimmer Component ============
/**
 * Shimmer loading placeholder
 */
@Composable
fun LoadingShimmer(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 16.dp
) {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200)
        ),
        label = "shimmer_translate"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = androidx.compose.ui.geometry.Offset(translateAnim - 300, 0f),
                    end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
                ),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

// ============ ErrorRetry Component ============
/**
 * Error state with retry button
 */
@Composable
fun ErrorRetry(
    message: String = "حدث خطأ ما",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = Color(0xFFE53935),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF3E2723),
            textAlign = TextAlign.Center
        )
        GoldButton(
            text = "إعادة المحاولة",
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

// ============ SectionHeader Component ============
/**
 * Section title with optional "show all" link
 */
@Composable
fun SectionHeader(
    title: String,
    showAll: Boolean = true,
    onShowAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )
        if (showAll && onShowAllClick != null) {
            Text(
                text = "عرض الكل",
                fontSize = 12.sp,
                color = Color(0xFFD4A574),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onShowAllClick)
            )
        }
    }
}

// ============ CategoryCard Component ============
/**
 * Grid card with icon, title, subtitle
 */
@Composable
fun CategoryCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    QuranCard(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFD4A574),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3E2723),
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF795548),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============ IslamicDivider Component ============
/**
 * Subtle gold divider line with Islamic pattern
 */
@Composable
fun IslamicDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = Color(0xFFD4A574),
    alpha: Float = 0.3f
) {
    Divider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        thickness = thickness,
        color = color.copy(alpha = alpha)
    )
}

// ============ ProgressCircle Component ============
/**
 * Circular progress indicator with percentage
 */
@Composable
fun ProgressCircle(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 4.dp,
    label: String? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        QuranLoadingIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            color = Color(0xFFD4A574)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
            if (label != null) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF795548)
                )
            }
        }
    }
}

// ============ CountBadge Component ============
/**
 * Small badge showing count
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .background(
                color = Color(0xFFE53935),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > maxCount) "$maxCount+" else count.toString(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============ QuranText Component ============
/**
 * Styled Quranic Arabic text
 */
@Composable
fun QuranText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = Color(0xFF3E2723)
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = FontWeight.Normal,
        textAlign = textAlign,
        color = color,
        lineHeight = (fontSize.value * 1.8).sp
    )
}

// ============ Spacer Helper ============
/**
 * Quick spacer for consistent spacing
 */
@Composable
fun HorizontalSpacer(width: Dp = 8.dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun VerticalSpacer(height: Dp = 8.dp) {
    Spacer(modifier = Modifier.height(height))
}
