package com.oracle.visualize.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

// ─── Material3 color schemes ──────────────────────────────────────────────────


private val M3DarkColorScheme = darkColorScheme(
    primary              = DarkTealPrimary,
    onPrimary            = DarkNavBarIconSelected,
    primaryContainer     = DarkTealLight,
    secondary            = DarkTealPrimary,
    onSecondary          = DarkNavBarIconSelected,
    secondaryContainer   = DarkNavBarSelected,
    onSecondaryContainer = DarkNavBarIconSelected,
    tertiary             = DarkOrangeButton,
    background           = DarkScreenBackground,
    surface              = DarkSurface2,
    surfaceContainer     = DarkNavBar,
    onBackground         = DarkTextDark,
    onSurface            = DarkNavBarIconUnselected,
    error                = DarkErrorRed
)

private val M3LightColorScheme = lightColorScheme(
    primary              = LightTealPrimary,
    onPrimary            = LightNavBarIconSelected,
    primaryContainer     = LightTealLight,
    secondary            = LightTealPrimary,
    onSecondary          = LightNavBarIconSelected,
    secondaryContainer   = LightNavBarSelected,
    onSecondaryContainer = LightNavBarIconSelected,
    tertiary             = LightOrangeButton,
    background           = LightScreenBackground,
    surface              = LightCardBackground,
    surfaceContainer     = LightNavBarBackground,
    onBackground         = LightTextDark,
    onSurface            = LightNavBarIconUnselected,
    error                = LightErrorRed
)

// ─── VisualizeTheme ───────────────────────────────────────────────────────────

@Composable
fun VisualizeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkColors else LightColors

    val m3ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> M3DarkColorScheme
        else -> M3LightColorScheme
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography  = Typography,
            content     = content
        )
    }
}