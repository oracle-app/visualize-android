package com.oracle.visualize.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * UI model representing a single item in the application's top-level navigation (e.g., Bottom Bar).
 *
 * This model links UI metadata with a strongly-typed destination to ensure navigation safety
 * and consistency across the app.
 *
 * @property label The string resource ID for the visible label of the navigation item.
 * @property icon The [androidx.compose.ui.graphics.vector.ImageVector] displayed as the visual representation of the item.
 * @property badgeCount The count to display in an optional notification badge. Defaults to 0 (no badge).
 * @property destination The [NavRoutes] destination that this item targets.
 */
data class NavItem(
    @StringRes val label: Int,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val destination: NavRoutes
)