package com.oracle.visualize.domain.models

/**
 * Domain model representing a Team.
 */
data class Team (
    val id: String,
    val members: List<User> = emptyList(),
    val name: String,
    val ownerID: String
)