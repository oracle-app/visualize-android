package com.oracle.visualize.domain.models

data class User (
    val id: String,
    val userType: UserType,
    val email: String,
    val userName: String,
    val profilePictureUrl: String?,
    val themePreference: ThemePreference,
    val chartTheme: String, // Pending: Check variable Type
    val notificationsEnabled: Boolean
)

enum class UserType {
    ADMIN, WRITER, COSTUMER
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
