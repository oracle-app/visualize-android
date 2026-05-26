package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.navigation.AppNavHost

/**
 * Main container screen that sets up the navigation host and bottom bar.
 *
 * @param viewModel The [MainViewModel] providing navigation items.
 * Uses [AppNavHost] to manage navigation.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = viewModel.navItems.find { item ->
        backStackEntry?.destination?.hasRoute(item.destination::class) == true
    }?.destination

    val showBottomBar = currentDestination != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navItems = viewModel.navItems,
                    currentDestination = currentDestination,
                    onItemSelected = { destination ->
                        navController.navigate(destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}
