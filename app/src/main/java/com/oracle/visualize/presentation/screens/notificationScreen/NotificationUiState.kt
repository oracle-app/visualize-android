package com.oracle.visualize.presentation.screens.notificationScreen

import com.oracle.visualize.domain.models.Notification

/**
 * UI State for the Notifications screen.
 * Encapsulates the different states of the notification data retrieval process.
 */
sealed interface NotificationUiState {
    /** Indicates that the notifications are currently being fetched. */
    data object Loading : NotificationUiState

    /**
     * Indicates that the notifications were successfully fetched.
     * 
     * @property notifications The list of [Notification] objects to display.
     * @property lastUpdated Timestamp of the last update to force UI recomposition.
     */
    data class Success(
        val notifications: List<Notification> = emptyList(),
        val lastUpdated: Long = System.currentTimeMillis()
    ) : NotificationUiState

    /**
     * Indicates that an error occurred while fetching notifications.
     * 
     * @property message Descriptive error message.
     */
    data class Error(val message: String) : NotificationUiState
}
