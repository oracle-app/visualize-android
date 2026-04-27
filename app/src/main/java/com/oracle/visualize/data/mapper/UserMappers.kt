package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.ThemePreference
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.UserType

fun UserDTO.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    profilePictureURL = profilePictureURL,
    chartTheme = chartTheme,
    hiddenVisualizations = hiddenVisualizations,

    userType = runCatching {
        UserType.valueOf(userType)
    }.getOrDefault(UserType.CONSUMER),

    themePreference = runCatching {
        ThemePreference.valueOf(themePreference)
    }.getOrDefault(ThemePreference.SYSTEM)
)
fun UserDTO.toShareUser(): ShareUser {
    return ShareUser(
        id = this.id,
        email = this.email,
        username = this.username,
        profilePictureURL = if (this.profilePictureURL != "") this.profilePictureURL else null
    )
}