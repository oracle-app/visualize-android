package com.oracle.visualize.presentation.screens.MainScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.domain.models.NavRoutes
import com.oracle.visualize.presentation.components.BottomNavBar
import com.oracle.visualize.presentation.screens.CreateScreen.CreatePage
import com.oracle.visualize.presentation.screens.FeedScreen.FeedPage
import com.oracle.visualize.presentation.screens.NotificationScreen.NotificationPage
import com.oracle.visualize.presentation.screens.ShareScreen.ShareAndPostScreen
import com.oracle.visualize.ui.theme.ScreenBackground

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedIndex.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navItems = viewModel.navItems,
                selectedIndex = selectedIndex,
                onItemSelected = viewModel::onNavItemSelected
            )
        },
        containerColor = ScreenBackground
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            currentRoute = currentRoute,
            onNavigateBack = { viewModel.onNavItemSelected(2) } // Back to Feed
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigateBack: () -> Unit = {}
) {
    when (currentRoute) {
        NavRoutes.Feed.route -> FeedPage(modifier = modifier)
        NavRoutes.Notifications.route -> NotificationPage(modifier = modifier)
        NavRoutes.Create.route -> CreatePage(modifier = modifier)
        NavRoutes.Share.route -> ShareAndPostScreen(onNavigateBack = onNavigateBack)
        // Add other routes as they are implemented
    }
}