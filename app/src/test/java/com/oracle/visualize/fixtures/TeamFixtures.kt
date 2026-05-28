package com.oracle.visualize.fixtures

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

object TeamFixtures {

    const val VALID_TEAM_ID   = "team123"
    const val VALID_OWNER_ID  = "user123"
    const val VALID_TEAM_NAME = "Design Team"

    val VALID_MEMBER_IDS = listOf("user123", "user456", "user789")

    val fakeMembers = listOf(
        ShareUser(id = "user123", username = "Alice",   email = "alice@test.com",   profilePictureURL = null),
        ShareUser(id = "user456", username = "Bob",     email = "bob@test.com",     profilePictureURL = null),
        ShareUser(id = "user789", username = "Charlie", email = "charlie@test.com", profilePictureURL = null),
    )

    val fakeTeam = ShareTeam(
        id          = VALID_TEAM_ID,
        name        = VALID_TEAM_NAME,
        ownerID     = VALID_OWNER_ID,
        memberCount = fakeMembers.size,
        members     = fakeMembers
    )

    val fakeTeamList = listOf(
        fakeTeam,
        ShareTeam(id = "team456", name = "Dev Team",  ownerID = VALID_OWNER_ID, memberCount = 2, members = fakeMembers.take(2)),
        ShareTeam(id = "team789", name = "Marketing", ownerID = "other_user",   memberCount = 1, members = fakeMembers.take(1)),
    )
}
