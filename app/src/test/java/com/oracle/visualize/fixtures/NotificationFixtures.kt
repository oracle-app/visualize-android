package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.Notification
import java.util.Date

object NotificationFixtures {

    const val VALID_USER_ID = "user123"
    const val VALID_NOTIFICATION_ID = "notif123"

    val fakeNotification = Notification(
        id = VALID_NOTIFICATION_ID,
        isRead = false,
        body = "Someone shared a visualization with you",
        createdAt = Date()
    )

    val fakeNotifications = listOf(
        Notification(
            id = "notif1",
            isRead = false,
            body = "Someone shared a visualization with you",
            createdAt = Date()
        ),
        Notification(
            id = "notif2",
            isRead = true,
            body = "Someone replied to your comment",
            createdAt = Date()
        )
    )
}
