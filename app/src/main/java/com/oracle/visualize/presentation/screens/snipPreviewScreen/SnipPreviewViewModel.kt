package com.oracle.visualize.presentation.screens.snipPreviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.comment.CreateCommentUseCase
import com.oracle.visualize.domain.usecases.comment.UploadSnipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnipPreviewViewModel @Inject constructor(
    private val uploadSnipUseCase: UploadSnipUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SnipPreviewUIState())
    val uiState: StateFlow<SnipPreviewUIState> = _uiState.asStateFlow()

    private val currentUserID: String = authRepository.getCurrentUserID() ?: ""

    fun onCaptionChange(value: String) {
        _uiState.update { it.copy(caption = value) }
    }

    fun showConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = true) }
    }

    fun dismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun consumeShareCompleted() {
        _uiState.update { it.copy(shareCompleted = false) }
    }

    fun shareSnipAsThread(
        visualizationId: String,
        snipUri: String
    ) {
        if (_uiState.value.isSharing) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSharing = true,
                    showConfirmDialog = false,
                    errorMessage = null
                )
            }

            val imageUrl = when (val uploadResult = uploadSnipUseCase(currentUserID, snipUri)) {
                is AppResult.Success -> uploadResult.data
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSharing = false,
                            errorMessage = R.string.error_upload_snip
                        )
                    }
                    return@launch
                }
            }

            when (createCommentUseCase(
                visualizationId = visualizationId,
                authorID = currentUserID,
                content = _uiState.value.caption,
                imageURL = imageUrl
            )) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSharing = false,
                            shareCompleted = true
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSharing = false,
                            errorMessage = R.string.error_create_comment
                        )
                    }
                }
            }
        }
    }
}
