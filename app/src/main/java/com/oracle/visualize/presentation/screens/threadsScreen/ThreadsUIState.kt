package com.oracle.visualize.presentation.screens.threadsScreen

import com.oracle.visualize.domain.models.Comment
data class ThreadsUIState(
    val isLoading: Boolean = false,
    val visualizationTitle: String = "",
    val currentUserId: String = "",
    val comments: List<Comment> = emptyList(),
    val errorMessage: Int? = null
)
