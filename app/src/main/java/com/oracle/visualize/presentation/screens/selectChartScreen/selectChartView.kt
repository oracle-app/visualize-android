package com.oracle.visualize.presentation.screens.selectchartscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: SelectChartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var chartIdToEdit by remember { mutableStateOf<String?>(null) }
    var tempTitle by remember { mutableStateOf("") }

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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                                    tempTitle = selection.visualization.title
                                    chartIdToEdit = selection.visualization.id
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

                // Edit Title Dialog
                if (chartIdToEdit != null) {
                    AlertDialog(
                        onDismissRequest = { chartIdToEdit = null },
                        title = { Text(stringResource(R.string.dialog_edit_title)) },
                        text = {
                            OutlinedTextField(
                                value = tempTitle,
                                onValueChange = { tempTitle = it },
                                label = { Text(stringResource(R.string.dialog_edit_title_label)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                chartIdToEdit?.let { viewModel.updateChartTitle(it, tempTitle) }
                                chartIdToEdit = null
                            }) {
                                Text(stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { chartIdToEdit = null }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                // Unsaved Changes Dialog
                if (state.isUnsavedChangesDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
                        title = { Text(stringResource(R.string.dialog_unsaved_title)) },
                        text = { Text(stringResource(R.string.dialog_unsaved_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.showUnsavedChangesDialog(false)
                                onBack()
                            }) {
                                Text(stringResource(R.string.dialog_leave), color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}
