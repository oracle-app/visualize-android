package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.VisualizationDto
import com.oracle.visualize.domain.models.Visualization

fun VisualizationDto.toDomain(): Visualization = Visualization(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = createdAt,
    comments = comments.map { it.toDomain() }
)