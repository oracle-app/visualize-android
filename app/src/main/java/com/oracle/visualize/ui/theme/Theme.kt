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



private val DarkColorScheme = darkColorScheme(
    primary = StrongBlue,
    onPrimary = White,
    primaryContainer = DarkMode_LightBlue,
    onPrimaryContainer = DarkMode_DarkGray,

    secondary = StrongOrange,
    onSecondary = White,

    secondaryContainer = Color.Transparent,
    onSecondaryContainer = DarkMode_NotAsLightGray,

    tertiaryContainer = DarkMode_NotAsLightOrange,
    onTertiaryContainer = DarkMode_LightOrange,

    tertiary = DarkMode_GreyishBlue,
    onTertiary = DarkMode_LightGreyishBlue,

    tertiaryFixed = DarkMode_GreyContent,

    background = DarkMode_Background,
    onBackground = DarkMode_NotAsLightGray,
    surface = DarkMode_Background,
    onSurface = DarkMode_NotAsLightGray,

    surfaceVariant = DarkMode_LighterBlue,
    onSurfaceVariant = DarkMode_StrongBlue,

    outline = DarkMode_GrayishBlue,
    outlineVariant = DarkMode_NotAsDarkGray,
    error = ErrorRed,
    onError = White,
    scrim = ScrimColor,
)

private val LightColorScheme = lightColorScheme(
    primary = StrongBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkGray,

    secondary = StrongOrange,
    onSecondary = White,

    secondaryContainer = White,
    onSecondaryContainer = NotAsLightGray,

    tertiaryContainer = NotAsLightOrange,
    onTertiaryContainer = LightOrange,

    tertiary = GreyishBlue,
    onTertiary = LightGreyishBlue,

    tertiaryFixed = GreyContent,

    background = VeryLightGray,
    onBackground = NotAsLightGray,
    surface = White,
    onSurface = Color.Black,

    surfaceVariant = LighterBlue,
    onSurfaceVariant = NotAsLightGray,

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
