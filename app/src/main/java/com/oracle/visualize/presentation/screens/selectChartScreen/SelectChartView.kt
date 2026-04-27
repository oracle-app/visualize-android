package com.oracle.visualize.presentation.screens.selectChartScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.oracle.visualize.presentation.screens.selectChartScreen.components.ChartCard
import com.oracle.visualize.ui.theme.ErrorRed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartSelectionPage(
    onBack: () -> Unit,
    onNavigateToShare: () -> Unit,
    viewModel: SelectChartViewModel = viewModel()
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
                        text = stringResource(R.string.chart_selection_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.icon_back))
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is ChartSelectionUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is ChartSelectionUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = ErrorRed)
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
                                text = stringResource(R.string.chart_selection_ready_title),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = stringResource(R.string.chart_selection_ready_subtitle),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.chart_selection_prompt),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = viewModel.hasSelections()
                            ) {
                                Text(stringResource(R.string.chart_selection_post_personal), color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onNavigateToShare,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = viewModel.hasSelections()
                            ) {
                                Text(stringResource(R.string.chart_selection_share_and_post), color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (showEditDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showEditDialog = null },
                        title = { Text(stringResource(R.string.dialog_edit_title), fontWeight = FontWeight.Bold) },
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
                                showEditDialog?.let { viewModel.updateChartTitle(it, tempTitle) }
                                showEditDialog = null
                            }) {
                                Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditDialog = null }) {
                                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                }

                if (state.isUnsavedChangesDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
                        title = { Text(stringResource(R.string.dialog_unsaved_title), fontWeight = FontWeight.Bold) },
                        text = { Text(stringResource(R.string.dialog_unsaved_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.showUnsavedChangesDialog(false)
                                onBack()
                            }) {
                                Text(stringResource(R.string.dialog_leave), color = ErrorRed)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                }
            }
        }
    }
}