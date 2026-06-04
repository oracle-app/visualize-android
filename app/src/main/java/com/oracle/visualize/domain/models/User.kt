package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.UserType

/**
 * Domain model representing a User.
 */
data class User (
    val id: String,
    val userType: UserType,
    val email: String,
    val username: String,
    val profilePictureURL: String,
    val themePreference: ThemePreference,
    val chartTheme: String,
    val hiddenVisualizations: List<String>
)
enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
