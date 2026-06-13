package com.oracle.visualize.presentation.screens.snipPreviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.threadsScreen.components.ThreadsTopBar

@Composable
fun SnipPreviewPage(
    visualizationId: String,
    snipUri: String,
    modifier: Modifier = Modifier,
    viewModel: SnipPreviewViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onShareCompleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.shareCompleted) {
        if (uiState.shareCompleted) {
            viewModel.consumeShareCompleted()
            onShareCompleted()
        }
    }

    if (uiState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isSharing) {
                    viewModel.dismissConfirmDialog()
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            title = { Text(stringResource(R.string.dialog_confirm_title)) },
            text = { Text(stringResource(R.string.dialog_confirm_message)) },
            confirmButton = {
                TextButton(
                    enabled = !uiState.isSharing,
                    onClick = {
                        viewModel.shareSnipAsThread(
                            visualizationId = visualizationId,
                            snipUri = snipUri
                        )
                    }
                ) {
                    Text(
                        text = if (uiState.isSharing) stringResource(R.string.dialog_confirm_yes_atl) else stringResource(R.string.dialog_confirm_yes),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !uiState.isSharing,
                    onClick = { viewModel.dismissConfirmDialog() }
                ) {
                    Text(
                        stringResource(R.string.dialog_confirm_no),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showConfirmDialog() },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.dialog_confirm_yes),
                    tint = MaterialTheme.colorScheme.onSecondary
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
                title = stringResource(R.string.preview),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.caption),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.share_insights),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.caption,
                    onValueChange = viewModel::onCaptionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    enabled = !uiState.isSharing,
                    placeholder = {
                        Text(stringResource(R.string.add_desc))
                    },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(R.string.preview_edited_vis),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                AsyncImage(
                    model = snipUri,
                    contentDescription = stringResource(R.string.edit_preview),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentScale = ContentScale.Fit
                )

                if (uiState.isSharing) {
                    Spacer(modifier = Modifier.height(12.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
