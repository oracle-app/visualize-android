package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

fun UserDTO.toShareUser(): ShareUser {
    return ShareUser(
        id = this.id,
        email = this.email,
        username = this.username,
        profilePictureURL = if (this.profilePictureURL != "") this.profilePictureURL else null
    )
}