package com.oracle.visualize.presentation.screens.threadsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.screens.threadsScreen.components.AddNoteBar
import com.oracle.visualize.presentation.screens.threadsScreen.components.ThreadsTopBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.threadsScreen.components.CommentCard

/**
 * Screen that displays the discussion threads of a selected visualization.
 *
 * Shows the visualization title, thread cards, comments timeline,
 * and the note input bar.
 *
 * @param visualizationId ID of the visualization whose threads are displayed (still not implemented).
 * @param modifier Modifier for the screen layout.
 * @param viewModel The [ThreadsViewModel] that manages the screen state.
 */

@Composable
fun ThreadsPage(
    visualizationId: String,
    modifier: Modifier = Modifier,
    viewModel: ThreadsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(visualizationId) {
        viewModel.loadThreads(visualizationId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AddNoteBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                onSendClick = { content ->
                    viewModel.createComment(
                        visualizationId = visualizationId,
                        content = content
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            ThreadsTopBar(
                title = uiState.visualizationTitle,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.comments) { comment ->
                    CommentCard(
                        comment = comment,
                        isCurrentUser = comment.authorId == uiState.currentUserId
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.no_threads),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
