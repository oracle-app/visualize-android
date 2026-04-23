package com.oracle.visualize.presentation.screens.createschart

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.createschart.components.FileStatusItem

/**
 * CreatePage is the entry point for users to upload their datasets.
 * Fully compatible with Material Design 3, Dark Mode and localized strings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePage(
    modifier: Modifier = Modifier,
    viewModel: CreateViewModel = viewModel(),
    onNavigateToSelection: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

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
                        text = stringResource(R.string.create_top_bar_title), 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val descriptionText = when (uiState) {
                is CreateChartUiState.Success -> stringResource(R.string.create_description_success)
                is CreateChartUiState.Uploading -> stringResource(R.string.create_description_uploading)
                else -> stringResource(R.string.create_description_idle)
            }

            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            if (uiState is CreateChartUiState.Idle) {
                DashedSelector(
                    onClick = { launcher.launch("*/*") }
                )
            } else {
                FileStatusSection(uiState, viewModel)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState !is CreateChartUiState.Success) {
                DatasetRequirementsSection()
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState is CreateChartUiState.Success) {
                Button(
                    onClick = onNavigateToSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create_generate_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DashedSelector(onClick: () -> Unit) {
    val borderColor = MaterialTheme.colorScheme.outline
    val iconColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = borderColor,
                style = stroke,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val iconSize = size.width
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(iconSize / 2, iconSize * 0.15f)
                    lineTo(iconSize * 0.8f, iconSize * 0.45f)
                    lineTo(iconSize * 0.2f, iconSize * 0.45f)
                    close()
                }
                drawPath(path, iconColor)
                drawRect(
                    color = iconColor,
                    topLeft = Offset(iconSize * 0.42f, iconSize * 0.45f),
                    size = Size(iconSize * 0.16f, iconSize * 0.25f)
                )
                drawRect(
                    color = iconColor,
                    topLeft = Offset(iconSize * 0.2f, iconSize * 0.75f),
                    size = Size(iconSize * 0.6f, iconSize * 0.08f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.dashed_selector_choose_file), 
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.dashed_selector_max_size), 
                fontSize = 12.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.dashed_selector_one_dataset), 
                fontSize = 12.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FileStatusSection(uiState: CreateChartUiState, viewModel: CreateViewModel) {
    when (uiState) {
        is CreateChartUiState.Uploading -> {
            FileStatusItem(
                fileName = uiState.fileName,
                fileSize = uiState.fileSize,
                progress = uiState.progress,
                onCancel = { viewModel.resetState() }
            )
        }
        is CreateChartUiState.Success -> {
            FileStatusItem(
                fileName = uiState.fileName,
                fileSize = uiState.fileSize,
                isSuccess = true,
                onDelete = { viewModel.resetState() }
            )
        }
        is CreateChartUiState.Error -> {
            FileStatusItem(
                fileName = uiState.fileName ?: stringResource(R.string.error_label),
                fileSize = uiState.fileSize ?: "",
                errorMessage = uiState.message,
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
            text = stringResource(R.string.dataset_requirements_title), 
            fontWeight = FontWeight.Bold, 
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.dataset_requirements_body),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.dataset_requirements_example_label), 
            fontSize = 12.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        TableExampleComponent()
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.dataset_requirements_footer),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TableExampleComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.table_header_date), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.table_header_product), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.table_header_sales), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.table_header_region), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
        
        val jan = stringResource(R.string.example_val_jan)
        val feb = stringResource(R.string.example_val_feb)
        val a = stringResource(R.string.example_val_a)
        val b = stringResource(R.string.example_val_b)
        val north = stringResource(R.string.example_val_north)
        val south = stringResource(R.string.example_val_south)

        val rows = listOf(
            listOf(jan, a, "120", north),
            listOf(jan, b, "95", south),
            listOf(feb, a, "150", north)
        )
        
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Text(cell, modifier = Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
