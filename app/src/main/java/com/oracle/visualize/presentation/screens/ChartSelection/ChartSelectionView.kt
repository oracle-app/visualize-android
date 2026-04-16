package com.oracle.visualize.presentation.screens.ChartSelection

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.presentation.screens.ChartSelection.components.ChartCard
import com.oracle.visualize.ui.theme.*

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose visualization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor
                )
            )
        },
        containerColor = ScreenBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealPrimary)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Your Visualizations Are Ready!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "We've generated several charts based on your dataset.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "Choose the chart that best represents the insights you want to share.",
                modifier = Modifier.padding(16.dp),
                color = TextGray,
                fontSize = 14.sp
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.charts) { selection ->
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

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* TODO: Post to personal feed logic */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeButton),
                    shape = RoundedCornerShape(8.dp),
                    enabled = viewModel.hasSelections()
                ) {
                    Text("Post to personal feed", color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeButton),
                    shape = RoundedCornerShape(8.dp),
                    enabled = viewModel.hasSelections()
                ) {
                    Text("Share and post", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }

    // Edit Title Dialog
    if (showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Edit Chart Title", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    label = { Text("New Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showEditDialog?.let { viewModel.updateChartTitle(it, tempTitle) }
                    showEditDialog = null
                }) {
                    Text("Confirm", color = TealPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Unsaved Changes Dialog (Example usage)
    if (uiState.isUnsavedChangesDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.showUnsavedChangesDialog(false) },
            title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold) },
            text = { Text("You have unsaved changes. Are you sure you want to leave?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.showUnsavedChangesDialog(false)
                    onBack()
                }) {
                    Text("Leave", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showUnsavedChangesDialog(false) }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}
