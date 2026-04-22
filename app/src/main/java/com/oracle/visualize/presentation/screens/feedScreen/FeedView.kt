package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.FeedTopBar
import com.oracle.visualize.presentation.components.SearchSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = viewModel()
) {
    val searchText = feedViewModel.searchText
    val itemsList = feedViewModel.items
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { FeedTopBar(scrollBehavior = scrollBehavior) }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item{
                    SearchSection(
                        text = searchText,
                        onTextChange = { feedViewModel.onSearchTextChange(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(itemsList) { item ->
                    FeedCard(item)
                }
            }
        }

    }
}