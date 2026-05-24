package com.oracle.visualize.presentation.screens.threadsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.CreateCommentUseCase
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetCommentsUseCase
import com.oracle.visualize.domain.usecases.GetThreadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Threads screen.
 *
 * Handles loading visualization information, loading comments,
 * and creating new comments for the selected visualization.
 *
 * @property createCommentUseCase Use case used to create comments.
 * @property getCommentsUseCase Use case used to fetch comments.
 * @property getAllUserVisualizationsUseCase Use case used to fetch visualization data.
 * @property authRepository Repository used to retrieve current user data.
 */
@HiltViewModel
class ThreadsViewModel @Inject constructor(
    private val createCommentUseCase: CreateCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getThreadsUseCase: GetThreadsUseCase,
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreadsUIState())
    val uiState: StateFlow<ThreadsUIState> = _uiState.asStateFlow()
    private var currentUserID: String = ""
    private var currentUserName: String = ""
    private var currentUserImageUrl: String? = null

    init {
        try {
            currentUserID = authRepository.getCurrentUserID()
            val currentUser = authRepository.getCurrentUser()
            currentUserName = currentUser?.email ?: "Current User"
        } catch (e: Exception) {
            Log.e("ThreadsViewModel", "Failed to retrieve current user", e)
        }
    }

    fun loadThreads(visualizationId: String) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val visualizationsResult = getAllUserVisualizationsUseCase(currentUserID)
            val commentsResult = getCommentsUseCase(visualizationId)
            val visualizationTitle = visualizationsResult.getOrNull()
                ?.find { it.id == visualizationId }
                ?.title
                ?: ""

            commentsResult.fold(
                onSuccess = { comments ->
                    val commentsWithThreads = comments.map { comment ->

                        val threads = getThreadsUseCase(
                            visualizationId = visualizationId,
                            commentId = comment.id
                        ).getOrElse {
                            emptyList()
                        }

                        comment.copy(
                            threads = threads
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualizationTitle = visualizationTitle,
                            currentUserId = currentUserID,
                            comments = commentsWithThreads
                        )
                    }
                },
                onFailure = { error ->
                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> R.string.error_network
                        is AppError.ParsingError -> R.string.error_parsing
                        is AppError.NotFound -> R.string.error_com_not_found
                        else -> R.string.error_unknown_retry
                    }

                    Log.e("ThreadsViewModel", "Error fetching comments: ${error.message}", error)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualizationTitle = visualizationTitle,
                            errorMessage = uiErrorMessage
                        )
                    }
                }
            )
        }
    }

    fun createComment(
        visualizationId: String,
        content: String
    ) {
        viewModelScope.launch {
            createCommentUseCase(
                visualizationId = visualizationId,
                authorID = currentUserID,
                content = content,
                imageURL = null
            ).fold(
                onSuccess = {
                    loadThreads(visualizationId)
                },
                onFailure = { error ->
                    Log.e("ThreadsViewModel", "Error creating comment: ${error.message}", error)

                    _uiState.update {
                        it.copy(
                            errorMessage = R.string.error_create_comment
                        )
                    }
                }
            )
        }
    }
}
