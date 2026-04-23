package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

fun TeamDTO.toShareTeam(users: List<ShareUser>): ShareTeam {
    val memberCount = this.membersIDs.size
    return ShareTeam(
        id = this.id,
        name = this.name,
        memberCount = memberCount,
        members = users
    )
}

