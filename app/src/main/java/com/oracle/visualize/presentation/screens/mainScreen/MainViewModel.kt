package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.NavItem
import com.oracle.visualize.domain.models.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MainViewModel : ViewModel() {

    // NavController owns navigation state — ViewModel only defines the nav items
    val navItems = listOf(
        NavItem(
            label = R.string.nav_create,
            icon  = Icons.Default.Add,
            route = NavRoutes.Create.route
        ),
        NavItem(
            label = R.string.nav_teams,
            icon  = Icons.Default.Groups,
            route = NavRoutes.Teams.route
        ),
        NavItem(
            label = R.string.nav_feed,
            icon  = Icons.Default.Home,
            route = NavRoutes.Feed.route
        ),
        NavItem(
            label = R.string.nav_notifications,
            icon  = Icons.Default.Notifications,
            badgeCount = 5,
            route = NavRoutes.Notifications.route
        ),
        NavItem(
            label = R.string.nav_profile,
            icon  = Icons.Default.Person,
            route = NavRoutes.Profile.route
        )
    )
}

