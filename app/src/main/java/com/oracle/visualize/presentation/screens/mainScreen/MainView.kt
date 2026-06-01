package com.oracle.visualize.presentation.screens.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.navigation.AppNavHost
import com.oracle.visualize.presentation.navigation.NavRoutes

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val navItems by viewModel.navItems.collectAsStateWithLifecycle()

    LaunchedEffect(backStackEntry?.destination?.route) {
        val isFeed = backStackEntry?.destination?.hasRoute(NavRoutes.Feed::class) == true
        val isSplash = backStackEntry?.destination?.hasRoute(NavRoutes.Splash::class) == true
        val isLogin = backStackEntry?.destination?.hasRoute(NavRoutes.Login::class) == true

        if (isFeed) {
            viewModel.loadNavItems()
        } else if (isSplash || isLogin) {
            viewModel.clearState()
        }
    }

    val currentDestination = navItems.find { item ->
        backStackEntry?.destination?.hasRoute(item.destination::class) == true
    }?.destination

    val showBottomBar = currentDestination != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar && navItems.isNotEmpty()) {
                BottomNavBar(
                    navItems = navItems,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
