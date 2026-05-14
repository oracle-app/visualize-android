package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.PaddingValues
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
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.navigation.NavRoutes
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.FullVisualizationPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.shareScreen.ShareAndPostScreen
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.ShareWithTeammatesScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController  = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = viewModel.navItems.find { item ->
        backStackEntry?.destination?.hasRoute(item.destination::class) == true
    }?.destination

    val showBottomBar = currentDestination != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navItems           = viewModel.navItems,
                    currentDestination = currentDestination,
                    onItemSelected     = { destination ->
                        navController.navigate(destination) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding  = innerPadding,
            modifier      = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = NavRoutes.Feed,
        modifier         = modifier
    ) {
        composable<NavRoutes.Feed> {
            FeedPage(
                modifier             = Modifier.fillMaxSize().padding(innerPadding),
                onVisualizationClick = { visualizationId ->
                    navController.navigate(NavRoutes.FullScreen(visualizationId))
                },
                onShareVisualization = { visualizationId ->
                    navController.navigate(NavRoutes.ShareWithTeammates(visualizationId))
                }
            )
        }

        composable<NavRoutes.Create> {
            CreatePage(modifier = Modifier.fillMaxSize().padding(innerPadding))
        }

        composable<NavRoutes.Notifications> {
            NotificationPage(modifier = Modifier.fillMaxSize().padding(innerPadding))
        }

        composable<NavRoutes.Teams> {
            // TODO: Implement TeamsPage

        }

        composable<NavRoutes.Profile> {
            ProfilePage(modifier = Modifier.fillMaxSize().padding(innerPadding))
        }


        composable<NavRoutes.FullScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.FullScreen>()
            FullVisualizationPage(
                visualizationId = route.visualizationId,
                modifier        = Modifier.fillMaxSize(),
                onBackClick     = { navController.popBackStack() },
                onThreadsClick  = {}
            )
        }

        composable<NavRoutes.Share> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.Share>()
            ShareAndPostScreen(
                visualizationId = route.visualizationId,
                onNavigateBack  = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.ShareWithTeammates> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.ShareWithTeammates>()
            ShareWithTeammatesScreen(
                visualizationId = route.visualizationId,
                onNavigateBack  = { navController.popBackStack() }
            )
        }
    }
}
