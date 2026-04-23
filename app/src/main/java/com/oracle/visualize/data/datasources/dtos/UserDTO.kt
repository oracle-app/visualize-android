package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId
import com.oracle.visualize.domain.models.ThemePreference
import com.oracle.visualize.domain.models.UserType

class UserDTO (
    @DocumentId
    val id: String = "",
    val userType: String = "",
    val email: String = "",
    val username: String = "",
    val profilePictureUrl: String = "",
    val themePreference: String = "",
    val chartTheme: String = "",
    val hiddenVisualizations: List<String> = emptyList()
)