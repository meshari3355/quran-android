package com.quranapp.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Small qibla compass widget for home screen
 * Displays arrow pointing to Qibla with degree text
 */
@Composable
fun MiniQiblaCompass(
    qiblaDegrees: Float = 45f,
    deviceDirection: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val relativeAngle = qiblaDegrees - deviceDirection

    Column(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFAF0E6),
                        Color(0xFFF5E6D3)
                    )
                )
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFFAF0E6)
                        ),
                        radius = 50f
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Compass circle background
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1a237e).copy(alpha = 0.1f),
                                Color(0xFFff6f00).copy(alpha = 0.05f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Cardinal directions text
            Box(
                modifier = Modifier
                    .size(70.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "N",
                    color = Color(0xFF3E2723),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Qibla arrow
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Qibla Direction",
                tint = Color(0xFFD4A574),
                modifier = Modifier
                    .size(28.dp)
                    .rotate(relativeAngle)
            )
        }

        // Degree text
        Text(
            text = "%.0f°".format(relativeAngle),
            color = Color(0xFFD4A574),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "القبلة",
            color = Color(0xFF5D4037),
            fontSize = 10.sp
        )
    }
}

/**
 * Medium Qibla Compass (for dedicated screen or widget)
 */
@Composable
fun MediumQiblaCompass(
    qiblaDegrees: Float = 45f,
    deviceDirection: Float = 0f,
    location: String = "الرياض",
    modifier: Modifier = Modifier
) {
    val relativeAngle = qiblaDegrees - deviceDirection

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF0E6),
                        Color(0xFFF5E6D3)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "بوصلة القبلة",
            color = Color(0xFF3E2723),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFFAF0E6)
                        ),
                        radius = 150f
                    ),
                    shape = CircleShape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Compass circle
            Box(
                modifier = Modifier
                    .size(184.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1a237e).copy(alpha = 0.08f),
                                Color(0xFFff6f00).copy(alpha = 0.04f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Direction markers
            CompassMarkers(
                modifier = Modifier.size(180.dp)
            )

            // Qibla arrow
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Qibla Direction",
                tint = Color(0xFFD4A574),
                modifier = Modifier
                    .size(56.dp)
                    .rotate(relativeAngle)
            )
        }

        // Angle information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFD4A574).copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "زاوية القبلة",
                    color = Color(0xFF795548),
                    fontSize = 11.sp
                )
                Text(
                    text = "%.0f°".format(qiblaDegrees),
                    color = Color(0xFFD4A574),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color(0xFFD4A574).copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "اتجاه جهازك",
                    color = Color(0xFF795548),
                    fontSize = 11.sp
                )
                Text(
                    text = "%.0f°".format(deviceDirection),
                    color = Color(0xFFD4A574),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = location,
            color = Color(0xFF5D4037),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CompassMarkers(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        // North
        Text(
            text = "ش",
            color = Color(0xFF3E2723),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = 8.dp)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        // South
        Text(
            text = "ج",
            color = Color(0xFF3E2723),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = (-8).dp)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        // West
        Text(
            text = "غ",
            color = Color(0xFF3E2723),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = 8.dp)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        // East
        Text(
            text = "ش",
            color = Color(0xFF3E2723),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = (-8).dp)
        )
    }
}
