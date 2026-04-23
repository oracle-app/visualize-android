package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.TeamDto
import com.oracle.visualize.domain.models.Team

fun TeamDto.toDomain(): Team = Team(
    id = id,
    memberIDs = memberIDs,
    name = name,
    ownerID = ownerID
)