package com.oracle.visualize.presentation.screens.mainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.navigation.NavRoutes
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.createEditScreen.CreateEditTeamPage
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.profileScreen.ProfilePage
import com.oracle.visualize.presentation.screens.teamsScreen.TeamsPage

private const val ROUTE_CREATE_TEAM = "create_team"
private const val ROUTE_EDIT_TEAM   = "edit_team/{teamId}"

/**
 * Root composable that hosts the [Scaffold] with the bottom nav bar and the main [NavHost].
 *
 * @param viewModel The [MainViewModel] providing nav items (injected by Hilt).
 * @param onToggleTheme Callback for toggling light/dark mode.
 * @param isDarkMode Current dark mode state.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onToggleTheme: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Profile matches any "profile/..." route since userId varies per user.
    val currentNavRoute: NavRoutes? = when {
        currentRoute == NavRoutes.Feed.route          -> NavRoutes.Feed
        currentRoute == NavRoutes.Create.route        -> NavRoutes.Create
        currentRoute == NavRoutes.Notifications.route -> NavRoutes.Notifications
        currentRoute == NavRoutes.Teams.route         -> NavRoutes.Teams
        currentRoute?.startsWith("profile/") == true  ->
            NavRoutes.Profile(userId = currentRoute.removePrefix("profile/"))
        else                                          -> null
    }

    val showBottomBar = currentNavRoute != null

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navItems           = viewModel.navItems,
                    currentDestination = currentNavRoute,
                    onItemSelected     = { destination ->
                        navController.navigate(destination.route) {
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
            modifier      = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

/**
 * Defines the full navigation graph for the app.
 *
 * @param navController The [NavHostController] managing back stack and routing.
 * @param modifier Modifier applied to the [NavHost] container.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = NavRoutes.Feed.route,
        modifier         = modifier
    ) {
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
            TeamsPage(
                modifier           = Modifier.fillMaxSize(),
                onNavigateToCreate = { navController.navigate(ROUTE_CREATE_TEAM) },
                onNavigateToEdit   = { teamId -> navController.navigate("edit_team/$teamId") }
            )
        }

        composable(ROUTE_CREATE_TEAM) {
            CreateEditTeamPage(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route     = ROUTE_EDIT_TEAM,
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) {
            CreateEditTeamPage(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route     = NavRoutes.Profile.ROUTE_PATTERN,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            ProfilePage(modifier = Modifier.fillMaxSize())
        }
    }
}
