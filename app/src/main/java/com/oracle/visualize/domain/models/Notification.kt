package com.oracle.visualize.domain.models

import java.util.Date

data class Notification (
    val id: String,
    val userID: String,
    val isRead: Boolean,
    val type: String,
    val createdAt: Date
)