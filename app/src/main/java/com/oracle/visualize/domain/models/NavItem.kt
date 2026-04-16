package com.oracle.visualize.domain.models

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val route: String
)