package com.oracle.visualize.presentation.screens.notificationScreen

import com.oracle.visualize.domain.models.Notification

data class NotificationUIState(
    val notifications: List<Notification> = emptyList(),
    val currentUserID: String = "",
    val isLoading: Boolean = false,
    val error: Int? = null
)
