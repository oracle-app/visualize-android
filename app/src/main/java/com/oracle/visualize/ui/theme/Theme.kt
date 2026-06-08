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

// Edit later!

private val DarkColorScheme = darkColorScheme(
    primary = DARKprimary,
    onPrimary = DARKonPrimary,
    primaryContainer = DARKprimaryContainer,
    onPrimaryContainer = DARKonPrimaryContainer,
    secondary = DARKsecondary,
    onSecondary = DARKonSecondary,
    secondaryContainer = DARKsecondaryContainer,
    onSecondaryContainer = DARKonSecondaryContainer,
    tertiaryContainer = DARKtertiaryContainer,
    onTertiaryContainer = DARKonTertiaryContainer,
    tertiary = DARKtertiary,
    onTertiary = DARKonTertiary,
    tertiaryFixed = DARKtertiaryFixed,
    background = DARKbackground,
    onBackground = DARKonBackground,
    surface = DARKsurface,
    onSurface = DARKonSurface,
    surfaceVariant = DARKsurfaceVariant,
    onSurfaceVariant = DARKonSurfaceVariant,
    outline = DARKoutline,
    outlineVariant = DARKoutlineVariant,
    error = DARKerror,
    onError = DARKonError,
    scrim = DARKscrim
)

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryFixed = tertiaryFixed,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    error = error,
    onError = onError,
    scrim = scrim
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
