package com.oracle.visualize.domain.models

import java.util.Date


data class Notification (
    val id:String,
    val isRead: Boolean,
    val body: String,
    val createdAt: Date,
    val senderProfilePictureURL: String?,
    )
