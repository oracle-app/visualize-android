package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oracle.visualize.presentation.navigation.NavRoutes
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.FullVisualizationPage
import com.oracle.visualize.presentation.screens.threadsScreen.ThreadsPage
import com.oracle.visualize.presentation.screens.loginScreen.LoginPage
import com.oracle.visualize.presentation.screens.splashScreen.SplashPage
import com.oracle.visualize.presentation.screens.selectChartScreen.ChartSelectionPage
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.presentation.screens.signupScreen.SignUpPage
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}