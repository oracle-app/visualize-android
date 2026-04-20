package com.oracle.visualize.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Color set ────────────────────────────────────────────────────────────────

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
    // Share screen
    val searchBarBg: Color,
    val avatarBorder: Color,
    val dialogBg: Color,
    val dialogBody: Color,
    val subtextSelected: Color,
    val subtextUnselected: Color,
    val sectionTitle: Color,
    // Feed screen
    val feedCardBg: Color,
    val feedCardBorder: Color,
    val textAccent: Color,   // meta info color (author, date)
    val textMuted: Color,    // placeholder, small info
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
    searchBarBg          = LightSearchBarBg,
    avatarBorder         = LightAvatarBorder,
    dialogBg             = LightDialogBg,
    dialogBody           = LightDialogBody,
    subtextSelected      = LightSubtextSelected,
    subtextUnselected    = LightSubtextUnselected,
    sectionTitle         = LightTextDark,
    feedCardBg           = LightCardBackground,
    feedCardBorder       = LightBorderGray,
    textAccent           = LightTealPrimary,
    textMuted            = Color(0xFF7FA9A9),
    isDark               = false
)

// ─── Dark palette (Figma exact) ───────────────────────────────────────────────

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
    searchBarBg          = DarkSearchBarBg,
    avatarBorder         = DarkAvatarBorder,
    dialogBg             = DarkDialogBg,
    dialogBody           = DarkDialogBody,
    subtextSelected      = DarkSubtextSelected,
    subtextUnselected    = DarkSubtextUnselected,
    sectionTitle         = DarkSectionTitle,
    feedCardBg           = DarkCardBackground,
    feedCardBorder       = DarkFeedCardBorder,
    textAccent           = DarkTextAccent,
    textMuted            = DarkTextMuted,
    isDark               = true
)

// ─── CompositionLocal ─────────────────────────────────────────────────────────

val LocalAppColors = compositionLocalOf<VisualizeColors> { LightColors }

val AppColors: VisualizeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current