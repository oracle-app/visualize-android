package com.oracle.visualize.presentation.screens.threadsScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.comment.CreateCommentUseCase
import com.oracle.visualize.domain.usecases.comment.CreateThreadUseCase
import com.oracle.visualize.domain.usecases.comment.DeleteCommentUseCase
import com.oracle.visualize.domain.usecases.comment.DeleteThreadUseCase
import com.oracle.visualize.domain.usecases.visualization.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.comment.GetCommentsUseCase
import com.oracle.visualize.domain.usecases.comment.GetThreadsUseCase
import com.oracle.visualize.domain.usecases.comment.UploadSnipUseCase
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
    private val createThreadUseCase: CreateThreadUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deleteThreadUseCase: DeleteThreadUseCase,
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val uploadSnipUseCase: UploadSnipUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreadsUIState())
    val uiState: StateFlow<ThreadsUIState> = _uiState.asStateFlow()
    private var currentUserID: String = ""
    private var currentUserName: String = ""
    private var currentUserImageUrl: String? = null

    init {
        currentUserID = authRepository.getCurrentUserID()

        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUserByUserID(currentUserID)
                currentUserName = currentUser?.username ?: currentUserID
                currentUserImageUrl = currentUser?.profilePictureURL
            } catch (e: Exception) {
                currentUserName = currentUserID
                currentUserImageUrl = null
            }
        }
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

    suspend fun uploadSnip(uri: String): String? {
        return uploadSnipUseCase(
            userID = currentUserID,
            uri = uri
        ).fold(
            onSuccess = { url -> url },
            onFailure = {
                _uiState.update { it.copy(errorMessage = R.string.error_upload_snip) }
                null
            }
        )
    }

    fun createComment(
        visualizationId: String,
        content: String,
        imageURL: String? = null
    ) {
        viewModelScope.launch {
            createCommentUseCase(
                visualizationId = visualizationId,
                authorID = currentUserID,
                content = content,
                imageURL = imageURL
            ).fold(
                onSuccess = { newComment ->
                    val currentUserData = getUserDisplayData(currentUserID)

                    val newCommentUi = CommentUiModel(
                        id = newComment.id,
                        authorID = newComment.authorID,
                        authorName = currentUserData.first,
                        authorImageURL = currentUserData.second,
                        content = newComment.content,
                        imageURL = newComment.imageURL,
                        createdAt = newComment.createdAt,
                        threads = emptyList()
                    )

                    _uiState.update {
                        it.copy(
                            comments = it.comments + newCommentUi
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            errorMessage = R.string.error_create_comment
                        )
                    }
                }
            )
        }
    }

    fun createCommentWithSnip(
        visualizationId: String,
        content: String,
        uri: String
    ) {
        viewModelScope.launch {
            val imageURL = uploadSnip(uri)
            createComment(visualizationId, content, imageURL)
        }
    }

    fun createThread(
        visualizationId: String,
        commentId: String,
        content: String
    ) {
        viewModelScope.launch {
            createThreadUseCase(
                visualizationId = visualizationId,
                commentId = commentId,
                authorID = currentUserID,
                authorName = currentUserName,
                authorAvatarURL = currentUserImageUrl,
                content = content
            ).fold(
                onSuccess = { newThread ->
                    val newThreadUi = ThreadUiModel(
                        id = newThread.id,
                        authorID = newThread.authorID,
                        authorName = newThread.authorName,
                        authorImageURL = newThread.authorAvatarURL,
                        content = newThread.content,
                        createdAt = newThread.createdAt
                    )
                    _uiState.update { state ->
                        state.copy(
                            replyingToCommentId = null,
                            replyingToAuthorName = null,
                            comments = state.comments.map { comment ->
                                if (comment.id == commentId) {
                                    comment.copy(threads = comment.threads + newThreadUi)
                                } else {
                                    comment
                                }
                            }
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(errorMessage = R.string.error_create_comment)
                    }
                }
            )
        }
    }

    fun startReply(
        commentId: String,
        authorName: String
    ) {
        _uiState.update {
            it.copy(
                replyingToCommentId = commentId,
                replyingToAuthorName = authorName
            )
        }
    }

    fun cancelReply() {
        _uiState.update {
            it.copy(
                replyingToCommentId = null,
                replyingToAuthorName = null
            )
        }
    }

    fun deleteComment(
        visualizationId: String,
        commentId: String
    ) {
        viewModelScope.launch {
            deleteCommentUseCase(
                visualizationId = visualizationId,
                commentId = commentId
            ).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            comments = state.comments.filterNot { it.id == commentId }
                        )
                    }
                },
                onFailure = {
                    _uiState.update { state ->
                        state.copy(errorMessage = R.string.error_unknown_retry)
                    }
                }
            )
        }
    }

    fun deleteThread(
        visualizationId: String,
        commentId: String,
        threadId: String
    ) {
        viewModelScope.launch {
            deleteThreadUseCase(
                visualizationId = visualizationId,
                commentId = commentId,
                threadId = threadId
            ).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            comments = state.comments.map { comment ->
                                if (comment.id == commentId) {
                                    comment.copy(
                                        threads = comment.threads.filterNot { it.id == threadId }
                                    )
                                } else {
                                    comment
                                }
                            }
                        )
                    }
                },
                onFailure = {
                    _uiState.update { state ->
                        state.copy(errorMessage = R.string.error_unknown_retry)
                    }
                }
            )
        }
    }
}
