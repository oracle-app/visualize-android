package com.oracle.visualize.domain.models

/**
 * Domain model representing a Team.
 */
data class Team (
    val id: String,
    val memberIDs: List<String>,
    val name: String,
    val ownerID: String
)