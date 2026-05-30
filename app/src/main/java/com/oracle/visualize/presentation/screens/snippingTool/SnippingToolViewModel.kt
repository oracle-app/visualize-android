package com.oracle.visualize.presentation.screens.snippingTool

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.usecases.GetIndividualVisualizationUseCase
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawElement
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawingTool
import com.oracle.visualize.presentation.screens.snippingTool.components.ShapeType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SnippingToolViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getIndividualVisualizationUseCase: GetIndividualVisualizationUseCase ) : ViewModel() {

    private val _uiState = MutableStateFlow(SnippingToolUiState())
    val uiState: StateFlow<SnippingToolUiState> = _uiState.asStateFlow()

    fun addElement(element: DrawElement) {
        _uiState.update { current ->
            current.copy(
                elements = current.elements + element,
                redoStack = emptyList()
            )
        }
    }

    fun undo() {
        _uiState.update { current ->
            if (current.elements.isEmpty()) return@update current
            current.copy(
                elements = current.elements.dropLast(1),
                redoStack = current.redoStack + current.elements.last()
            )
        }
    }

    fun redo() {
        _uiState.update { current ->
            if (current.redoStack.isEmpty()) return@update current
            current.copy(
                elements = current.elements + current.redoStack.last(),
                redoStack = current.redoStack.dropLast(1)
            )
        }
    }

    fun setShape(shape: ShapeType) {
        _uiState.update { it.copy(selectedShape = shape) }
    }

    fun setColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    fun setStrokeWidth(width: Float) {
        _uiState.update { it.copy(strokeWidth = width) }
    }

    fun selectTool(tool: DrawingTool) {
        _uiState.update { current ->
            if (current.isDrawingMode && current.selectedTool == tool) {
                current.copy(isDrawingMode = false, selectedTool = null)
            } else {
                current.copy(
                    isDrawingMode = true,
                    isCroppingMode = false,
                    selectedTool = tool
                )
            }
        }
        setIsTransformable()
    }

    fun toggleCrop() {
        _uiState.update { it.copy(
            isCroppingMode = !it.isCroppingMode,
            isDrawingMode = false
        )}
        setIsTransformable()
    }

    fun toggleConfirmDialog() {
        _uiState.update { it.copy(
            showConfirmDialog = !it.showConfirmDialog
        )
        }
    }

    fun toggleCancelDialog() {
        _uiState.update { it.copy(
            showCancelDialog = !it.showCancelDialog
        )
        }
    }

    fun setCropRect(rect: IntRect) {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels

        _uiState.update { it.copy(cropRect = IntRect(
            left = rect.left.coerceIn(0, screenWidth),
            top = rect.top.coerceIn(0, screenHeight),
            right = rect.right.coerceIn(0, screenWidth),
            bottom = rect.bottom.coerceIn(0, screenHeight)
        ))}
    }

    fun confirmCrop(bitmap: Bitmap): Bitmap {
        val left = _uiState.value.cropRect.left.coerceIn(0, bitmap.width)
        val top = _uiState.value.cropRect.top.coerceIn(0, bitmap.height)
        val right = _uiState.value.cropRect.right.coerceIn(0, bitmap.width)
        val bottom = _uiState.value.cropRect.bottom.coerceIn(0, bitmap.height)
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    fun setIsTransformable() {
        _uiState.update { it.copy(isTransformable = !_uiState.value.isDrawingMode && !_uiState.value.isCroppingMode) }
    }

    fun reset() {
        _uiState.update { current ->
            current.copy(
                elements = emptyList(),
                redoStack = emptyList(),
                isDrawingMode = false,
                isCroppingMode = false,
                selectedTool = null
            )
        }
    }

}
