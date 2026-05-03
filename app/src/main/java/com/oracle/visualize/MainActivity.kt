package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.oracle.visualize.presentation.screens.loginScreen.LoginView
import com.oracle.visualize.presentation.screens.resetPasswordScreen.ResetPasswordView
import com.oracle.visualize.presentation.screens.registrationScreen.RegistrationView
import com.oracle.visualize.presentation.screens.verificationScreen.VerificationView
import com.oracle.visualize.presentation.screens.splashScreen.SplashView
import com.oracle.visualize.presentation.screens.mainScreen.MainViewModel
import com.oracle.visualize.ui.theme.VisualizeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point activity for the Visualize application.
 * This activity sets up the application theme and initial navigation/screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisualizeTheme {
                MainScreen()
            }
        }
    }
}

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
    viewModel: MainViewModel = hiltViewModel()
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
                    currentDestination = viewModel.navItems.find { it.destination::class.simpleName?.lowercase() == currentRoute }?.destination,
                    onItemSelected = { destination ->
                        val route = when (destination) {
                            is com.oracle.visualize.presentation.navigation.NavRoutes.Feed -> NavRoutes.Feed.route
                            is com.oracle.visualize.presentation.navigation.NavRoutes.Create -> NavRoutes.Create.route
                            is com.oracle.visualize.presentation.navigation.NavRoutes.Notifications -> NavRoutes.Notifications.route
                            is com.oracle.visualize.presentation.navigation.NavRoutes.Teams -> NavRoutes.Teams.route
                            is com.oracle.visualize.presentation.navigation.NavRoutes.Profile -> NavRoutes.Profile.route
                            is com.oracle.visualize.presentation.navigation.NavRoutes.FullScreen -> ""
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
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

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Splash.route) {
            SplashView(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route)
                },
                onNavigateToSignUp = {
                    navController.navigate(NavRoutes.Registration.route)
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginView(
                onNavigateToRegistration = {
                    navController.navigate(NavRoutes.Registration.route)
                },
                onNavigateToResetPassword = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Feed.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ForgotPassword.route) {
            ResetPasswordView(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Registration.route) {
            RegistrationView(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route)
                },
                onRegistrationSuccess = {
                    navController.navigate(NavRoutes.Verification.route)
                }
            )
        }

        composable(NavRoutes.Verification.route) {
            VerificationView(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(NavRoutes.Feed.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
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

        composable(NavRoutes.Teams.route) {
            // TODO: Add TeamsPage when implemented
        }

        composable(NavRoutes.Profile.route) {
            // TODO: Add ProfilePage when implemented
        }
    }
}
