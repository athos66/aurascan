package com.aura.scanlab.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SuccessGreen,
    secondary = DarkGrey,
    tertiary = AlertRed,
    background = DarkGrey,
    surface = DarkGrey,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = SuccessGreen,
    secondary = LightGrey,
    tertiary = AlertRed,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = DarkGrey,
    onTertiary = White,
    onBackground = DarkGrey,
    onSurface = DarkGrey,
)

@Composable
fun AuraScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
