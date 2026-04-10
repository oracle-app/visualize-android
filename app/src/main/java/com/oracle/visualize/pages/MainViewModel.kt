package com.oracle.visualize.pages


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.lifecycle.ViewModel
import com.oracle.visualize.navigation.NavItem
import com.oracle.visualize.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    val navItems = listOf(
        NavItem(
            label = "Feed",
            icon = Icons.Default.Home,
            badgeCount = 0,
            route = NavRoutes.Feed.route
        ),
        NavItem(
            label = "Notifications",
            icon = Icons.Default.Notifications,
            badgeCount = 5,
            route = NavRoutes.Notifications.route
        )
    )

    fun onNavItemSelected(index: Int) {
        _selectedIndex.value = index
    }
}