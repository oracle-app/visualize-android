package com.oracle.visualize.presentation.screens.notificationScreen

import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.models.enums.NotificationGroup

data class NotificationUIState(
    val groupedNotifications: Map<NotificationGroup, List<Notification>> = emptyMap(),
    val currentUserID: String = "",
    val isLoading: Boolean = false,
    val error: Int? = null
)
