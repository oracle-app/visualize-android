package com.oracle.visualize.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val route: String
)