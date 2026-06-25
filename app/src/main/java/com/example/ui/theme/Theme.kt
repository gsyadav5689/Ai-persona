package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentIndigo,
    tertiary = AccentRose,
    background = Slate900,
    surface = Slate800,
    surfaceVariant = Slate700,
    onPrimary = Slate50,
    onSecondary = Slate50,
    onTertiary = Slate50,
    onBackground = Slate50,
    onSurface = Slate100,
    error = Color(0xFFEF4444)
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    secondary = AccentIndigo,
    tertiary = AccentRose,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark Theme by default for the cinematic luxury feel
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our tailored palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) PremiumDarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
