package com.oracle.visualize.ui.screens.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.ui.screens.create.components.FileStatusItem
import com.oracle.visualize.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
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
                    containerColor = TealLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .then(
                        if (uiState is CreateUiState.Idle) {
                            Modifier
                                .height(180.dp)
                                .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                                .clickable {
                                    launcher.launch(
                                        arrayOf(
                                            "text/comma-separated-values",
                                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                            "text/csv"
                                        )
                                    )
                                }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is CreateUiState.Idle -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Choose a .xlsx or .csv file.", fontWeight = FontWeight.Medium)
                            Text("Minimum file size: 100 MB", fontSize = 12.sp, color = TextGray)
                            Text("Only one dataset can be uploaded.", fontSize = 12.sp, color = TextGray)
                        }
                    }
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
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dataset Format Requirements
            if (uiState !is CreateUiState.Success) {
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
                    TableExample()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Each row should represent a single data entry. Avoid empty rows or merged cells.",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState is CreateUiState.Success) {
                Button(
                    onClick = { /* Navigate to next screen */ },
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
fun TableExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TealLight.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Product", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Sales", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Region", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderGray.copy(alpha = 0.5f))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Jan", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("A", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("120", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("North", modifier = Modifier.weight(1f), fontSize = 12.sp)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Feb", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("B", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("95", modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text("South", modifier = Modifier.weight(1f), fontSize = 12.sp)
        }
    }
}
