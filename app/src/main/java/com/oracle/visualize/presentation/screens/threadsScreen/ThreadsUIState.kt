package com.oracle.visualize.presentation.screens.threadsScreen

import com.oracle.visualize.domain.models.Thread

data class ThreadsUIState(
    val isLoading: Boolean = false,
    val visualizationTitle: String = "",
    val currentUserId: String = "",
    val threads: List<Thread> = emptyList(),
    val errorMessage: String? = null
)