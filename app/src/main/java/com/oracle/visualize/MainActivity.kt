package com.oracle.visualize

import android.os.Bundle
import android.view.WindowManager
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
import com.oracle.visualize.presentation.screens.loginScreen.LoginView
import com.oracle.visualize.presentation.screens.resetPasswordScreen.ResetPasswordView
import com.oracle.visualize.presentation.screens.registrationScreen.RegistrationView
import com.oracle.visualize.presentation.screens.verificationScreen.VerificationView
import com.oracle.visualize.presentation.screens.splashScreen.SplashView
import com.oracle.visualize.presentation.screens.mainScreen.MainViewModel
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.FullVisualizationPage
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
        
        // Prevent screenshots and screen recording for security
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        
        enableEdgeToEdge()
        setContent {
            VisualizeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = viewModel.navItems.find { item ->
        backStackEntry?.destination?.hasRoute(item.destination::class) == true
    }?.destination

    val showBottomBar = currentDestination != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash,
        modifier = modifier
    ) {
        composable<NavRoutes.Splash> {
            SplashView(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login)
                },
                onNavigateToSignUp = {
                    navController.navigate(NavRoutes.Registration)
                }
            )
        }

        composable<NavRoutes.Login> {
            LoginView(
                onNavigateToRegistration = {
                    navController.navigate(NavRoutes.Registration)
                },
                onNavigateToResetPassword = {
                    navController.navigate(NavRoutes.ForgotPassword)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.ForgotPassword> {
            ResetPasswordView(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.Registration> {
            RegistrationView(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login)
                },
                onRegistrationSuccess = {
                    navController.navigate(NavRoutes.Verification)
                }
            )
        }

        composable<NavRoutes.Verification> {
            VerificationView(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }

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
            CreatePage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Teams> {
            // TODO: Implement TeamsPage
        }

        composable<NavRoutes.Profile> {
            ProfilePage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.FullScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.FullScreen>()
            FullVisualizationPage(
                visualizationId = route.visualizationId,
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    navController.popBackStack()
                },
                onThreadsClick = {}
            )
        }
    }
}
