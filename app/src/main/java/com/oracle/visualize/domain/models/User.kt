package com.oracle.visualize.domain.models

data class User (
    val id: String,
    val userType: String,
    val email: String,
    val userName: String,
    val profilePictureUrl: String?,
    val themePreference: String, // Pending: Memory optimization
    val chartTheme: String, // Pending: Memory optimization
    val notificationsEnabled: Boolean,
    val tokens: List<String>
)