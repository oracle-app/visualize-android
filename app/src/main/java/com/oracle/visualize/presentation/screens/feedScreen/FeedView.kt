package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.FeedTopBar
import com.oracle.visualize.presentation.components.SearchSection
import com.oracle.visualize.R

/**
 * Composable representing the Feed screen.
 * Displays a list of visualizations with filtering and search capabilities.
 *
 * @param modifier Modifier for the layout.
 * @param feedViewModel The [FeedViewModel] that provides data and handles logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = hiltViewModel(),
    onVisualizationClick: (String) -> Unit = {}
) {
    val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FeedTopBar(
                scrollBehavior = scrollBehavior,
                selectedFilter = if (uiState is FeedUiState.Success)
                    (uiState as FeedUiState.Success).selectedFilter else VisualizationFilter.ALL,
                onFilterSelected = { feedViewModel.onFilterChange(it) }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = (uiState as? FeedUiState.Success)?.isRefreshing == true,
            onRefresh = { feedViewModel.loadData(forceRefresh = true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            //Smart cast
            when (val state = uiState) {
                is FeedUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FeedUiState.Error -> {
                    Text(
                        text = stringResource(state.message),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FeedUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(22.dp))
                            SearchSection(
                                text = state.searchText,
                                onTextChange = { feedViewModel.onSearchTextChange(it) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (state.items.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.error_viz_not_found),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(
                                items = state.items,
                                key = { it.id }
                            ) { item ->
                                FeedCard(
                                    item = item,
                                    onClick = { onVisualizationClick(item.id) }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                }
            }
        }
    }

