package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.User

fun UserDTO.toDomain(): User = User(
    id = id,
    userType = userType,
    email = email,
    userName = userName,
    profilePictureUrl = profilePictureUrl,
    themePreference = themePreference,
    chartTheme = chartTheme,
    notificationsEnabled = notificationsEnabled,
    hiddenVisualizations = hiddenVisualizations
)