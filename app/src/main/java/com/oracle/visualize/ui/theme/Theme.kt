package com.oracle.visualize.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TEAL_200,
    onPrimary = BLACK,
    secondary = PURPLE_200,
    tertiary = TEAL_700,
    background = BLACK,
    surface = BLACK,
    onBackground = WHITE,
    onSurface = WHITE,
    error = RED_900
)

private val LightColorScheme = lightColorScheme(
    primary = TEAL_700,
    onPrimary = WHITE,
    secondary = PURPLE_500,
    tertiary = TEAL_200,
    background = WHITE,
    surface = GREY_50,
    onBackground = BLACK,
    onSurface = BLACK,
    error = RED_900
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
