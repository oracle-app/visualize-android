package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.oracle.visualize.presentation.screens.feedScreen.components.DeleteForEveryoneDialog
import com.oracle.visualize.presentation.screens.feedScreen.components.DeleteForMeDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = hiltViewModel(),
    onVisualizationClick: (String) -> Unit = {},
    onShareVisualization: (String) -> Unit = {}
) {
    val uiState        by feedViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior  = TopAppBarDefaults.pinnedScrollBehavior()

    // ─── Delete dialogs ───────────────────────────────────────────────────────
    when (val dialog = uiState.deleteDialogState) {
        is DeleteDialogState.ShowDeleteForMe -> {
            DeleteForMeDialog(
                onDismiss = { feedViewModel.onDismissDeleteDialog() },
                onConfirm = { feedViewModel.onConfirmDeleteForMe(dialog.visualizationId) }
            )
        }
        is DeleteDialogState.ShowDeleteForEveryone -> {
            DeleteForEveryoneDialog(
                onDismiss = { feedViewModel.onDismissDeleteDialog() },
                onConfirm = { feedViewModel.onConfirmDeleteForEveryone(dialog.visualizationId) }
            )
        }
        is DeleteDialogState.Hidden -> Unit
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar   = {
            FeedTopBar(
                scrollBehavior   = scrollBehavior,
                selectedFilter   = uiState.selectedFilter,
                onFilterSelected = { feedViewModel.onFilterChange(it) }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
            onRefresh    = { feedViewModel.loadData(forceRefresh = true) },
            modifier     = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text     = uiState.errorMessage!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.items.isEmpty() && !uiState.isLoading -> {
                    Text(
                        text     = "No visualizations found.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(22.dp))
                            SearchSection(
                                text         = uiState.searchText,
                                onTextChange = { feedViewModel.onSearchTextChange(it) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(
                            items = uiState.items,
                            key   = { it.id }
                        ) { item ->
                            FeedCard(
                                item                = item,
                                currentUserID       = feedViewModel.currentUserID,
                                isMenuOpen          = uiState.openMenuForId == item.id,
                                onClick             = { onVisualizationClick(item.id) },
                                onOpenMenu          = { feedViewModel.onOpenMenu(item.id) },
                                onDismissMenu       = { feedViewModel.onDismissMenu() },
                                onShare             = {
                                    feedViewModel.onShareSelected()
                                    onShareVisualization(item.id)
                                },
                                onDeleteForMe       = { feedViewModel.onRequestDeleteForMe(item.id) },
                                onDeleteForEveryone = { feedViewModel.onRequestDeleteForEveryone(item.id) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}
