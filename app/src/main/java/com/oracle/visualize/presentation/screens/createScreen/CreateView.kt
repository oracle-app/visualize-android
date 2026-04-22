<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
package com.oracle.visualize.presentation.screens.createschart

========
package com.oracle.visualize.presentation.screens.createScreen
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
========
import androidx.compose.foundation.verticalScroll
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
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
<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
import com.oracle.visualize.presentation.screens.createschart.components.FileStatusItem
import com.oracle.visualize.ui.theme.*
========
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.CreateUiState
import com.oracle.visualize.presentation.screens.createScreen.components.FileStatusItem
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt

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

    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val descriptionText = when (uiState) {
<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
                is CreateChartUiState.Success -> "Your dataset is ready! Generate visualizations to explore your data."
                is CreateChartUiState.Uploading -> "Uploading your dataset..."
                else -> "Upload a dataset and we'll generate the best visualizations to help you understand your data."
========
                is CreateUiState.Success -> stringResource(R.string.create_description_success)
                is CreateUiState.Uploading -> stringResource(R.string.create_description_uploading)
                else -> stringResource(R.string.create_description_idle)
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
            }

            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
            // File Picker Area / Status Area
            if (uiState is CreateChartUiState.Idle) {
========
            if (uiState is CreateUiState.Idle) {
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
                DashedSelector(
                    onClick = { launcher.launch("*/*") }
                )
            } else {
                FileStatusSection(uiState, viewModel)
            }

            Spacer(modifier = Modifier.height(32.dp))

<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
            // Dataset Format Requirements
            if (uiState !is CreateChartUiState.Success) {
========
            if (uiState !is CreateUiState.Success) {
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
                DatasetRequirementsSection()
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState is CreateChartUiState.Success) {
                Button(
                    onClick = onNavigateToSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        stringResource(R.string.create_generate_button),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Visual selector for file picking with dashed border.
 */
@Composable
fun DashedSelector(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
        val bordercolor = MaterialTheme.colorScheme.outlineVariant
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = bordercolor,
                style = stroke,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
        val teal = MaterialTheme.colorScheme.onSurfaceVariant
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val iconSize = size.width
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
                text = stringResource(R.string.dashed_selector_choose_file),
                fontWeight = FontWeight.Medium,
                color = teal
            )
<<<<<<<< HEAD:app/src/main/java/com/oracle/visualize/presentation/screens/createschart/CreateView.kt
            Text("Maximum file size: 100 MB", fontSize = 12.sp, color = TextGray)
            Text("Only one dataset can be uploaded.", fontSize = 12.sp, color = TextGray)
========
            Text(stringResource(R.string.dashed_selector_min_size), fontSize = 12.sp, color = bordercolor)
            Text(stringResource(R.string.dashed_selector_one_dataset), fontSize = 12.sp, color = bordercolor)
>>>>>>>> origin/develop:app/src/main/java/com/oracle/visualize/presentation/screens/createScreen/CreateView.kt
        }
    }
}

/**
 * Displays the current status of the file (Uploading, Success, or Error).
 */
@Composable
fun FileStatusSection(uiState: CreateChartUiState, viewModel: CreateViewModel) {
    when (val state = uiState) {
        is CreateChartUiState.Uploading -> {
            FileStatusItem(
                fileName = state.fileName,
                fileSize = state.fileSize,
                progress = state.progress,
                onCancel = { viewModel.resetState() }
            )
        }
        is CreateChartUiState.Success -> {
            FileStatusItem(
                fileName = state.fileName,
                fileSize = state.fileSize,
                isSuccess = true,
                onDelete = { viewModel.resetState() }
            )
        }
        is CreateChartUiState.Error -> {
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

/**
 * Explains the required format for datasets.
 */
@Composable
fun DatasetRequirementsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.dataset_requirements_title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
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
            color = MaterialTheme.colorScheme.onBackground,
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

/**
 * Visual table example for guidance.
 */
@Composable
fun TableExampleComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.table_header_date), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(stringResource(R.string.table_header_product), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(stringResource(R.string.table_header_sales), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(stringResource(R.string.table_header_region), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onPrimary)

        val rows = listOf(
            listOf("Jan", "A", "120", "North"),
            listOf("Jan", "B", "95", "South"),
            listOf("Feb", "A", "150", "North")
        )

        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Text(cell, modifier = Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}