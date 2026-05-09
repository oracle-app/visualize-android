package com.oracle.visualize.presentation.screens.threadsScreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Threads screen.
 *
 * Uses mock data to display information
 *
 * @property uiState StateFlow containing the UI state of the screen.
 */

@HiltViewModel
class ThreadsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ThreadsUIState())
    val uiState: StateFlow<ThreadsUIState> = _uiState.asStateFlow()

    fun loadThreads(visualizationId: String) {
        _uiState.value = ThreadsUIState(
            visualizationTitle = "Relative performance of major currencies against the dollar",
            currentUserId = "user",
            threads = mockThreads
        )
    }
}

private val mockThreads = listOf(
    Thread(
        id = "1",
        authorId = "user",
        authorName = "Diana Escalante",
        timestamp = "20 min ago",
        content = "The Australian dollar has sold off more than any other currency! This part of the chart shows when it drops below all the other major currencies.",
        imageUrl = null,
        comments = listOf(
            Comment(
                id = "1",
                authorId = "2",
                authorName = "Jocelyn Duarte",
                timestamp = "17 min ago",
                content = "Good catch. The drop here is noticeably steeper than the other currencies."
            ),
            Comment(
                id = "2",
                authorId = "3",
                authorName = "Eduardo Salazar",
                timestamp = "3 min ago",
                content = "It might be linked to expectations around interest rate decisions from the Reserve Bank of Australia or weaker economic indicators during that period."
            )
        )
    ),
    Thread(
        id = "2",
        authorId = "4",
        authorName = "Lucy Martinez",
        timestamp = "02/03/26",
        content = "The Australian dollar has sold off more than any other currency! This part of the chart shows when it drops below all the other major currencies.",
        imageUrl = null,
        comments = listOf(
            Comment(
                id = "3",
                authorId = "2",
                authorName = "Jocelyn Duarte",
                timestamp = "17 min ago",
                content = "Nice catch!"
            )
        )
    )
)
