package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.User

fun UserDTO.toDomain(): User = User(
    id = id,
    userType = userType,
    email = email,
    username = username,
    profilePictureURL = profilePictureUrl,
    themePreference = themePreference,
    chartTheme = chartTheme,
    hiddenVisualizations = hiddenVisualizations
)