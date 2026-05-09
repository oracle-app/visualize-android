package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context
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

            //TODO: Get from Auth Repository
            getAllUserVisualizationsUseCase(currentUserID).fold(
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
                        is AppError.NetworkError -> context.getString(R.string.error_network)
                        is AppError.ParsingError -> context.getString(R.string.error_parsing)
                        is AppError.NotFound     -> context.getString(R.string.error_not_found)
                        else                     -> context.getString(R.string.error_unknown_retry)
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

