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
    primary = DARK_MODE_STRONG_BLUE,
    onPrimary = WHITE,
    primaryContainer = DARK_MODE_LIGHT_BLUE,
    onPrimaryContainer = DARK_MODE_DARK_GRAY,

    secondary = STRONG_ORANGE,
    onSecondary = WHITE,

    secondaryContainer = Color.Transparent,
    onSecondaryContainer = DARK_MODE_NOT_AS_LIGHT_GRAY,

    background = DARK_MODE_BACKGROUND,
    onBackground = DARK_MODE_DARK_GRAY,
    surface = DARK_MODE_BACKGROUND,
    onSurface = DARK_MODE_DARK_GRAY,

    surfaceVariant = DARK_MODE_LIGHTER_BLUE,
    onSurfaceVariant = DARK_MODE_NOT_AS_DARK_GRAY,

    outline = DARK_MODE_GRAYISH_BLUE,
    outlineVariant = DARK_MODE_NOT_AS_DARK_GRAY,
    error = ERROR_RED,
    onError = WHITE,
    scrim = SCRIM_COLOR
)

/**
 * Light Color Scheme using Visualize Brand Identity.
 * Adjusts onBackground/onSurface to DARK_GRAY to match the "negro" requirement for titles.
 */
private val LightColorScheme = lightColorScheme(
    primary = STRONG_BLUE,
    onPrimary = WHITE,
    primaryContainer = LIGHT_BLUE,
    onPrimaryContainer = DARK_GRAY,

    secondary = STRONG_ORANGE,
    onSecondary = WHITE,

    secondaryContainer = WHITE,
    onSecondaryContainer = NOT_AS_LIGHT_GRAY,

    background = VERY_LIGHT_GRAY,
    onBackground = DARK_GRAY,
    surface = VERY_LIGHT_GRAY,
    onSurface = DARK_GRAY,

    surfaceVariant = LIGHTER_BLUE,
    onSurfaceVariant = NOT_AS_DARK_GRAY,

    outline = GRAYISH_BLUE,
    outlineVariant = NOT_AS_DARK_GRAY,
    error = ERROR_RED,
    onError = WHITE,
    scrim = SCRIM_COLOR
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
