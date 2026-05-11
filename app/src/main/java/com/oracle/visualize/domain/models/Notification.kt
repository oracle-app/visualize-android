package com.oracle.visualize.domain.models

import java.util.Date

/**
 * Represents a notification entity in the system.
 *
 * @property id Unique identifier for the notification.
 * @property user Name of the user or system who triggered the notification.
 * @property message The content/body of the notification.
 * @property createdAt The date and time when the notification was created.
 * @property avatarRes Resource ID for the avatar icon associated with the notification.
 */
data class Notification(
    val id: String,
    val user: String,
    val message: String,
    val createdAt: Date,
    val avatarRes: Int
)

/**
 * Enumeration of possible sections to group notifications by date.
 */
enum class NotificationSection {
    /** Notifications from the current day. */
    TODAY,
    /** Notifications from the previous day. */
    YESTERDAY,
    /** Notifications from the last 30 days excluding today and yesterday. */
    LAST_30_DAYS
}
