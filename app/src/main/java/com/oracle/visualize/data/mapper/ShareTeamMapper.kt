package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

/**
 * Extension function to map [TeamDTO] to [ShareTeam] domain model.
 *
 * @param users List of [ShareUser] who are members of the team.
 * @return A [ShareTeam] object containing team details and its members.
 */
fun TeamDTO.toShareTeam(users: List<ShareUser>): ShareTeam {
    val memberCount = this.membersIDs.size
    return ShareTeam(
        id = this.id,
        name = this.name,
        memberCount = memberCount,
        members = users
    )
}

