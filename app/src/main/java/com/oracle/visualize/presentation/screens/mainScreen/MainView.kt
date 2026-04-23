package com.oracle.visualize.presentation.screens.mainScreen

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
import com.oracle.visualize.presentation.screens.feedScreen.FeedPage
import com.oracle.visualize.presentation.screens.notificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.createChartScreen.CreatePage
import com.oracle.visualize.presentation.screens.selectChartScreen.ChartSelectionPage


@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    //We obtain the state from View
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedIndex.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
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
