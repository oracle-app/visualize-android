package com.oracle.visualize.presentation.screens.selectChartScreen

import androidx.activity.compose.BackHandler
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.selectChartScreen.components.ChartCard
import com.oracle.visualize.ui.theme.ErrorRed

/**
 * Screen for selecting visualizations to post or share.
 * @param onBack Callback to navigate back.
 * @param onNavigateToShare Callback to navigate to the share screen.
 * @param viewModel The [SelectChartViewModel] that manages chart selections.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartSelectionPage(
    onBack: () -> Unit,
    onNavigateToShare: () -> Unit,
    viewModel: SelectChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf<String?>(null) }
    var tempTitle by remember { mutableStateOf("") }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val FIGMA_BACKGROUND_COLOR = Color(0xFFE6EDEC)

    // Intercept the system back gesture to show unsaved-changes dialog when needed
    BackHandler {
        val state = uiState
        if (state is ChartSelectionUiState.Success && state.hasTitleChanges) {
            viewModel.showUnsavedChangesDialog(true)
        } else {
            onBack()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.chart_selection_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val state = uiState
                            if (state is ChartSelectionUiState.Success && state.hasTitleChanges) {
                                viewModel.showUnsavedChangesDialog(true)
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.icon_back),
                            tint = Color.Black
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FIGMA_BACKGROUND_COLOR,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            if (uiState is ChartSelectionUiState.Success) {
                Surface(
                    color = FIGMA_BACKGROUND_COLOR,
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
                        Button(
                            onClick = { /* TODO: Implement logic */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = viewModel.hasSelections()
                        ) {
                            Text(
                                text = stringResource(R.string.chart_selection_post_personal),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onNavigateToShare,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = viewModel.hasSelections()
                        ) {
                            Text(
                                text = stringResource(R.string.chart_selection_share_and_post),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = FIGMA_BACKGROUND_COLOR
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = ErrorRed)
                    }
                }
                is ChartSelectionUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // Instruction text
                        item {
                            Text(
                                text = stringResource(R.string.chart_selection_prompt),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        }

                        // Chart suggestions
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
                    }
                }
            }
        }

        // Dialog for editing chart titles
        if (showEditDialog != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                containerColor = MaterialTheme.colorScheme.onPrimary,
                title = {
                    Text(
                        text = stringResource(R.string.dialog_edit_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                            focusedLabelColor = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }

        // Dialog for unsaved title changes
        if (uiState is ChartSelectionUiState.Success && (uiState as ChartSelectionUiState.Success).isUnsavedChangesDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
                containerColor = MaterialTheme.colorScheme.onPrimary,
                title = {
                    Text(
                        text = stringResource(R.string.dialog_unsaved_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.dialog_unsaved_message),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showUnsavedChangesDialog(false)
                            onBack()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_leave),
                            color = ErrorRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    }
}
