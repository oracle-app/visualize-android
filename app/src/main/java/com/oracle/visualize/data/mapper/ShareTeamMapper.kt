package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

/**
 * Extension function to map [TeamDTO] to [ShareTeam] domain model.
 *
 * Member count uses the resolved [users] list (not membersIDs.size) so that
 * the owner is always counted regardless of whether old Firestore documents
 * stored them in membersIDs or only in ownerID.
 *
 * @param users Fully-resolved list of [ShareUser] members, including the owner.
 */
fun TeamDTO.toShareTeam(users: List<ShareUser>): ShareTeam {
    return ShareTeam(
        id          = this.id,
        name        = this.name,
        ownerID     = this.ownerID,
        memberCount = users.size,   // count from resolved users, not raw IDs
        members     = users
    )
}
