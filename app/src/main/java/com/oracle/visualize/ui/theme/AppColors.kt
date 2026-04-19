package com.oracle.visualize.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Color set data class ─────────────────────────────────────────────────────

data class VisualizeColors(
    val screenBackground: Color,
    val topBarColor: Color,
    val tealPrimary: Color,
    val tealLight: Color,
    val textDark: Color,
    val textGray: Color,
    val borderGray: Color,
    val orangeButton: Color,
    val navBarBackground: Color,
    val navBarSelected: Color,
    val navBarIconUnselected: Color,
    val navBarIconSelected: Color,
    val errorRed: Color,
    val errorBackground: Color,
    val cardBackground: Color,
    val teamUnselectedBg: Color,
    val teamSelectedBg: Color,
    val isDark: Boolean
)

// ─── Light palette ────────────────────────────────────────────────────────────

val LightColors = VisualizeColors(
    screenBackground     = LightScreenBackground,
    topBarColor          = LightTopBarColor,
    tealPrimary          = LightTealPrimary,
    tealLight            = LightTealLight,
    textDark             = LightTextDark,
    textGray             = LightTextGray,
    borderGray           = LightBorderGray,
    orangeButton         = LightOrangeButton,
    navBarBackground     = LightNavBarBackground,
    navBarSelected       = LightNavBarSelected,
    navBarIconUnselected = LightNavBarIconUnselected,
    navBarIconSelected   = LightNavBarIconSelected,
    errorRed             = LightErrorRed,
    errorBackground      = LightErrorBackground,
    cardBackground       = LightCardBackground,
    teamUnselectedBg     = LightTeamUnselectedBg,
    teamSelectedBg       = LightTeamSelectedBg,
    isDark               = false
)

// ─── Dark palette ─────────────────────────────────────────────────────────────

val DarkColors = VisualizeColors(
    screenBackground     = DarkScreenBackground,
    topBarColor          = DarkTopBarColor,
    tealPrimary          = DarkTealPrimary,
    tealLight            = DarkTealLight,
    textDark             = DarkTextDark,
    textGray             = DarkTextGray,
    borderGray           = DarkBorderGray,
    orangeButton         = DarkOrangeButton,
    navBarBackground     = DarkNavBarBackground,
    navBarSelected       = DarkNavBarSelected,
    navBarIconUnselected = DarkNavBarIconUnselected,
    navBarIconSelected   = DarkNavBarIconSelected,
    errorRed             = DarkErrorRed,
    errorBackground      = DarkErrorBackground,
    cardBackground       = DarkCardBackground,
    teamUnselectedBg     = DarkTeamUnselectedBg,
    teamSelectedBg       = DarkTeamSelectedBg,
    isDark               = true
)

// ─── CompositionLocal ─────────────────────────────────────────────────────────


val LocalAppColors = compositionLocalOf<VisualizeColors> { LightColors }

// ─── Accessor ─────────────────────────────────────────────────────────────────

val AppColors: VisualizeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current