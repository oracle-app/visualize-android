package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.data.datasources.local.ChartCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val authRepository: AuthRepository,
    private val chartCacheManager: ChartCacheManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(FullVisualizationUIState())
    val uiState: StateFlow<FullVisualizationUIState> = _uiState.asStateFlow()
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

            getAllUserVisualizationsUseCase(currentUserID).fold(
                onSuccess = { visualizations ->
                    val visualization = visualizations.find { it.id == visualizationId }
                    if (visualization == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = R.string.error_viz_not_found
                            )
                        }
                        return@fold
                    }
                    val chart = withContext(Dispatchers.IO) {
                        chartCacheManager.getChart(visualization.id)
                            ?: ChartMapper.fromPreviewJson(visualization.previewJSON)?.also { parsedChart ->
                                chartCacheManager.saveChart(visualization.id, parsedChart)
                            }
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = visualization,
                            chart = chart,
                            errorMessage = if (chart == null) {
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

