package com.oracle.visualize.presentation.screens.MainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.NavItem
import com.oracle.visualize.domain.models.NavRoutes

class MainViewModel : ViewModel() {

    // NavController owns navigation state — ViewModel only defines the nav items
    val navItems = listOf(
        NavItem(
            label = "Create",
            icon  = Icons.Default.Add,
            route = NavRoutes.Create.route
        ),
        NavItem(
            label = "Teams",
            icon  = Icons.Default.Groups,
            route = NavRoutes.Teams.route
        ),
        NavItem(
            label = "Feed",
            icon  = Icons.Default.Home,
            route = NavRoutes.Feed.route
        ),
        NavItem(
            label = "Notifications",
            icon  = Icons.Default.Notifications,
            badgeCount = 5,
            route = NavRoutes.Notifications.route
        ),
        NavItem(
            label = "Profile",
            icon  = Icons.Default.Person,
            route = NavRoutes.Profile.route
        )
    )
}