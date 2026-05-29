package com.oracle.visualize.presentation.screens.selectChartScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.text.style.TextAlign
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.selectChartScreen.components.ChartCard

/**
 * Screen for selecting visualizations to post or share.
 *
 * @param onNavigateBack Callback to navigate back.
 * @param onNavigateToShare Callback to navigate to the share screen.
 * @param onNavigateToFeed Callback to navigate to the feed screen.
 * @param viewModel The [SelectChartViewModel] that manages chart selections.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartSelectionPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToShare: (taskId: String, selectedChartIndices: List<Int>, customTitles: List<String>) -> Unit,
    onNavigateToFeed: () -> Unit,
    viewModel: SelectChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf<String?>(null) }
    var tempTitle by remember { mutableStateOf("") }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Intercept the system back gesture to show unsaved-changes dialog when needed
    BackHandler {
        val state = uiState
        if (state is ChartSelectionUiState.Success && state.hasTitleChanges) {
            viewModel.showUnsavedChangesDialog(true)
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    IconButton(
                        onClick = {
                            val state = uiState
                            if (state is ChartSelectionUiState.Success && state.hasTitleChanges) {
                                viewModel.showUnsavedChangesDialog(true)
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            val state = uiState
            if (state is ChartSelectionUiState.Success) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val context = androidx.compose.ui.platform.LocalContext.current

                        Button(
                            onClick = {
                                viewModel.postSelectedChartsToPersonalFeed(
                                    onSuccess = onNavigateToFeed,
                                    onError = { errorMsg ->
                                        android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = viewModel.hasSelections()
                        ) {
                            Text(
                                text = stringResource(R.string.chart_selection_post_personal),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                val selected = state.charts.filter { it.isSelected }
                                val indices = selected.map { it.chartIndex }
                                val titles = selected.map { it.customTitle }
                                onNavigateToShare(viewModel.taskId, indices, titles)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = viewModel.hasSelections()
                        ) {
                            Text(
                                text = stringResource(R.string.chart_selection_share_and_post),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ChartSelectionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is ChartSelectionUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Unable to Load Charts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.fetchOverview() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Retry",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                is ChartSelectionUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
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
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = stringResource(R.string.chart_selection_ready_subtitle),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        item {
                            Text(
                                text = stringResource(R.string.chart_selection_prompt),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        }

                        items(
                            items = state.charts,
                            key = { it.id }
                        ) { selection ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ChartCard(
                                    chart = selection.chart,
                                    title = selection.customTitle,
                                    isSelected = selection.isSelected,
                                    onSelect = { viewModel.toggleSelection(selection.id) },
                                    onEditTitle = {
                                        tempTitle = selection.customTitle
                                        showEditDialog = selection.id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        text = stringResource(R.string.dialog_edit_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        label = { Text(text = stringResource(R.string.dialog_edit_title_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showEditDialog?.let { id -> viewModel.updateChartTitle(id, tempTitle) }
                            showEditDialog = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        if (uiState is ChartSelectionUiState.Success && (uiState as ChartSelectionUiState.Success).isUnsavedChangesDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        text = stringResource(R.string.dialog_unsaved_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.dialog_unsaved_message),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showUnsavedChangesDialog(false)
                            onNavigateBack()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_leave),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
