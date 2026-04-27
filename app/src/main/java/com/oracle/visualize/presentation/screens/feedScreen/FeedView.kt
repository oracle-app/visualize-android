package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.FeedTopBar
import com.oracle.visualize.presentation.components.SearchSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FeedTopBar(
                scrollBehavior = scrollBehavior,
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { feedViewModel.onFilterChange(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(22.dp))
                            SearchSection(
                                text = uiState.searchText,
                                onTextChange = { feedViewModel.onSearchTextChange(it) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(uiState.items) { item ->
                            FeedCard(item)
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}