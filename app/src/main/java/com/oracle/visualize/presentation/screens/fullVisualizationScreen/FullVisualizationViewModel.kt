package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.visualization.GetIndividualVisualizationUseCase
import com.oracle.visualize.domain.usecases.chart.ParseFullScreenChartUseCase
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
    private val parseFullScreenChartUseCase: ParseFullScreenChartUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FullVisualizationUIState())
    val uiState: StateFlow<FullVisualizationUIState> = _uiState.asStateFlow()

    /*
    NOTE: The currentUserID just became apparently useless, but I'll leave it here in
    case it becomes relevant for a future feature.
    */
    private var currentUserID: String = ""

    init {
        currentUserID = authRepository.getCurrentUserID()
    }

    fun loadVisualization(visualizationId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getIndividualVisualizationUseCase(visualizationId).fold(
                onSuccess = { visualization ->
                    val chart = visualization?.let { parseFullScreenChartUseCase(it) }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = visualization,
                            chart = chart,
                            errorMessage = if (visualization == null) {
                                R.string.error_viz_not_found
                            } else if (chart == null) {
                                R.string.error_chart_not_found
                            } else {
                                null
                            }
                        )
                    }
                },
                onFailure = { error ->
                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> R.string.error_network
                        is AppError.ParsingError -> R.string.error_parsing
                        is AppError.NotFound     -> R.string.error_viz_not_found
                        else                     -> R.string.error_unknown_retry
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

    fun onSnipCompleted(bitmap: Bitmap) {
        // TODO: attach to thread
    }

}

