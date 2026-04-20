package com.oracle.visualize.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Base palette (raw colors — do not use directly in composables) ───────────

// Teal family
val Teal10  = Color(0xFF002021)
val Teal20  = Color(0xFF00373A)
val Teal30  = Color(0xFF004F54)
val Teal40  = Color(0xFF34797C)   // Primary — Figma TealPrimary light
val Teal80  = Color(0xFFA9C8C4)
val Teal90  = Color(0xFFCDE9EA)   // Lightest — Figma TopBarColor light
val Teal95  = Color(0xFFE6F4F4)

// Orange family
val Orange40 = Color(0xFFE69138)  // Figma OrangeButton light
val Orange80 = Color(0xFFFFB74D)

// Neutral family
val Neutral10  = Color(0xFF13212C)
val Neutral30  = Color(0xFF323232)
val Neutral80  = Color(0xFFCCCCCC)
val Neutral95  = Color(0xFFF5F4F2)
val NeutralWhite = Color(0xFFFFFFFF)

// Error family
val Error40  = Color(0xFFD32F2F)
val Error90  = Color(0xFFFFEBEE)
val Error80  = Color(0xFFEF9A9A)

// ─── Figma Dark Mode exact values (extracted from Figma source code) ──────────
// Screen backgrounds
val FigmaDarkBg         = Color(0xFF282520)   // All screens background
val FigmaDarkTopBar     = Color(0xFF2A5152)   // Top bar + nav bar + dialog bg
val FigmaDarkNavBar     = Color(0xFF2C5354)   // Bottom navbar (Feed screen)
val FigmaDarkNavBarAlt  = Color(0xFF2A5152)   // Bottom navbar (Create/Share)

// Surface / card colors
val FigmaDarkCard       = Color(0xFF3D504D)   // Feed cards
val FigmaDarkCardBorder = Color(0xFF80A7A1)   // Feed card selected border
val FigmaDarkTeamCard   = Color(0xFF415351)   // Team rows unselected
val FigmaDarkSurface2   = Color(0xFF1E1E1E)   // Create screen upload area bg

// Selection / indicator colors
val FigmaDarkSelected   = Color(0xFF244546)   // NavBar selected pill, team selected subtext
val FigmaDarkTeal       = Color(0xFF34797C)   // Selected team card bg, teal primary dark

// Text colors
val FigmaDarkTextPrimary  = Color(0xFFFFFDFD)  // Main titles and body text
val FigmaDarkTextSecond   = Color(0xFFEBE5E5)  // Section titles ("My Teams")
val FigmaDarkTextSub      = Color(0xFF597271)  // Subtexts, descriptions
val FigmaDarkTextMuted    = Color(0xFFBEDBDC)  // Small info text, search placeholder
val FigmaDarkTextAccent   = Color(0xFF8AC1C4)  // Feed meta info, icon tints
val FigmaDarkTextLabel    = Color(0xFFD9D9D9)  // NavBar labels

// Avatar / border
val FigmaDarkAvatarBorder = Color(0xFF244546)  // Avatar circle borders dark
val FigmaDarkBorder       = Color(0xFFBEDBDC)  // Upload box border

// Dialog
val FigmaDarkDialogBg     = Color(0xFF2A5152)  // UnsavedChanges dialog background
val FigmaDarkDialogBody   = Color(0xFFD9D9D9)  // Dialog body text

// Error / destructive
val FigmaDarkErrorText    = Color(0xFFD55555)  // Delete for everyone text

// ─── Light semantic tokens ────────────────────────────────────────────────────

val LightScreenBackground     = Neutral95
val LightTopBarColor          = Teal90
val LightTealPrimary          = Teal40
val LightTealLight            = Teal90
val LightTextDark             = Neutral10
val LightTextGray             = Neutral30
val LightBorderGray           = Neutral80
val LightOrangeButton         = Orange40
val LightNavBarBackground     = Teal90
val LightNavBarSelected       = Teal40
val LightNavBarIconUnselected = Color(0xFF4A454E)
val LightNavBarIconSelected   = NeutralWhite
val LightErrorRed             = Error40
val LightErrorBackground      = Error90
val LightCardBackground       = NeutralWhite
val LightTeamUnselectedBg     = Color(0xFFE5ECEB)
val LightTeamSelectedBg       = Teal40
val LightSearchBarBg          = Color(0xFFF5F5F5)
val LightAvatarBorder         = Color(0xFFE6EDEC)
val LightDialogBg             = NeutralWhite
val LightDialogBody           = Color(0xFF49454F)
val LightSubtextSelected      = Color(0xFFCDE9EA)
val LightSubtextUnselected    = Color(0xFF597271)

// ─── Dark semantic tokens (Figma exact) ──────────────────────────────────────

val DarkScreenBackground     = FigmaDarkBg
val DarkTopBarColor          = FigmaDarkTopBar
val DarkTealPrimary          = FigmaDarkTeal
val DarkTealLight            = FigmaDarkSelected
val DarkTextDark             = FigmaDarkTextPrimary
val DarkTextGray             = FigmaDarkTextSub
val DarkBorderGray           = FigmaDarkAvatarBorder
val DarkOrangeButton         = Orange40              // Figma keeps orange unchanged
val DarkNavBarBackground     = FigmaDarkNavBar
val DarkNavBarSelected       = FigmaDarkSelected
val DarkNavBarIconUnselected = FigmaDarkTextLabel
val DarkNavBarIconSelected   = FigmaDarkTextPrimary
val DarkErrorRed             = FigmaDarkErrorText
val DarkErrorBackground      = Color(0xFF4D1F1F)
val DarkCardBackground       = FigmaDarkCard
val DarkTeamUnselectedBg     = FigmaDarkTeamCard
val DarkTeamSelectedBg       = FigmaDarkTeal
val DarkSearchBarBg          = FigmaDarkSurface2
val DarkAvatarBorder         = FigmaDarkAvatarBorder
val DarkDialogBg             = FigmaDarkDialogBg
val DarkDialogBody           = FigmaDarkDialogBody
val DarkSubtextSelected      = FigmaDarkSelected
val DarkSubtextUnselected    = FigmaDarkTextSub
val DarkSectionTitle         = FigmaDarkTextSecond
val DarkFeedCardBorder       = FigmaDarkCardBorder
val DarkTextAccent           = FigmaDarkTextAccent
val DarkTextMuted            = FigmaDarkTextMuted

// ─── Legacy aliases (backward compatibility — screens not yet migrated) ───────

val ScreenBackground     = LightScreenBackground
val TopBarColor          = LightTopBarColor
val TealPrimary          = LightTealPrimary
val TealLight            = LightTealLight
val TextDark             = LightTextDark
val TextGray             = LightTextGray
val BorderGray           = LightBorderGray
val OrangeButton         = LightOrangeButton
val NavBarBackground     = LightNavBarBackground
val NavBarSelected       = LightNavBarSelected
val NavBarIconUnselected = LightNavBarIconUnselected
val NavBarIconSelected   = LightNavBarIconSelected
val ErrorRed             = LightErrorRed
val ErrorBackground      = LightErrorBackground

// Feed (legacy)
val StrongGreen   = Teal80
val StrongerGreen = Teal40
val Green         = Color(0xFFE6EDEC)
val LightGreen    = Teal90

// Material baseline (legacy)
val Purple80     = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80       = Color(0xFFEFB8C8)
val Purple40     = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40       = Color(0xFF7D5260)