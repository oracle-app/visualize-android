package com.oracle.visualize.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Base palette (raw colors) ────────────────────────────────────────────────


// Teal family
val Teal10  = Color(0xFF002021)
val Teal20  = Color(0xFF00373A)
val Teal30  = Color(0xFF004F54)
val Teal40  = Color(0xFF34797C)
val Teal80  = Color(0xFFA9C8C4)
val Teal90  = Color(0xFFCDE9EA)
val Teal95  = Color(0xFFE6F4F4)

// Orange family
val Orange40 = Color(0xFFE69138)
val Orange80 = Color(0xFFFFB74D)
val Orange90 = Color(0xFFFFE0B2)

// Neutral family
val Neutral10  = Color(0xFF13212C)
val Neutral20  = Color(0xFF1D2B35)
val Neutral30  = Color(0xFF323232)
val Neutral80  = Color(0xFFCCCCCC)
val Neutral90  = Color(0xFFE8E8E8)
val Neutral95  = Color(0xFFF5F4F2)
val Neutral99  = Color(0xFFFCFCFC)
val NeutralWhite = Color(0xFFFFFFFF)

// Error family
val Error40  = Color(0xFFD32F2F)
val Error90  = Color(0xFFFFEBEE)
val Error80  = Color(0xFFEF9A9A)

// Dark surface colors
val DarkSurface    = Color(0xFF1A1C1E)
val DarkSurface2   = Color(0xFF1F2527)
val DarkSurface3   = Color(0xFF252B2D)
val DarkNavBar     = Color(0xFF1F2527)
val DarkTopBar     = Color(0xFF1F3335)

// ─── Light semantic tokens ────────────────────────────────────────────────────

val LightScreenBackground  = Neutral95
val LightTopBarColor       = Teal90
val LightTealPrimary       = Teal40
val LightTealLight         = Teal90
val LightTextDark          = Neutral10
val LightTextGray          = Neutral30
val LightBorderGray        = Neutral80
val LightOrangeButton      = Orange40
val LightNavBarBackground  = Teal90
val LightNavBarSelected    = Teal40
val LightNavBarIconUnselected = Color(0xFF4A454E)
val LightNavBarIconSelected   = NeutralWhite
val LightErrorRed          = Error40
val LightErrorBackground   = Error90
val LightCardBackground    = NeutralWhite
val LightTeamUnselectedBg  = Color(0xFFE5ECEB)
val LightTeamSelectedBg    = Teal40

// ─── Dark semantic tokens ─────────────────────────────────────────────────────

val DarkScreenBackground   = DarkSurface
val DarkTopBarColor        = DarkTopBar
val DarkTealPrimary        = Teal80
val DarkTealLight          = Teal20
val DarkTextDark           = Color(0xFFE1E3E3)
val DarkTextGray           = Color(0xFFB0B8B8)
val DarkBorderGray         = Color(0xFF3D4F50)
val DarkOrangeButton       = Orange80
val DarkNavBarBackground   = DarkNavBar
val DarkNavBarSelected     = Teal80
val DarkNavBarIconUnselected = Color(0xFFCAC4D0)
val DarkNavBarIconSelected   = Neutral10
val DarkErrorRed           = Error80
val DarkErrorBackground    = Color(0xFF4D1F1F)
val DarkCardBackground     = DarkSurface3
val DarkTeamUnselectedBg   = Color(0xFF243234)
val DarkTeamSelectedBg     = Teal30


val ScreenBackground  = LightScreenBackground
val TopBarColor       = LightTopBarColor
val TealPrimary       = LightTealPrimary
val TealLight         = LightTealLight
val TextDark          = LightTextDark
val TextGray          = LightTextGray
val BorderGray        = LightBorderGray
val OrangeButton      = LightOrangeButton
val NavBarBackground  = LightNavBarBackground
val NavBarSelected    = LightNavBarSelected
val NavBarIconUnselected = LightNavBarIconUnselected
val NavBarIconSelected   = LightNavBarIconSelected
val ErrorRed          = LightErrorRed
val ErrorBackground   = LightErrorBackground

// Feed (legacy)
val StrongGreen   = Teal80
val StrongerGreen = Teal40
val Green         = Color(0xFFE6EDEC)
val LightGreen    = Teal90

val Purple80      = Color(0xFFD0BCFF)
val PurpleGrey80  = Color(0xFFCCC2DC)
val Pink80        = Color(0xFFEFB8C8)
val Purple40      = Color(0xFF6650a4)
val PurpleGrey40  = Color(0xFF625b71)
val Pink40        = Color(0xFF7D5260)