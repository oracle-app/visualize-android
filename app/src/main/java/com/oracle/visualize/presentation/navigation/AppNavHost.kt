package com.oracle.visualize.presentation.navigation

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
import com.oracle.visualize.presentation.screens.selectChartScreen.ChartSelectionPage
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.ShareWithTeammatesScreen
import com.oracle.visualize.presentation.screens.signupScreen.SignUpPage
import com.oracle.visualize.presentation.screens.splashScreen.SplashPage
import com.oracle.visualize.presentation.screens.teamsScreen.TeamsPage
import com.oracle.visualize.presentation.screens.threadsScreen.ThreadsPage

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController    = navController,
        startDestination = NavRoutes.Splash,
        modifier         = modifier
    ) {
        composable<NavRoutes.Feed> {
            FeedPage(
                modifier = Modifier.fillMaxSize(),
                onVisualizationClick = { visualizationId ->
                    navController.navigate(NavRoutes.FullScreen(visualizationId))
                },
                onShareVisualization = { visualizationId ->
                    navController.navigate(NavRoutes.ShareWithTeammates(visualizationId))
                }
            )
        }

        composable<NavRoutes.Create> {
            CreatePage(
                modifier = Modifier.fillMaxSize(),
                onNavigateToSelection = {
                    navController.navigate(NavRoutes.ChartSelection(taskId = ""))
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
                        popUpTo<NavRoutes.ChartSelection> { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoutes.ShareAndPost> {
            ShareAndPostScreen(
                modifier       = Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize())
        }

        composable<NavRoutes.Teams> {
            TeamsPage(
                modifier = Modifier.fillMaxSize(),
                onNavigateToCreate = {
                    navController.navigate(NavRoutes.CreateEditTeam(teamId = null))
                },
                onNavigateToEdit = { teamId ->
                    navController.navigate(NavRoutes.CreateEditTeam(teamId = teamId))
                }
            )
        }

        composable<NavRoutes.CreateEditTeam> {
            CreateEditTeamPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.Profile> {
            ProfilePage(modifier = Modifier.fillMaxSize())
            // TODO: Pass profile.userId to ProfilePage
        }

        composable<NavRoutes.FullScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.FullScreen>()
            FullVisualizationPage(
                visualizationId = route.visualizationId,
                modifier        = Modifier.fillMaxSize(),
                onBackClick     = { navController.popBackStack() },
                onThreadsClick  = {
                    navController.navigate(NavRoutes.Threads(route.visualizationId))
                }
            )
        }

        composable<NavRoutes.Threads> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.Threads>()
            ThreadsPage(
                visualizationId = route.visualizationId,
                modifier        = Modifier.fillMaxSize(),
                onBackClick     = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.ShareWithTeammates> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.ShareWithTeammates>()
            ShareWithTeammatesScreen(
                visualizationId = route.visualizationId,
                onNavigateBack  = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.Splash> {
            SplashPage(
                modifier = Modifier.fillMaxSize(),
                onSessionActive = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) { inclusive = true }
                    }
                },
                onLoginClick  = { navController.navigate(NavRoutes.Login) },
                onSignUpClick = { navController.navigate(NavRoutes.Signup) }
            )
        }

        composable<NavRoutes.Login> {
            LoginPage(
                modifier = Modifier.fillMaxSize(),
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(NavRoutes.Signup) }
            )
        }

        composable<NavRoutes.Signup> {
            SignUpPage(
                modifier = Modifier.fillMaxSize(),
                onSignUpSuccess = {
                    navController.navigate(NavRoutes.Feed) {
                        popUpTo(NavRoutes.Splash) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.Signup) { inclusive = true }
                    }
                }
            )
        }
    }
}
