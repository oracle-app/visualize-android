package com.oracle.visualize.presentation.screens.SnippingTool

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import com.oracle.visualize.presentation.screens.SnippingTool.Components.DrawElement
import com.oracle.visualize.presentation.screens.SnippingTool.Components.DrawingTool
import com.oracle.visualize.presentation.screens.SnippingTool.Components.ShapeType

data class SnippingToolUiState(
    val elements: List<DrawElement> = emptyList(),
    val redoStack: List<DrawElement> = emptyList(),
    val selectedTool: DrawingTool = DrawingTool.PEN,
    val selectedShape: ShapeType = ShapeType.RECTANGLE,
    val selectedColor: Color = Color.Red,
    val strokeWidth: Float = 4f,
    val isDrawingMode: Boolean = false,
    val cropRect: IntRect = IntRect(0, 0, 0, 0),
    val isCroppingMode: Boolean = false
)
