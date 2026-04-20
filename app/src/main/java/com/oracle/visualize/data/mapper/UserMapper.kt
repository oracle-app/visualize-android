package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.User

fun UserDTO.toUser(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.username,
        profilePictureURL = this.profilePictureURL
    )
}
