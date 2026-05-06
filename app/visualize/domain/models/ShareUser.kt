package com.oracle.visualize.domain.models

/**
 * Domain model representing a user for sharing purposes.
 */
data class ShareUser(
    val id: String,
    val username: String,
    val email: String,
    val profilePictureURL: String?,
)
