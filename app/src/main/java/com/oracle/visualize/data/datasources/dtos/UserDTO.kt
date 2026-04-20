package com.oracle.visualize.data.datasources.dtos

data class UserDTO(
    val id: String,
    val email: String,
    val username: String,
    val userType: String,
    val profilePictureURL: String,
    val themePreference: String,
    val notificationsEnabled: String,
    val token: List<String>
)
