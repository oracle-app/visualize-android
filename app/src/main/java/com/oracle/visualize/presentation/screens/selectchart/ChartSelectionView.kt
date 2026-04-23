package com.oracle.visualize.presentation.screens.selectchart

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
import com.oracle.visualize.presentation.screens.selectchart.components.ChartCard
import com.oracle.visualize.ui.theme.*

/**
 * ChartSelectionPage allows users to pick which visualizations they want to post.
 * Uses Material 3 standards for scrolling and theming to support Dark Mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartSelectionPage(
    onBack: () -> Unit,
    onNavigateToShare: () -> Unit,
    viewModel: ChartSelectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf<String?>(null) }
    var tempTitle by remember { mutableStateOf("") }
    
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.choose_visualization),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (val state = uiState) {
            ChartSelectionUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                        .padding(paddingValues)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.visualizations_ready_title),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = stringResource(R.string.visualizations_ready_desc),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.select_insight_desc),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }

                    items(state.charts) { selection ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ChartCard(
                                visualization = selection.visualization,
                                isSelected = selection.isSelected,
                                onSelect = { viewModel.toggleSelection(selection.visualization.id) },
                                onEditTitle = {
                                    tempTitle = selection.visualization.title
                                    showEditDialog = selection.visualization.id
                                }
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = viewModel.hasSelections()
                            ) {
                                Text(
                                    text = stringResource(R.string.post_to_feed), 
                                    color = MaterialTheme.colorScheme.onSecondary, 
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = onNavigateToShare,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = viewModel.hasSelections()
                            ) {
                                Text(
                                    text = stringResource(R.string.share_and_post), 
                                    color = MaterialTheme.colorScheme.onSecondary, 
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (showEditDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showEditDialog = null },
                        title = { Text(stringResource(R.string.edit_chart_title), fontWeight = FontWeight.Bold) },
                        text = {
                            OutlinedTextField(
                                value = tempTitle,
                                onValueChange = { tempTitle = it },
                                label = { Text(stringResource(R.string.new_title_label)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showEditDialog?.let { viewModel.updateChartTitle(it, tempTitle) }
                                showEditDialog = null
                            }) {
                                Text(stringResource(R.string.confirm_btn), color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditDialog = null }) {
                                Text(stringResource(R.string.cancel_btn), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )
                }

                if (state.isUnsavedChangesDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
                        title = { Text(stringResource(R.string.unsaved_changes_title), fontWeight = FontWeight.Bold) },
                        text = { Text(stringResource(R.string.unsaved_changes_desc)) },
                        confirmButton = {
                            TextButton(onClick = { 
                                viewModel.showUnsavedChangesDialog(false)
                                onBack()
                            }) {
                                Text(stringResource(R.string.leave_btn), color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                                Text(stringResource(R.string.cancel_btn), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )
                }
            }
        }
    }
}
