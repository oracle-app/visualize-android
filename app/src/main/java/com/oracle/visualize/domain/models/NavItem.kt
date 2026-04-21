package com.oracle.visualize.domain.models

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    @StringRes val label: Int,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val route: String
)