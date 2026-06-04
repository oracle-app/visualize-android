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
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.enums.UserType
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.presentation.navigation.NavItem
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navItems = MutableStateFlow<List<NavItem>>(emptyList())
    val navItems: StateFlow<List<NavItem>> = _navItems.asStateFlow()
    private var lastLoadedUserId: String? = null

    init {
        loadNavItems()
    }

    fun loadNavItems() {
        viewModelScope.launch {
            try {
                val currentUserID = authRepository.getCurrentUserID() ?: ""

                if (currentUserID.isBlank()) return@launch
                if (currentUserID == lastLoadedUserId && _navItems.value.isNotEmpty()) return@launch
                val userResult = userRepository.getUserByUserID(currentUserID)
                val currentUserType = when (userResult) {
                    is AppResult.Success -> userResult.data.userType
                    is AppResult.Error -> UserType.CONSUMER
                }

                lastLoadedUserId = currentUserID
                val items = mutableListOf(
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
                        destination = NavRoutes.Profile(userId = currentUserID)
                    )
                )
                if (currentUserType != UserType.CONSUMER) {
                    items.add(
                        0,
                        NavItem(
                            label = R.string.nav_create,
                            icon  = Icons.Default.Add,
                            destination = NavRoutes.Create
                        )
                    )
                }

                _navItems.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearState() {
        _navItems.value = emptyList()
        lastLoadedUserId = null
    }
}
