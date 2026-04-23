package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId

data class UserDTO(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val userType: String = "",
    val profilePictureURL: String = "",
    val themePreference: String = "",
    val notificationsEnabled: String = "",
    val chartTheme: String = "",
    val token: List<String> = listOf()
)
