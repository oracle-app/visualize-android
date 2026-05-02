package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId


/**
 * Data Transfer Object representing a user in the database.
 *
 * @property id The unique identifier for the user document.
 * @property userType The type of user (e.g., administrator, standard).
 * @property email The user's email address.
 * @property username The user's display name.
 * @property profilePictureURL URL to the user's profile image.
 * @property themePreference The user's preferred application theme.
 * @property chartTheme The user's preferred chart visualization theme.
 * @property hiddenVisualizations List of IDs for visualizations hidden by the user.
 */
class UserDTO (
    @DocumentId
    val id: String = "",
    val userType: String = "",
    val email: String = "",
    val username: String = "",
    val profilePictureURL: String = "",
    val themePreference: String = "",
    val chartTheme: String = "",
    val hiddenVisualizations: List<String> = emptyList()
)