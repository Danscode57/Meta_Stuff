package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuestDarkColorScheme = darkColorScheme(
    primary = QuestCyan,
    onPrimary = Color.Black,
    primaryContainer = QuestCyanDark,
    onPrimaryContainer = Color.White,
    secondary = QuestPurple,
    onSecondary = Color.White,
    secondaryContainer = QuestPurpleLight,
    onSecondaryContainer = Color.Black,
    tertiary = PassthroughEmerald,
    onTertiary = Color.Black,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder
)

@Composable
fun Quest3Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuestDarkColorScheme,
        typography = Typography,
        content = content
    )
}
