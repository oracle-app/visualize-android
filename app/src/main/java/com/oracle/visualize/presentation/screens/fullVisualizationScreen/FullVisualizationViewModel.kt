package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetMockChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * ViewModel for the FullScreen visualization screen.
 *
 * Loads the selected visualization using its ID.
 *
 * @property getAllUserVisualizationsUseCase Use case to fetch user visualizations.
 */
@HiltViewModel
class FullVisualizationViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val getMockChartUseCase: GetMockChartUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FullVisualizationUIState())
    val uiState: StateFlow<FullVisualizationUIState> = _uiState.asStateFlow()
    private val currentUserID: String = "e9Nk8XrxHJAtwN3Hf2FL"

    fun loadVisualization(visualizationId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            /*
            * Get a chart from the mock chart repository.
            *
            * @param chartType: The type of chart, according belongs to the
            * ChartTypes enum.
            *
            * CHART TYPES (Check "domain/models/enums/ChartTypes.kt"):
            * - VERTICAL_BAR (Vertical Bar Chart)
            * - HORIZONTAL_BAR (Vertical Bar Chart)
            * - STACKED_BAR (Stacked Bar Chart)
            * - LINE (Line Chart)
            * - SCATTER (Scatter Chart)
            * - PIE (Pie Chart)
            * - DONUT (Donut Chart)
            * - AREA (Area Chart)
            *
            * TODO: Get data from the microservice when it becomes available.
            *
            * */
            val mockChart = getMockChartUseCase(ChartTypes.VERTICAL_BAR).fold(
                onSuccess = { it },
                onFailure = { null }
            )

            //TODO: Get from Auth Repository
            getAllUserVisualizationsUseCase(currentUserID).fold(
                onSuccess = { visualizations ->
                    val visualization = visualizations.find { it.id == visualizationId }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = visualization,
                            chart = mockChart,
                            errorMessage = if (visualization == null) {
                                "Visualization not found."
                            } else if (mockChart == null) {
                                "Chart not found."
                            } else {
                                null
                            }
                        )
                    }
                },
                onFailure = { error ->
                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> "Connection error. Please check your internet."
                        is AppError.ParsingError -> "There was a problem reading this visualization."
                        is AppError.NotFound -> "Visualization not found."
                        else -> "An unexpected error occurred. Please try again,."
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = null,
                            errorMessage = uiErrorMessage
                        )
                    }
                }
            )
        }
    }
}

