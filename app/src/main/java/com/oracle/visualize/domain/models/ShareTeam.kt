package com.oracle.visualize.domain.models

/**
 * Domain model representing a team for sharing purposes.
 */
data class ShareTeam(
    val id: String,
    val name: String,
    val memberCount: Int,
    val members: List<ShareUser> = emptyList()
)