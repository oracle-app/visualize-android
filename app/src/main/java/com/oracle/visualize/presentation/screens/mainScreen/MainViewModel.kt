package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.navigation.NavItem
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    /**
     * The list of navigation items to be displayed in the main UI.
     * The NavController (UI layer) will use the 'destination.route' for actual navigation.
     */
    val navItems = listOf(
        NavItem(
            label = R.string.nav_create,
            icon  = Icons.Default.Add,
            destination = NavRoutes.Create
        ),
        NavItem(
            label = R.string.nav_teams,
            icon  = Icons.Default.Groups,
            destination = NavRoutes.Teams
        ),
        NavItem(
            label = R.string.nav_feed,
            icon  = Icons.Default.Home,
            destination = NavRoutes.Feed
        ),
        NavItem(
            label = R.string.nav_notifications,
            icon  = Icons.Default.Notifications,
            badgeCount = 5,
            destination = NavRoutes.Notifications
        ),
        NavItem(
            label = R.string.nav_profile,
            icon  = Icons.Default.Person,
            destination = NavRoutes.Profile
        )
    )
}