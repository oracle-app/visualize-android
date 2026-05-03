package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullVisualizationViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FullVisualizationUIState())
    val uiState: StateFlow<FullVisualizationUIState> = _uiState.asStateFlow()

    fun loadVisualization(visualizationId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            //TODO: Get from Auth Repository
            val userID = "oEJtQz0gdbRpTZ8ETPCy"

            getAllUserVisualizationsUseCase(userID, VisualizationFilter.ALL).fold(
                onSuccess = { visualizations ->
                    val visualization = visualizations.find { it.id == visualizationId }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualization = visualization,
                            errorMessage = if (visualization == null) {
                                "Visualization not found."
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

