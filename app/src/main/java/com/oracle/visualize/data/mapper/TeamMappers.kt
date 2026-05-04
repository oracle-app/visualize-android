package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.Team

/**
 * Extension function to map [TeamDTO] to [Team] domain model.
 *
 * @return A [Team] object containing the same data as the DTO.
 */
fun TeamDTO.toDomain(): Team = Team(
    id = id,
    memberIDs = membersIDs,
    name = name,
    ownerID = ownerID
)