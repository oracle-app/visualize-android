package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oracle.visualize.domain.models.NavRoutes
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage


// Bottom nav destinations — screens outside this list hide the nav bar
private val bottomNavDestinations = setOf(
    NavRoutes.Feed::class,
    NavRoutes.Create::class,
    NavRoutes.Notifications::class,
    NavRoutes.Teams::class,
    NavRoutes.Profile::class
)

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onToggleTheme: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val showBottomBar = backStackEntry?.destination?.hasRoute(NavRoutes::class) == true
            && bottomNavDestinations.any { backStackEntry?.destination?.hasRoute(it) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navItems = viewModel.navItems, navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

// ─── NavHost ──────────────────────────────────────────────────────────────────

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Feed, // Passing the object, not a string
        modifier = modifier
    ) {
        composable<NavRoutes.Feed> {
            FeedPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Create> {
            CreatePage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Teams> {
            // TODO: Implement TeamsPage
        }

        composable<NavRoutes.Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<NavRoutes.Profile>()
            // TODO: Pass profile.userId to ProfilePage
        }
    }
}