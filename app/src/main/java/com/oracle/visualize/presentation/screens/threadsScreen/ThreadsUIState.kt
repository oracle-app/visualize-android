package com.oracle.visualize.presentation.screens.threadsScreen

import java.util.Date

data class ThreadsUIState(
    val isLoading: Boolean = false,
    val visualizationTitle: String = "",
    val currentUserId: String = "",
    val comments: List<CommentUiModel> = emptyList(),
    val errorMessage: Int? = null,
    val replyingToCommentId: String? = null,
    val replyingToAuthorName: String? = null
)

data class CommentUiModel(
    val id: String,
    val authorID: String,
    val authorName: String,
    val authorImageURL: String?,
    val content: String,
    val imageURL: String?,
    val createdAt: Date,
    val threads: List<ThreadUiModel> = emptyList()
)

data class ThreadUiModel(
    val id: String,
    val authorID: String,
    val authorName: String,
    val authorImageURL: String?,
    val content: String,
    val createdAt: Date
)
