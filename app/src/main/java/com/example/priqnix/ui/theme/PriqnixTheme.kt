package com.example.priqnix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Фиолетовые цвета
val PurpleDark = Color(0xFF6C3CE1)
val PurpleLight = Color(0xFF9A67F5)
val DarkBackground = Color(0xFF0F0F12)
val DarkSurface = Color(0xFF1E1E2A)
val OnDark = Color.White

private val DarkColorScheme = darkColorScheme(
    primary = PurpleDark,
    secondary = PurpleLight,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onBackground = OnDark,
    onSurface = OnDark
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,
    secondary = PurpleLight,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun PriqnixTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}