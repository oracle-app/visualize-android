package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.firestore.DocumentId
import com.oracle.visualize.domain.models.ThemePreference
import com.oracle.visualize.domain.models.UserType

class UserDTO (
    @DocumentId
    val id: String = "",
    val userType: UserType = UserType.WRITER,
    val email: String = "",
    val userName: String = "",
    val profilePictureUrl: String? = null,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val chartTheme: String = "",
    val notificationsEnabled: Boolean = true,
    val hiddenVisualizations: List<String> = emptyList()
)