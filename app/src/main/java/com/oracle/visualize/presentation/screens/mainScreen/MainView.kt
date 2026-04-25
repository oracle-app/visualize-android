package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oracle.visualize.domain.models.NavRoutes
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.login.LoginScreen
import com.oracle.visualize.presentation.screens.login.ResetPasswordScreen
import com.oracle.visualize.presentation.screens.registration.RegistrationScreen
import com.oracle.visualize.presentation.screens.registration.VerificationScreen


// Bottom nav destinations — screens outside this list hide the nav bar
private val bottomNavRoutes = setOf(
    NavRoutes.Feed.route,
    NavRoutes.Create.route,
    NavRoutes.Notifications.route,
    NavRoutes.Teams.route,
    NavRoutes.Profile.route
)

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onToggleTheme: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Only show bottom nav on main tab destinations
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navItems = viewModel.navItems,
                    navController = navController
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

// ─── NavHost ──────────────────────────────────────────────────────────────────

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onNavigateToRegistration = {
                    navController.navigate(NavRoutes.Registration.route)
                },
                onNavigateToResetPassword = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                }
            )
        }

        composable(NavRoutes.ForgotPassword.route) {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.Registration.route) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route)
                },
                onNavigateToVerification = {
                    navController.navigate(NavRoutes.Verification.route)
                }
            )
        }

        composable(NavRoutes.Verification.route) {
            VerificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.Feed.route) {
            FeedPage(modifier = Modifier.fillMaxSize())
        }

        composable(NavRoutes.Create.route) {
            CreatePage(modifier = Modifier.fillMaxSize())
        }

        composable(NavRoutes.Notifications.route) {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        // Teams and Profile — placeholders until screens are implemented
        composable(NavRoutes.Teams.route) {
            // TODO: Add TeamsPage when implemented
        }

        composable(NavRoutes.Profile.route) {
            // TODO: Add ProfilePage when implemented
        }
    }
}
