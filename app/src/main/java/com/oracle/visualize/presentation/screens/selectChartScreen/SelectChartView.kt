package com.oracle.visualize.presentation.screens.selectchartscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ChartSelectionUiState
import com.oracle.visualize.presentation.screens.selectchartscreen.components.ChartCard

/**
 * Screen that displays suggested charts as interactive cards.
 * Users can select one or more charts and edit their titles before publishing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectChartView(
    onBack: () -> Unit,
    onNavigateToShare: () -> Unit,
    viewModel: SelectChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.chart_selection_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.hasSelections()) {
                            viewModel.showUnsavedChangesDialog(true)
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
    ) { paddingValues ->
        // Handle UI states using when expression as per sealed interface standard
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is ChartSelectionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ChartSelectionUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ChartSelectionUiState.Success -> {
                    ChartSelectionContent(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToShare = onNavigateToShare
                    )
                    
                    // Dialogs handled within the Success state context
                    if (state.editingChartId != null) {
                        EditTitleDialog(
                            title = state.editingTitle,
                            onTitleChange = { viewModel.onEditingTitleChanged(it) },
                            onConfirm = { viewModel.confirmTitleEdit() },
                            onDismiss = { viewModel.cancelTitleEdit() }
                        )
                    }

                    if (state.isUnsavedChangesDialogVisible) {
                        UnsavedChangesDialog(
                            onConfirm = {
                                viewModel.showUnsavedChangesDialog(false)
                                onBack()
                            },
                            onDismiss = { viewModel.showUnsavedChangesDialog(false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartSelectionContent(
    state: ChartSelectionUiState.Success,
    viewModel: SelectChartViewModel,
    onNavigateToShare: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.chart_selection_ready_title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.chart_selection_ready_subtitle),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Prompt Section
        item {
            Text(
                text = stringResource(R.string.chart_selection_prompt),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Interactive Chart Cards
        items(state.charts) { selection ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ChartCard(
                    visualization = selection.visualization,
                    isSelected = selection.isSelected,
                    onSelect = { viewModel.toggleSelection(selection.visualization.id) },
                    onEditTitle = {
                        viewModel.startEditingTitle(
                            selection.visualization.id,
                            selection.visualization.title
                        )
                    }
                )
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Logic for personal feed */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp),
                    enabled = viewModel.hasSelections()
                ) {
                    Text(
                        text = stringResource(R.string.chart_selection_post_personal),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onNavigateToShare,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp),
                    enabled = viewModel.hasSelections()
                ) {
                    Text(
                        text = stringResource(R.string.chart_selection_share_and_post),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun EditTitleDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.dialog_edit_title_label)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun UnsavedChangesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_unsaved_title)) },
        text = { Text(stringResource(R.string.dialog_unsaved_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_leave), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
