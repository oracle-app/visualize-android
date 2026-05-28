package com.oracle.visualize.domain.models

/**
 * Domain model representing a team for sharing and display purposes.
 *
 * @property id Unique identifier of the team.
 * @property name Display name of the team.
 * @property ownerID The user ID of the team owner.
 * @property memberCount Total number of members.
 * @property members Resolved list of [ShareUser] members.
 */
data class ShareTeam(
    val id: String,
    val name: String,
    val ownerID: String = "",
    val memberCount: Int,
    val members: List<ShareUser> = emptyList()
)
