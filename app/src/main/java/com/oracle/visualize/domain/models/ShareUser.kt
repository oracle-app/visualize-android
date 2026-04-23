package com.oracle.visualize.domain.models

data class ShareUser(
    val id: String,
    val username: String,
    val email: String,
    val profilePictureURL: String?,
)
