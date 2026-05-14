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
import com.oracle.visualize.presentation.screens.loginScreen.LoginPage
import com.oracle.visualize.presentation.screens.splashScreen.SplashPage
import com.oracle.visualize.presentation.screens.selectChartScreen.ChartSelectionPage
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.presentation.screens.signupScreen.SignUpPage


/**
 * Main container screen that sets up the navigation host and bottom bar.
 *
 * @param viewModel The [MainViewModel] providing navigation items.
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

// ─── NavHost ──────────────────────────────────────────────────────────────────

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash,
        modifier = modifier
    ) {
        composable<NavRoutes.Feed> {
            FeedPage(
                modifier = Modifier.fillMaxSize(),
                onVisualizationClick = { visualizationId ->
                    navController.navigate(
                        NavRoutes.FullScreen(visualizationId)
                    )
                }
            )
        }

        composable<NavRoutes.Create> {
            CreatePage(
                modifier = Modifier.fillMaxSize(),
                onNavigateToSelection = {
                    navController.navigate(NavRoutes.ChartSelection)
                }
            )
        }

        composable<NavRoutes.ChartSelection> {
            ChartSelectionPage(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShare = {
                    navController.navigate(NavRoutes.ShareAndPost)
                },
                onNavigateToFeed = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.ChartSelection) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.ShareAndPost> {
            ShareAndPostScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Teams> {
            // TODO: Implement TeamsPage
        }

        composable<NavRoutes.Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<NavRoutes.Profile>()
            ProfilePage(
                modifier = Modifier.fillMaxSize()
            )
            // TODO: Pass profile.userId to ProfilePage
        }

        composable<NavRoutes.FullScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.FullScreen>()

            FullVisualizationPage(
                visualizationId = route.visualizationId,
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    navController.popBackStack()
                },
                onThreadsClick = {
                }
            )
        }

        composable<NavRoutes.Splash> {
            SplashPage(
                modifier = Modifier.fillMaxSize(),
                onSessionActive = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login)
                },
                onSignUpClick = {
                    navController.navigate(NavRoutes.Signup)
                }
            )
        }

        composable<NavRoutes.Login> {
            LoginPage(
                modifier = Modifier.fillMaxSize(),
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Login) {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(NavRoutes.Signup)
                }
            )
        }

        composable<NavRoutes.Signup> {
            SignUpPage(
                modifier = Modifier.fillMaxSize(),
                onSignUpSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.Signup) {
                            inclusive = true
                        }
                    }
                }
            )
        }

    }
}
