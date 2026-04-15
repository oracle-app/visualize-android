package com.oracle.visualize.ui.screens.chartselection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.data.model.Chart
import com.oracle.visualize.ui.screens.chartselection.components.ChartCard
import com.oracle.visualize.ui.screens.chartselection.components.ChartSelectionBottomActions
import com.oracle.visualize.ui.screens.chartselection.components.ChartSelectionTopBar
import com.oracle.visualize.ui.screens.chartselection.components.EditTitleDialog
import com.oracle.visualize.ui.screens.chartselection.components.HeaderMessage

/**
 * Screen that allows users to select and edit chart visualizations.
 */
@Composable
fun ChartSelectionScreen(
    chartViewModel: ChartSelectionViewModel = viewModel()
) {
    val uiState by chartViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ChartSelectionTopBar(
                onBackClick = { /* Handle back action */ },
                onForwardClick = { /* Handle forward action */ }
            )
        },
        bottomBar = {
            ChartSelectionBottomActions()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HeaderMessage()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose the chart that best represents the insights you want to share.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChartList(
                uiState = uiState,
                onToggleSelection = { id -> chartViewModel.toggleSelection(id) },
                onEditClick = { chart -> chartViewModel.onEditClick(chart) }
            )
        }

        if (uiState.isEditDialogVisible) {
            EditTitleDialog(
                currentTitle = uiState.editingChart?.title ?: "",
                onDismiss = { chartViewModel.onDismissEditDialog() },
                onConfirm = { newTitle -> chartViewModel.onUpdateChartTitle(newTitle) }
            )
        }
    }
}

/**
 * Scrollable list of charts available for selection.
 */
@Composable
private fun ChartList(
    uiState: ChartSelectionUiState,
    onToggleSelection: (String) -> Unit,
    onEditClick: (Chart) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(uiState.charts) { chart ->
            ChartCard(
                chart = chart,
                onSelect = { onToggleSelection(chart.id) },
                onEdit = { onEditClick(chart) }
            )
        }
    }
}
