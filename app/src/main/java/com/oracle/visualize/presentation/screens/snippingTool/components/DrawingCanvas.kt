package com.oracle.visualize.presentation.screens.snippingTool.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun DrawingCanvas(
    elements: List<DrawElement>,
    selectedTool: DrawingTool,
    selectedShape: ShapeType,
    selectedColor: Color,
    strokeWidth: Float,
    isDrawingMode: Boolean,
    onAddElement: (DrawElement) -> Unit,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    modifier: Modifier = Modifier
) {
    val currentPath = remember { Path() }

    // Incremented on every drag event to help redo/undo
    var pathVersion by remember { mutableStateOf(0) }

    var shapeStart by remember { mutableStateOf<Offset?>(null) }
    var shapeEnd by remember { mutableStateOf<Offset?>(null) }
    var textPosition by remember { mutableStateOf<Offset?>(null) }
    var textInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (isDrawingMode) Modifier
                        .pointerInput(selectedTool, selectedShape, selectedColor, strokeWidth) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    when (selectedTool) {
                                        DrawingTool.PEN, DrawingTool.ERASER -> {
                                            currentPath.reset()
                                            currentPath.moveTo(offset.x, offset.y)
                                            pathVersion++
                                        }
                                        DrawingTool.SHAPE -> {
                                            shapeStart = offset
                                            shapeEnd = offset
                                        }
                                        DrawingTool.TEXT -> {
                                            textPosition = offset
                                            textInput = ""
                                        }
                                    }
                                },
                                onDrag = { change, _ ->
                                    when (selectedTool) {
                                        DrawingTool.PEN, DrawingTool.ERASER -> {
                                            currentPath.lineTo(change.position.x, change.position.y)
                                            pathVersion++
                                        }
                                        DrawingTool.SHAPE -> {
                                            shapeEnd = change.position
                                        }
                                        DrawingTool.TEXT -> { }
                                    }
                                },
                                onDragEnd = {
                                    when (selectedTool) {
                                        DrawingTool.PEN -> {
                                            onAddElement(
                                                DrawElement.FreePath(
                                                    path = Path().apply { addPath(currentPath) },
                                                    color = selectedColor,
                                                    strokeWidth = strokeWidth
                                                )
                                            )
                                        }
                                        DrawingTool.ERASER -> {
                                            onAddElement(
                                                DrawElement.FreePath(
                                                    path = Path().apply { addPath(currentPath) },
                                                    color = Color.Transparent,
                                                    strokeWidth = strokeWidth
                                                )
                                            )
                                        }
                                        DrawingTool.SHAPE -> {
                                            shapeStart?.let { start ->
                                                shapeEnd?.let { end ->
                                                    onAddElement(
                                                        DrawElement.Shape(
                                                            type = selectedShape,
                                                            start = start,
                                                            end = end,
                                                            color = selectedColor,
                                                            strokeWidth = strokeWidth
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        DrawingTool.TEXT -> { }
                                    }
                                    currentPath.reset()
                                    pathVersion++
                                    shapeStart = null
                                    shapeEnd = null
                                }
                            )
                        }
                    else Modifier
                )
        ) {
            @Suppress("UNUSED_EXPRESSION") pathVersion

            elements.forEach { element ->
                drawElement(element, textMeasurer)
            }

            when (selectedTool) {
                DrawingTool.PEN, DrawingTool.ERASER -> {
                    if (!currentPath.isEmpty) {
                        drawPath(
                            path = currentPath,
                            color = if (selectedTool == DrawingTool.ERASER) Color.Transparent else selectedColor,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
                            blendMode = if (selectedTool == DrawingTool.ERASER) BlendMode.Clear else BlendMode.SrcOver
                        )
                    }
                }
                DrawingTool.SHAPE -> {
                    shapeStart?.let { start ->
                        shapeEnd?.let { end ->
                            drawShapePreview(selectedShape, start, end, selectedColor, strokeWidth)
                        }
                    }
                }
                DrawingTool.TEXT -> { }
            }
        }

        textPosition?.let { position ->
            LaunchedEffect(position) {
                focusRequester.requestFocus()
            }
            BasicTextField(
                value = textInput,
                onValueChange = { textInput = it },
                textStyle = TextStyle(
                    color = selectedColor,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (textInput.isNotEmpty()) {
                            onAddElement(
                                DrawElement.Text(
                                    text = textInput,
                                    position = position,
                                    color = selectedColor,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        textPosition = null
                        textInput = ""
                    }
                ),
                modifier = Modifier
                    .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                    .focusRequester(focusRequester)
            )
        }
    }
}

private fun DrawScope.drawElement(element: DrawElement, textMeasurer: TextMeasurer) {
    when (element) {
        is DrawElement.FreePath -> {
            if (!element.path.isEmpty) {
                drawPath(
                    path = element.path,
                    color = element.color,
                    style = Stroke(width = element.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    blendMode = if (element.color == Color.Transparent) BlendMode.Clear else BlendMode.SrcOver
                )
            }
        }
        is DrawElement.Shape -> {
            drawShapePreview(element.type, element.start, element.end, element.color, element.strokeWidth)
        }
        is DrawElement.Text -> {
            drawText(
                textMeasurer = textMeasurer,
                text = element.text,
                topLeft = element.position,
                style = TextStyle(
                    color = element.color,
                    fontSize = element.fontSize
                )
            )
        }
    }
}

private fun DrawScope.drawShapePreview(
    type: ShapeType,
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    val stroke = Stroke(width = strokeWidth)
    when (type) {
        ShapeType.RECTANGLE -> {
            drawRect(
                color = color,
                topLeft = Offset(minOf(start.x, end.x), minOf(start.y, end.y)),
                size = Size(abs(end.x - start.x), abs(end.y - start.y)),
                style = stroke
            )
        }
        ShapeType.CIRCLE -> {
            val center = Offset((start.x + end.x) / 2, (start.y + end.y) / 2)
            val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2)) / 2
            drawCircle(
                color = color,
                center = center,
                radius = radius,
                style = stroke
            )
        }
        ShapeType.LINE -> {
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        ShapeType.TRIANGLE -> {
            val path = Path().apply {
                moveTo((start.x + end.x) / 2, start.y)
                lineTo(end.x, end.y)
                lineTo(start.x, end.y)
                close()
            }
            drawPath(path = path, color = color, style = stroke)
        }
        ShapeType.ARROW -> {
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            val angle = atan2(end.y - start.y, end.x - start.x)
            val arrowLength = 30f
            val arrowAngle = Math.toRadians(30.0).toFloat()

            val arrowPoint1 = Offset(
                end.x - arrowLength * cos(angle - arrowAngle),
                end.y - arrowLength * sin(angle - arrowAngle)
            )
            val arrowPoint2 = Offset(
                end.x - arrowLength * cos(angle + arrowAngle),
                end.y - arrowLength * sin(angle + arrowAngle)
            )

            drawLine(color = color, start = end, end = arrowPoint1, strokeWidth = strokeWidth, cap = StrokeCap.Round)
            drawLine(color = color, start = end, end = arrowPoint2, strokeWidth = strokeWidth, cap = StrokeCap.Round)
        }
    }
}
