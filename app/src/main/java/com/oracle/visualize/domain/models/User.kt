package com.oracle.visualize.domain.models

data class User (
    val id: String,
    val userType: String,
    val email: String,
    val username: String,
    val profilePictureURL: String,
    val themePreference: String,
    val chartTheme: String, // Pending: Check variable Type
    val hiddenVisualizations: List<String>
)

enum class UserType {
    ADMIN, WRITER, COSTUMER
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
