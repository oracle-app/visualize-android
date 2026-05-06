package com.oracle.visualize.domain.models

<<<<<<< Updated upstream
/**
 * Domain model representing a User.
 */
data class User (
=======
data class User(
>>>>>>> Stashed changes
    val id: String,
    val userType: UserType,
    val email: String,
    val username: String,
    val profilePictureURL: String,
    val themePreference: ThemePreference,
    val chartTheme: String, // Pending: Check variable Type
    val hiddenVisualizations: List<String>,
)

enum class UserType {
<<<<<<< Updated upstream
    ADMIN, WRITER, CONSUMER
=======
    ADMIN,
    WRITER,
    COSTUMER,
>>>>>>> Stashed changes
}

enum class ThemePreference {
    LIGHT,
    DARK,
    SYSTEM,
}
