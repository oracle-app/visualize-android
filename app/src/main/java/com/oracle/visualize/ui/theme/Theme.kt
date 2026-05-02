package com.oracle.visualize.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark Color Scheme using Visualize Brand Identity.
 * Mapped to provide high contrast for "negro" (dark) text and elements.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkMode_StrongBlue,
    onPrimary = White,
    primaryContainer = DarkMode_LightBlue,
    onPrimaryContainer = DarkMode_DarkGray,

    secondary = StrongOrange,
    onSecondary = White,

    secondaryContainer = Color.Transparent,
    onSecondaryContainer = DarkMode_NotAsLightGray,

    background = DarkMode_Background,
    onBackground = DarkMode_DarkGray,
    surface = DarkMode_Background,
    onSurface = DarkMode_DarkGray,

    surfaceVariant = DarkMode_LighterBlue,
    onSurfaceVariant = DarkMode_NotAsDarkGray,

    outline = DarkMode_GrayishBlue,
    outlineVariant = DarkMode_NotAsDarkGray,
    error = ErrorRed,
    onError = White,
    scrim = ScrimColor
)

/**
 * Light Color Scheme using Visualize Brand Identity.
 * Adjusts onBackground/onSurface to DARK_GRAY to match the "negro" requirement for titles.
 */
private val LightColorScheme = lightColorScheme(
    primary = StrongBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkGray,

    secondary = StrongOrange,
    onSecondary = White,

    secondaryContainer = White,
    onSecondaryContainer = NotAsLightGray,

    background = VeryLightGray,
    onBackground = DarkGray,
    surface = VeryLightGray,
    onSurface = DarkGray,

    surfaceVariant = LighterBlue,
    onSurfaceVariant = NotAsDarkGray,

    outline = GrayishBlue,
    outlineVariant = NotAsDarkGray,
    error = ErrorRed,
    onError = White,
    scrim = ScrimColor
)

@Composable
fun VisualizeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
