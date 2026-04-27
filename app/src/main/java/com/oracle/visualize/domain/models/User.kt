package com.oracle.visualize.domain.models

data class User (
    val id: String,
    val userType: UserType,
    val email: String,
    val username: String,
    val profilePictureURL: String,
    val themePreference: ThemePreference,
    val chartTheme: String, // Pending: Check variable Type
    val hiddenVisualizations: List<String>
)

enum class UserType {
    ADMIN, WRITER, CONSUMER
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
