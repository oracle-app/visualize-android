package com.oracle.visualize.presentation.screens.MainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.domain.models.NavRoutes
import com.oracle.visualize.presentation.screens.FeedScreen.FeedPage
import com.oracle.visualize.presentation.screens.NotificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.createschart.CreatePage
import com.oracle.visualize.presentation.screens.selectchart.ChartSelectionPage
import com.oracle.visualize.ui.theme.ScreenBackground

/**
 * MainScreen acts as the primary navigation host for the application.
 * It coordinates the visibility of the bottom navigation bar and the current content area.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    // We obtain the state from View
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedIndex.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Navigation bar is hidden on specific flows like chart selection
            if (currentRoute != NavRoutes.ChartSelection.route) {
                BottomNavBar(
                    navItems = viewModel.navItems,
                    selectedIndex = selectedIndex,
                    onItemSelected = viewModel::onNavItemSelected
                )
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            currentRoute = currentRoute,
            onNavigate = viewModel::onNavItemSelected,
            viewModel = viewModel
        )
    }
}

/**
 * ContentScreen determines which page to display based on the [currentRoute].
 */
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (Int) -> Unit,
    viewModel: MainViewModel
) {
    when (currentRoute) {
        NavRoutes.Feed.route -> FeedPage(modifier = modifier)
        NavRoutes.Notifications.route -> NotificationPage(modifier = modifier)
        NavRoutes.Create.route -> CreatePage(
            modifier = modifier,
            onNavigateToSelection = { viewModel.navigateToRoute(NavRoutes.ChartSelection.route) }
        )
        NavRoutes.ChartSelection.route -> ChartSelectionPage(
            onBack = { viewModel.navigateToRoute(NavRoutes.Create.route) },
            onNavigateToShare = {}
        )
        else -> { }
    }
}
