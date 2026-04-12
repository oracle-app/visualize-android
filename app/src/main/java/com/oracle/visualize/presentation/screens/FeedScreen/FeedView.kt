package com.oracle.visualize.presentation.screens.FeedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.SearchSection

@Composable
fun FeedView(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = viewModel()
) {
    val searchText = feedViewModel.searchText
    val itemsList = feedViewModel.items

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        SearchSection(
            text = searchText,
            onTextChange = { feedViewModel.onSearchTextChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(itemsList) { item ->
                FeedCard(item)
            }
        }
    }
}