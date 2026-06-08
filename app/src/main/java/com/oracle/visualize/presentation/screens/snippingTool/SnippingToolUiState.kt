package com.oracle.visualize.presentation.screens.snippingTool

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.TextUnit
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationFullScreen
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawElement
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawingTool
import com.oracle.visualize.presentation.screens.snippingTool.components.ShapeType
import com.oracle.visualize.ui.theme.ChartPalette

data class SnippingToolUiState(
    val elements: List<DrawElement> = emptyList(),
    val redoStack: List<DrawElement> = emptyList(),
    val selectedTool: DrawingTool? = null,
    val selectedShape: ShapeType = ShapeType.RECTANGLE,
    val selectedColor: Color = Color.Red,
    val strokeWidth: Float = 4f,
    val isDrawingMode: Boolean = false,
    val isItalics: Boolean = false,
    val fontSize: Float = 16f,
    val cropRect: IntRect = IntRect(0, 0, 0, 0),
    val isCroppingMode: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val showCancelDialog: Boolean = false,
    val isTransformable: Boolean = true,

    val isLoading: Boolean = false,
    val visualization: VisualizationFullScreen? = null,
    val chart: Chart<*>? = null,
    val chartColorTheme: ChartPalette = ChartPalette.THEME1,
    val errorMessage: Int? = null
)
