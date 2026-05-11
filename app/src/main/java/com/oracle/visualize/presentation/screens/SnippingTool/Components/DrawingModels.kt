package com.oracle.visualize.presentation.screens.SnippingTool.Components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

enum class DrawingTool {
    PEN, ERASER, SHAPE, TEXT
}

enum class ShapeType {
    CIRCLE, RECTANGLE, LINE, TRIANGLE
}

sealed class DrawElement {
    data class FreePath(
        val points: List<Offset>,
        val color: Color,
        val strokeWidth: Float
    ) : DrawElement()

    data class Shape(
        val type: ShapeType,
        val start: Offset,
        val end: Offset,
        val color: Color,
        val strokeWidth: Float
    ) : DrawElement()

    data class Text(
        val text: String,
        val position: Offset,
        val color: Color,
        val fontSize: TextUnit
    ) : DrawElement()
}
