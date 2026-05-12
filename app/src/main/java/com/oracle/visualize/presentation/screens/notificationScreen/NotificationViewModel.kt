package com.oracle.visualize.presentation.screens.notificationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.NotificationSection
import com.oracle.visualize.domain.repositories.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for the Notifications screen.
 * Handles notification data retrieval, sorting, and date formatting logic.
 *
 * @property notificationRepository Repository to fetch notification data.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    notificationRepository: NotificationRepository,
) : ViewModel() {

    /**
     * UI state representing the current list of notifications or loading/error status.
     * Includes a timestamp to force UI refresh on every repository emission.
     */
    val uiState: StateFlow<NotificationUiState> = notificationRepository.getNotifications()
        .map { notifications ->
            if (notifications.isEmpty()) {
                NotificationUiState.Success(emptyList(), System.currentTimeMillis())
            } else {
                NotificationUiState.Success(
                    notifications = notifications.sortedByDescending { it.createdAt },
                    lastUpdated = System.currentTimeMillis(),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationUiState.Loading
        )

    /**
     * Formats a given date to a human-readable relative string.
     * 
     * @param date The date to format.
     * @return A string such as "Just now", "20 min ago", "Yesterday", or "DD/MM/YY".
     */
    fun formatTimestamp(date: Date): String {
        val now = Date()
        val diffInMs = now.time - date.time
        val diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMs)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMs)
        
        return when {
            diffInMin < 1 -> "Just now"
            diffInMin < 60 -> "$diffInMin min ago"
            (diffInHours < 24) && isSameDay(now, date) -> "$diffInHours h ago"
            isYesterday(date) -> "Yesterday"
            else -> {
                val cal = Calendar.getInstance().apply { time = date }
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH) + 1
                val year = cal.get(Calendar.YEAR) % 100
                "$day/$month/$year"
            }
        }
    }

    /**
     * Categorizes a notification date into a specific [NotificationSection].
     * 
     * @param date The date to categorize.
     * @return The corresponding [NotificationSection].
     */
    fun getSection(date: Date): NotificationSection {
        val now = Date()
        return when {
            isSameDay(now, date) -> NotificationSection.TODAY
            isYesterday(date) -> NotificationSection.YESTERDAY
            else -> NotificationSection.LAST_30_DAYS
        }
    }

    /**
     * Helper to check if two dates fall on the same calendar day.
     */
    private fun isSameDay(d1: Date, d2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = d1 }
        val cal2 = Calendar.getInstance().apply { time = d2 }
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) &&
                (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
    }

    /**
     * Helper to check if a date was yesterday relative to today.
     */
    private fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val cal = Calendar.getInstance().apply { time = date }
        return yesterday.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
    }
}
