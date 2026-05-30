package com.oracle.visualize.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import com.oracle.visualize.presentation.screens.teamsScreen.TeamsPage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.createEditScreen.CreateEditTeamPage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.FullVisualizationPage
import com.oracle.visualize.presentation.screens.loginScreen.LoginPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.resetPasswordScreen.ResetPasswordPage
import com.oracle.visualize.presentation.screens.selectChartScreen.ChartSelectionPage
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.presentation.screens.signupScreen.SignUpPage
import com.oracle.visualize.presentation.screens.splashScreen.SplashPage
import com.oracle.visualize.presentation.screens.threadsScreen.ThreadsPage


/**
 * The main navigation host for the application.
 * Defines all the routes and their corresponding composable screens.
 *
 * @param navController The [NavHostController] that manages the navigation within this host.
 * @param modifier The modifier to be applied to the NavHost.
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController    = navController,
        startDestination = NavRoutes.Splash,
        modifier         = modifier
    ) {
        composable<NavRoutes.Feed> {
            FeedPage(
                modifier             = Modifier.fillMaxSize(),
                onVisualizationClick = { visualizationId ->
                    navController.navigate(NavRoutes.FullScreen(visualizationId))
                }
            )
        }

        composable<NavRoutes.Create> {
            CreatePage(
                modifier = Modifier.fillMaxSize(),
                onNavigateToSelection = { taskId ->
                    navController.navigate(NavRoutes.ChartSelection(taskId))
                }
            )
        }

        composable<NavRoutes.ChartSelection> {
            ChartSelectionPage(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShare = { taskId, indices, titles ->
                    navController.navigate(NavRoutes.ShareAndPost(taskId, indices, titles))
                },
                onNavigateToFeed = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Feed) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.ShareAndPost> {
            ShareAndPostScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFeed = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Feed) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Teams> {
            // TODO: Implement TeamsPage
        }

        composable<NavRoutes.Profile> {
            ProfilePage(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                onLogout = {
                    navController.navigate(NavRoutes.Splash)
                }
            )
        }

        composable<NavRoutes.FullScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.FullScreen>()
            FullVisualizationPage(
                visualizationId = route.visualizationId,
                modifier = Modifier.fillMaxSize(),
                startInSnippingMode = route.startInSnippingMode,
                onBackClick = {
                    navController.popBackStack()
                },
                onThreadsClick = { uri ->
                    navController.navigate(
                        NavRoutes.Threads(
                            visualizationId = route.visualizationId,
                            snipUri = uri
                        )
                    )
                }
            )
        }

        composable<NavRoutes.Threads> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.Threads>()
            ThreadsPage(
                visualizationId = route.visualizationId,
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    navController.popBackStack()
                },
                onCropClick = {
                    navController.navigate(
                        NavRoutes.FullScreen(
                            visualizationId = route.visualizationId,
                            startInSnippingMode = true
                        )
                    )
                },
                image = route.snipUri
            )
        }

        composable<NavRoutes.Teams> {
            TeamsPage(
                modifier           = Modifier.fillMaxSize(),
                onNavigateToCreate = {
                    navController.navigate(NavRoutes.CreateEditTeam(teamId = null))
                },
                onNavigateToEdit   = { teamId ->
                    navController.navigate(NavRoutes.CreateEditTeam(teamId = teamId))
                }
            )
        }

        composable<NavRoutes.CreateEditTeam> {
            CreateEditTeamPage(
                onNavigateBack = { navController.popBackStack() }
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
                onSignUpClick  = { navController.navigate(NavRoutes.Signup) },
                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ResetPassword)
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

        composable<NavRoutes.ResetPassword> {
            ResetPasswordPage(
                modifier = Modifier.fillMaxSize(),
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
