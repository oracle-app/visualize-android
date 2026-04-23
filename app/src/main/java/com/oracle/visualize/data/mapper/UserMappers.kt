package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

fun UserDTO.toDomain(): User = User(
    id = id,
    userType = userType,
    email = email,
    username = username,
    profilePictureURL = profilePictureURL,
    themePreference = themePreference,
    chartTheme = chartTheme,
    hiddenVisualizations = hiddenVisualizations
)
fun UserDTO.toShareUser(): ShareUser {
    return ShareUser(
        id = this.id,
        email = this.email,
        username = this.username,
        profilePictureURL = if (this.profilePictureURL != "") this.profilePictureURL else null
    )
}