package com.oracle.visualize.domain.models

data class ShareUser(
    val id: String,
    val name: String,
    val email: String,
    val avatarInitials: String,
    val avatarColor: Long = 0xFFE8A87C
)
