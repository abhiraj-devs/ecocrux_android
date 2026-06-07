package com.example.ecocrux.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EcocruxColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = Color.Black,
    secondary = AccentBlue,
    background = BgDarkNavy,
    surface = SurfaceDarkBlue,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentRed,
    surfaceVariant = BorderSlate,
    onSurfaceVariant = TextSecondary
)

@Composable
fun EcocruxTheme(
    content: @Composable () -> Unit,
) {
    // Forcing the dark theme to match the wireframes exactly
    MaterialTheme(
        colorScheme = EcocruxColorScheme,
        typography = Typography,
        content = content
    )
}
