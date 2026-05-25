package com.oracle.visualize.presentation.screens.threadsScreen

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
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreadsUIState())
    val uiState: StateFlow<ThreadsUIState> = _uiState.asStateFlow()
    private var currentUserID: String = ""

    init {
        currentUserID = authRepository.getCurrentUserID()
    }

    private suspend fun getUserDisplayData(
        userID: String
    ): Pair<String, String?> {
        return try {
            val user = userRepository.getUserByUserID(userID)

            Pair(
                user?.username ?: userID,
                user?.profilePictureURL
            )
        } catch (e: Exception) {
            Pair(userID, null)
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
                    val commentsUi = comments.map { comment ->

                        val commentAuthor = getUserDisplayData(comment.authorID)

                        val threads = getThreadsUseCase(
                            visualizationId = visualizationId,
                            commentId = comment.id
                        ).getOrElse {
                            emptyList()
                        }

                        val threadsUi = threads.map { thread ->

                            val threadAuthor = getUserDisplayData(thread.authorID)

                            ThreadUiModel(
                                id = thread.id,
                                authorID = thread.authorID,
                                authorName = thread.authorName.ifBlank { threadAuthor.first },
                                authorImageURL = thread.authorAvatarURL ?: threadAuthor.second,
                                content = thread.content,
                                createdAt = thread.createdAt
                            )
                        }

                        CommentUiModel(
                            id = comment.id,
                            authorID = comment.authorID,
                            authorName = commentAuthor.first,
                            authorImageURL = commentAuthor.second,
                            content = comment.content,
                            imageURL = comment.imageURL,
                            createdAt = comment.createdAt,
                            threads = threadsUi
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            visualizationTitle = visualizationTitle,
                            currentUserId = currentUserID,
                            comments = commentsUi
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
