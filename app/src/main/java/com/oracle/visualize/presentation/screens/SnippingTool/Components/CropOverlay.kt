package com.oracle.visualize.presentation.screens.SnippingTool.Components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer


private enum class Corner { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

@Composable
fun CropOverlay(
    cropRect: IntRect,
    onCropRectChange: (IntRect) -> Unit,
    isCropDraggable: Boolean,
    modifier: Modifier = Modifier
) {
    val handleSize = 24.dp
    val handleSizePx = with(LocalDensity.current) { handleSize.toPx() }
    var draggedCorner by remember { mutableStateOf<Corner?>(null) }
    val cropRectState = rememberUpdatedState(cropRect)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .then(
                if (isCropDraggable) Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val current = cropRectState.value
                            val handleRadius = handleSizePx
                            draggedCorner = when {
                                offset.x < current.left + handleRadius && offset.y < current.top + handleRadius -> Corner.TOP_LEFT
                                offset.x > current.right - handleRadius && offset.y < current.top + handleRadius -> Corner.TOP_RIGHT
                                offset.x < current.left + handleRadius && offset.y > current.bottom - handleRadius -> Corner.BOTTOM_LEFT
                                offset.x > current.right - handleRadius && offset.y > current.bottom - handleRadius -> Corner.BOTTOM_RIGHT
                                else -> null
                            }
                        },
                        onDragEnd = { draggedCorner = null },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val current = cropRectState.value
                            when (draggedCorner) {
                                Corner.TOP_LEFT -> onCropRectChange(IntRect(
                                    left = (current.left + dragAmount.x.toInt()).coerceAtMost(current.right - 100),
                                    top = (current.top + dragAmount.y.toInt()).coerceAtMost(current.bottom - 100),
                                    right = current.right,
                                    bottom = current.bottom
                                ))
                                Corner.TOP_RIGHT -> onCropRectChange(IntRect(
                                    left = current.left,
                                    top = (current.top + dragAmount.y.toInt()).coerceAtMost(current.bottom - 100),
                                    right = (current.right + dragAmount.x.toInt()).coerceAtLeast(current.left + 100),
                                    bottom = current.bottom
                                ))
                                Corner.BOTTOM_LEFT -> onCropRectChange(IntRect(
                                    left = (current.left + dragAmount.x.toInt()).coerceAtMost(current.right - 100),
                                    top = current.top,
                                    right = current.right,
                                    bottom = (current.bottom + dragAmount.y.toInt()).coerceAtLeast(current.top + 100)
                                ))
                                Corner.BOTTOM_RIGHT -> onCropRectChange(IntRect(
                                    left = current.left,
                                    top = current.top,
                                    right = (current.right + dragAmount.x.toInt()).coerceAtLeast(current.left + 100),
                                    bottom = (current.bottom + dragAmount.y.toInt()).coerceAtLeast(current.top + 100)
                                ))
                                null -> { }
                            }
                        }
                    )
                } else Modifier
            )
    ) {
        // Dark mask
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )

        // Clear crop area
        drawRect(
            color = Color.Transparent,
            blendMode = BlendMode.Clear,
            topLeft = Offset(cropRect.left.toFloat(), cropRect.top.toFloat()),
            size = Size(cropRect.width.toFloat(), cropRect.height.toFloat())
        )

        // Crop rect border
        drawRect(
            color = Color.White,
            topLeft = Offset(cropRect.left.toFloat(), cropRect.top.toFloat()),
            size = Size(cropRect.width.toFloat(), cropRect.height.toFloat()),
            style = Stroke(width = 2.dp.toPx())
        )

        // Corner handles
        listOf(
            Offset(cropRect.left.toFloat(), cropRect.top.toFloat()),
            Offset(cropRect.right.toFloat(), cropRect.top.toFloat()),
            Offset(cropRect.left.toFloat(), cropRect.bottom.toFloat()),
            Offset(cropRect.right.toFloat(), cropRect.bottom.toFloat())
        ).forEach { corner ->
            drawCircle(
                color = Color.White,
                radius = handleSizePx / 2,
                center = corner
            )
        }
    }
}
