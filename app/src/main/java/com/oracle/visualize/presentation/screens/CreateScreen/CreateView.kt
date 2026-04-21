package com.oracle.visualize.presentation.screens.CreateScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.domain.models.CreateUiState
import com.oracle.visualize.presentation.screens.CreateScreen.components.FileStatusItem
import com.oracle.visualize.ui.theme.*

/**
 * Screen for uploading a dataset to create new visualizations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePage(
    modifier: Modifier = Modifier,
    viewModel: CreateViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Using GetContent for broader support of cloud providers like Google Drive
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.onFileSelected(uri, context)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Create Data Visualizations", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor
                )
            )
        },
        containerColor = ScreenBackground
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val descriptionText = when (uiState) {
                is CreateUiState.Success -> "Your dataset is ready! Generate visualizations to explore your data."
                is CreateUiState.Uploading -> "Uploading your dataset..."
                else -> "Upload a dataset and we'll generate the best visualizations to help you understand your data."
            }

            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // File Picker Area / Status Area
            if (uiState is CreateUiState.Idle) {
                DashedSelector(
                    onClick = {
                        // Use a broad filter to ensure Google Drive appears
                        launcher.launch("*/*")
                    }
                )
            } else {
                FileStatusSection(uiState, viewModel)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dataset Format Requirements
            if (uiState !is CreateUiState.Success) {
                DatasetRequirementsSection()
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState is CreateUiState.Success) {
                Button(
                    onClick = { /* TODO: Navigation */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeButton),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Generate Visualizations", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DashedSelector(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = BorderGray,
                style = stroke,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Custom Arrow Icon
            Canvas(modifier = Modifier.size(48.dp)) {
                val iconSize = size.width
                val teal = TealPrimary
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(iconSize / 2, iconSize * 0.15f)
                    lineTo(iconSize * 0.8f, iconSize * 0.45f)
                    lineTo(iconSize * 0.2f, iconSize * 0.45f)
                    close()
                }
                drawPath(path, teal)
                drawRect(
                    color = teal,
                    topLeft = Offset(iconSize * 0.42f, iconSize * 0.45f),
                    size = Size(iconSize * 0.16f, iconSize * 0.25f)
                )
                drawRect(
                    color = teal,
                    topLeft = Offset(iconSize * 0.2f, iconSize * 0.75f),
                    size = Size(iconSize * 0.6f, iconSize * 0.08f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose a .xlsx or .csv file.", 
                fontWeight = FontWeight.Medium,
                color = TealPrimary
            )
            Text("Maximum file size: 100 MB", fontSize = 12.sp, color = TextGray)
            Text("Only one dataset can be uploaded.", fontSize = 12.sp, color = TextGray)
        }
    }
}

@Composable
fun FileStatusSection(uiState: CreateUiState, viewModel: CreateViewModel) {
    when (val state = uiState) {
        is CreateUiState.Uploading -> {
            FileStatusItem(
                fileName = state.fileName,
                fileSize = state.fileSize,
                progress = state.progress,
                onCancel = { viewModel.resetState() }
            )
        }
        is CreateUiState.Success -> {
            FileStatusItem(
                fileName = state.fileName,
                fileSize = state.fileSize,
                isSuccess = true,
                onDelete = { viewModel.resetState() }
            )
        }
        is CreateUiState.Error -> {
            FileStatusItem(
                fileName = state.fileName ?: "Error",
                fileSize = state.fileSize ?: "",
                errorMessage = state.message,
                onCancel = { viewModel.resetState() }
            )
        }
        else -> {}
    }
}

@Composable
fun DatasetRequirementsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Dataset Format Requirements", 
            fontWeight = FontWeight.Bold, 
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Upload a table-formatted dataset with column headers in the first row.",
            color = TextGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Example", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        
        TableExampleComponent()
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Each row should represent a single data entry. Avoid empty rows or merged cells.",
            fontSize = 14.sp,
            color = TextGray
        )
    }
}

@Composable
fun TableExampleComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE9F1F2), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Product", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Sales", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Region", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderGray.copy(alpha = 0.5f))
        
        val rows = listOf(
            listOf("Jan", "A", "120", "North"),
            listOf("Jan", "B", "95", "South"),
            listOf("Feb", "A", "150", "North")
        )
        
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Text(cell, modifier = Modifier.weight(1f), fontSize = 12.sp)
                }
            }
        }
    }
}
