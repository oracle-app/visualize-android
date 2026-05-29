package com.oracle.visualize.presentation.screens.threadsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    onBackClick: () -> Unit,
    onCropClick: () -> Unit,
    image: String? = ""
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(visualizationId) {
        viewModel.loadThreads(visualizationId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                if (uiState.replyingToCommentId != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 12.dp,
                                top = 8.dp,
                                bottom = 4.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.reply_to,
                                uiState.replyingToAuthorName ?: ""
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { viewModel.cancelReply() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = stringResource(R.string.cancel_reply),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                AddNoteBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    hint = if (uiState.replyingToCommentId != null) {
                        stringResource(R.string.write_reply)
                    } else {
                        stringResource(R.string.start_thread)
                    },
                    image = image,
                    onSendClick = { content ->
                        val replyingToCommentId = uiState.replyingToCommentId
                        when {
                            replyingToCommentId != null -> {
                                viewModel.createThread(
                                    visualizationId = visualizationId,
                                    commentId = replyingToCommentId,
                                    content = content
                                )
                            }
                            image != null -> {
                                viewModel.createCommentWithSnip(visualizationId, content, image)
                            }
                            else -> {
                                viewModel.createComment(
                                    visualizationId = visualizationId,
                                    content = content
                                )
                            }
                        }
                    },
                    onCropClick = onCropClick
                )
            }
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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    val errorMessage = uiState.errorMessage ?: R.string.error_unknown_retry

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(errorMessage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                uiState.comments.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_threads_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
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
                                currentUserId = uiState.currentUserId,
                                isCurrentUser = comment.authorID == uiState.currentUserId,
                                onReplyClick = {
                                    viewModel.startReply(
                                        commentId = comment.id,
                                        authorName = comment.authorName
                                    )
                                },
                                onDeleteClick = {
                                    viewModel.deleteComment(
                                        visualizationId = visualizationId,
                                        commentId = comment.id
                                    )
                                },
                                onDeleteThreadClick = { threadId ->
                                    viewModel.deleteThread(
                                        visualizationId = visualizationId,
                                        commentId = comment.id,
                                        threadId = threadId
                                    )
                                }
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
    }
}
