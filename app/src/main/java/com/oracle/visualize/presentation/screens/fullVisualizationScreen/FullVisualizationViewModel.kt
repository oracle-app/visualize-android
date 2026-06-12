package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.chart.GetUserChartThemeUseCase
import com.oracle.visualize.domain.usecases.visualization.GetIndividualVisualizationUseCase
import com.oracle.visualize.domain.usecases.chart.ParseFullScreenChartUseCase
import com.oracle.visualize.ui.theme.ChartPalette
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
 * @property getIndividualVisualizationUseCase Use case to fetch user visualizations.
 */
@HiltViewModel
class FullVisualizationViewModel @Inject constructor(
    private val getIndividualVisualizationUseCase: GetIndividualVisualizationUseCase,
    private val getUserChartThemeUseCase: GetUserChartThemeUseCase,
    private val parseFullScreenChartUseCase: ParseFullScreenChartUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FullVisualizationUIState())
    val uiState: StateFlow<FullVisualizationUIState> = _uiState.asStateFlow()

    private var currentUserID: String = ""
    private var userChartTheme: ChartPalette = ChartPalette.THEME1

    init {
        currentUserID = authRepository.getCurrentUserID() ?: ""
    }

    fun loadVisualization(visualizationId: String) {
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

    fun onSnipCompleted(bitmap: Bitmap) {
        // TODO: attach to thread
    }

}

