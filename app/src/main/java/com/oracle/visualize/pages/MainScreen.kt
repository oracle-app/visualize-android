package com.oracle.visualize.pages


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.navigation.BottomNavBar



@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val selectedIndex by viewModel.selectedIndex.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navItems = viewModel.navItems,
                selectedIndex = selectedIndex,
                onItemSelected = viewModel::onNavItemSelected
            )
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex
        )
    }
}



@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int
) {
    when (selectedIndex) {
        0 -> FeedPage(modifier = modifier)
        1 -> NotificationPage(modifier = modifier)
    }
}
