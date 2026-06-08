package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.ThemePreference
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.enums.UserType

/**
 * Extension function to map [UserDTO] to [User] domain model.
 * Handles parsing of enums like [UserType] and [ThemePreference].
 *
 * @return A [User] object.
 */
fun UserDTO.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    profilePictureURL = profilePictureURL,
    chartTheme = chartTheme,
    hiddenVisualizations = hiddenVisualizations,

    userType = runCatching {
        UserType.valueOf(userType.trim().uppercase())
    }.getOrDefault(UserType.CONSUMER),

    themePreference = runCatching {
        ThemePreference.valueOf(themePreference)
    }.getOrDefault(ThemePreference.SYSTEM)
)
/**
 * Extension function to map [UserDTO] to [ShareUser] domain model.
 * Used for sharing features where only a subset of user data is needed.
 *
 * @return A [ShareUser] object.
 */
fun UserDTO.toShareUser(): ShareUser {
    return ShareUser(
        id = this.id,
        email = this.email,
        username = this.username,
        profilePictureURL = if (this.profilePictureURL != "") this.profilePictureURL else null
    )
}
