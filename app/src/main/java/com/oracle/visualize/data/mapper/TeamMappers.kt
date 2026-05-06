package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User

/**
 * Extension function to map [TeamDTO] to [Team] domain model.
 *
 * @return A [Team] object containing the same data as the DTO.
 */
fun TeamDTO.toDomain(members: List<User>): Team = Team(
    id = id,
    members = members,
    name = name,
    ownerID = ownerID
)