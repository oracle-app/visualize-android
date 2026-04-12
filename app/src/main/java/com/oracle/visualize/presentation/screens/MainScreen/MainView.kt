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
import com.oracle.visualize.presentation.screens.FeedScreen.FeedView
import com.oracle.visualize.presentation.screens.NotificationScreen.NotificationPage


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
            BottomNavBar(
                navItems = viewModel.navItems,
                selectedIndex = selectedIndex,
                onItemSelected = viewModel::onNavItemSelected //Update the state
            )
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            currentRoute = currentRoute //Update the screen type
        )
    }
}



@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    currentRoute: String
) {
    when (currentRoute) {
        NavRoutes.Feed.route -> FeedView(modifier = modifier)
        NavRoutes.Notifications.route -> NotificationPage(modifier = modifier)
    }
}
