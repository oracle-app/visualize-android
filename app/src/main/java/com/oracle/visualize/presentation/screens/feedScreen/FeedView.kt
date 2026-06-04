package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.enums.UserType
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.models.policyObjects.VisualizationPermissions
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.FeedTopBar
import com.oracle.visualize.presentation.components.SearchSection
import com.oracle.visualize.presentation.screens.feedScreen.components.DeleteForEveryoneDialog
import com.oracle.visualize.presentation.screens.feedScreen.components.DeleteForMeDialog
import com.oracle.visualize.presentation.screens.feedScreen.components.SkeletonFeedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = hiltViewModel(),
    onVisualizationClick: (String) -> Unit = {},
    onShareVisualization: (String) -> Unit = {}
) {
    val uiState        by feedViewModel.uiState.collectAsStateWithLifecycle<FeedUiState>()
    val scrollBehavior  = TopAppBarDefaults.pinnedScrollBehavior()

    // Auto-reload when resuming from ShareWithTeammates
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.currentStateFlow.collect { state ->
            if (state == Lifecycle.State.RESUMED) {
                feedViewModel.refreshIfCacheInvalidated()
            }
        }
    }

    // Dialogs and deferred navigation — outside LazyColumn so they float above the list
    if (uiState is FeedUiState.Success) {
        val state = uiState as FeedUiState.Success

        LaunchedEffect(state.pendingShareId) {
            state.pendingShareId?.let { id ->
                feedViewModel.onShareNavigated()
                onShareVisualization(id)
            }
        }

        state.deleteDialogForId?.let { vizId ->
            DeleteForEveryoneDialog(
                onDismiss = { feedViewModel.onDismissDialog() },
                onConfirm = { feedViewModel.onConfirmDeleteForEveryone(vizId) }
            )
        }

        state.hideDialogForId?.let { vizId ->
            DeleteForMeDialog(
                onDismiss = { feedViewModel.onDismissDialog() },
                onConfirm = { feedViewModel.onConfirmHideForMe(vizId) }
            )
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar   = {
            FeedTopBar(
                scrollBehavior   = scrollBehavior,
                selectedFilter   = (uiState as? FeedUiState.Success)?.selectedFilter ?: VisualizationFilter.ALL,
                onFilterSelected = { feedViewModel.onFilterChange(it) },
                onSearchClick    = { feedViewModel.toggleSearch() }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = (uiState as? FeedUiState.Success)?.isRefreshing == true,
            onRefresh    = { feedViewModel.loadData(forceRefresh = true) },
            modifier     = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())
        ) {
            when (val state = uiState) {

                is FeedUiState.Loading -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 22.dp)
                    ) {
                        items(3) { SkeletonFeedCard() }
                    }
                }

                is FeedUiState.Error -> {
                    Text(text = stringResource(state.message), modifier = Modifier.align(Alignment.Center))
                }

                is FeedUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier            = Modifier.fillMaxSize().padding(horizontal = 20.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(22.dp))
                            if (state.isSearching) {
                                SearchSection(
                                    text         = state.searchText,
                                    onTextChange = { feedViewModel.onSearchTextChange(it) }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (state.items.isEmpty()) {
                            item {
                                Text(
                                    text      = stringResource(R.string.error_viz_not_found),
                                    modifier  = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(items = state.items, key = { it.card.id }) { feedItem ->
                                FeedCard(
                                    item                = feedItem.card,
                                    chart               = feedItem.chart,
                                    currentUserID       = state.currentUserID,
                                    isChartLoading      = feedItem.isChartLoading,
                                    onLoadChartRequest  = { feedViewModel.loadChartForCard(feedItem.card) },
                                    permissions         = state.permissionsMap[feedItem.card.id] ?: VisualizationPermissions(
                                        UserType.CONSUMER,
                                        "",
                                        ""
                                    ),
                                    isMenuOpen          = state.menuOpenForId == feedItem.card.id,
                                    onClick             = { onVisualizationClick(feedItem.card.id) },
                                    onMenuOpen          = { feedViewModel.onMenuOpen(feedItem.card.id) },
                                    onMenuDismiss       = { feedViewModel.onMenuDismiss() },
                                    onDeleteForEveryone = { feedViewModel.onRequestDeleteForEveryone(feedItem.card.id) },
                                    onHideForMe         = { feedViewModel.onRequestHideForMe(feedItem.card.id) },
                                    onShare             = { feedViewModel.onRequestShare(feedItem.card.id) }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }

                else -> {}
            }
        }
    }
}
