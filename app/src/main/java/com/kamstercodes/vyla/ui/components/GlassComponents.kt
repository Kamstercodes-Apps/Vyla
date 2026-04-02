package com.kamstercodes.vyla.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kamstercodes.vyla.ui.theme.GlassColors

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentAlignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.Center,
    borderWidth: Dp = 1.dp,
    transparency: Float = 0.4f,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    val baseColor = if (isDark) Color.Black else Color.White
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.4f)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = transparency),
                        baseColor.copy(alpha = transparency * 0.6f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.Transparent, borderColor.copy(alpha = 0.1f))
                ),
                shape = shape
            )
            .drawWithContent {
                drawContent()
            },
        contentAlignment = contentAlignment,
        content = content
    )
}

fun Modifier.iosGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    transparency: Float = 0.3f
) = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = transparency),
                Color.White.copy(alpha = transparency * 0.5f)
            )
        )
    )
    .border(
        width = 0.5.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.Transparent,
                Color.White.copy(alpha = 0.1f)
            )
        ),
        shape = shape
    )
