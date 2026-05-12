package com.oracle.visualize.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.loginScreen.LoginView
import com.oracle.visualize.presentation.screens.resetPasswordScreen.ResetPasswordView
import com.oracle.visualize.presentation.screens.registrationScreen.RegistrationView
import com.oracle.visualize.presentation.screens.verificationScreen.VerificationView
import com.oracle.visualize.presentation.screens.splashScreen.SplashView
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.FullVisualizationPage

/**
 * The main navigation host for the application.
 * Defines all the routes and their corresponding composable screens.
 *
 * @param navController The [NavHostController] that manages the navigation within this host.
 * @param modifier The modifier to be applied to the NavHost.
 */
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
