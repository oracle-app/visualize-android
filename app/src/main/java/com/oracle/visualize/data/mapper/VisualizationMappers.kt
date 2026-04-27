package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import kotlin.collections.map

fun VisualizationDTO.toDomain(): Visualization = Visualization(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = createdAt.toDate(),
)

fun VisualizationDTO.toVisualizationCard(authorName: String, sharedUsers: List<User>): VisualizationCard {
    return VisualizationCard(
        id = this.id,
        title = this.title,
        author = authorName,
        createdAt = this.createdAt.toDate(),
        configJSON = this.configJSON,
        sharedWith = sharedUsers
    )
}