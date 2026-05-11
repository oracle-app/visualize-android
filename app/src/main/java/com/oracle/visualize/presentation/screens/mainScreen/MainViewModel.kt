package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.repositories.NotificationRepository
import com.oracle.visualize.presentation.navigation.NavItem
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Main screen (container for bottom navigation).
 * Provides the reactive list of navigation items, including dynamic badge counts.
 * 
 * @property notificationRepository Repository used to observe notification counts.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    notificationRepository: NotificationRepository
) : ViewModel() {

    /**
     * The reactive list of navigation items.
     * Updates automatically when the notification count changes in the repository.
     */
    val navItems: StateFlow<List<NavItem>> = notificationRepository.getNotifications()
        .map { notifications ->
            val count = notifications.size
            listOf(
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
                    badgeCount = count,
                    destination = NavRoutes.Notifications
                ),
                NavItem(
                    label = R.string.nav_profile,
                    icon  = Icons.Default.Person,
                    destination = NavRoutes.Profile(userId = "placeholder")
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
