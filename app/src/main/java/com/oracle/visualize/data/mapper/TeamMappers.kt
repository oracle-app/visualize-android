package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.Team

fun TeamDTO.toDomain(): Team = Team(
    id = id,
    memberIDs = membersIDs,
    name = name,
    ownerID = ownerID
)