package com.oracle.visualize.presentation.screens.snippingTool

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.chart.GetUserChartThemeUseCase
import com.oracle.visualize.domain.usecases.chart.ParseFullScreenChartUseCase
import com.oracle.visualize.domain.usecases.visualization.GetIndividualVisualizationUseCase
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawElement
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawingTool
import com.oracle.visualize.presentation.screens.snippingTool.components.ShapeType
import com.oracle.visualize.ui.theme.ChartPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnippingToolViewModel @Inject constructor(
    private val getIndividualVisualizationUseCase: GetIndividualVisualizationUseCase,
    private val getUserChartThemeUseCase: GetUserChartThemeUseCase,
    private val parseFullScreenChartUseCase: ParseFullScreenChartUseCase,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context ) : ViewModel() {

    private val _uiState = MutableStateFlow(SnippingToolUiState())
    val uiState: StateFlow<SnippingToolUiState> = _uiState.asStateFlow()
    private var currentUserID: String = ""
    private var userChartTheme: ChartPalette = ChartPalette.THEME1

    private var containerSize: IntSize = IntSize.Zero

    fun setContainerSize(size: IntSize) {
        containerSize = size
    }

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

    fun setItalics() {
        _uiState.update { it.copy(isItalics = !it.isItalics) }
    }

    fun setFontSize(fontsize: Float) {
        _uiState.update { it.copy(fontSize = fontsize) }
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
        val width = containerSize.width.takeIf { it > 0 } ?: context.resources.displayMetrics.widthPixels
        val height = containerSize.height.takeIf { it > 0 } ?: context.resources.displayMetrics.heightPixels

        _uiState.update { it.copy(cropRect = IntRect(
            left = rect.left.coerceIn(0, width),
            top = rect.top.coerceIn(0, height),
            right = rect.right.coerceIn(0, width),
            bottom = rect.bottom.coerceIn(0, height)
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

    fun loadVisualization(visualizationId: String) {
        currentUserID = authRepository.getCurrentUserID() ?: ""
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            userChartTheme = when (val chartColorThemeResult = getUserChartThemeUseCase(currentUserID)){
                is AppResult.Success -> chartColorThemeResult.data
                is AppResult.Error -> userChartTheme
            }

            when (val result = getIndividualVisualizationUseCase(visualizationId)) {
                is AppResult.Success -> {
                    val visualization = result.data

                    if (visualization == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = R.string.error_viz_not_found
                            )
                        }
                    } else {
                        when (val chartResult = parseFullScreenChartUseCase(visualization)) {
                            is AppResult.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        visualization = visualization,
                                        chart = chartResult.data,
                                        chartColorTheme = userChartTheme,
                                        errorMessage = null
                                    )
                                }
                            }

                            is AppResult.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        visualization = visualization,
                                        chartColorTheme = userChartTheme,
                                        errorMessage = R.string.error_chart_not_found
                                    )
                                }
                            }
                        }
                    }
                }

                is AppResult.Error -> {
                    val uiErrorMessage = when (result.error) {
                        is AppError.NetworkError -> R.string.error_network
                        is AppError.ParsingError -> R.string.error_parsing
                        is AppError.NotFound -> R.string.error_viz_not_found
                        else -> R.string.error_unknown_retry
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = null,
                            chartColorTheme = userChartTheme,
                            errorMessage = uiErrorMessage
                        )
                    }
                }
            }
        }
    }

}
