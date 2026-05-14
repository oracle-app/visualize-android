package com.oracle.visualize.data.mapper

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard

/**
 * Extension function to map [VisualizationDTO] to [Visualization] domain model.
 *
 * @return A [Visualization] object.
 */
fun VisualizationDTO.toDomain(): Visualization = Visualization(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = createdAt.toDate(),
)

/**
 * Extension function to map [Visualization] to [VisualizationDTO] Data Transfer Object.
 *
 * @return A [VisualizationDTO] object.
 */
fun Visualization.toVisualizationDTO(): VisualizationDTO = VisualizationDTO(
    id = id,
    authorID = authorID,
    title = title,
    configJSON = configJSON,
    sharedWithUsers = sharedWithUsers,
    sharedWithTeams = sharedWithTeams,
    createdAt = Timestamp(createdAt),
)

/**
 * Extension function to map [VisualizationDTO] to [VisualizationCard] domain model.
 * Used for displaying a summary of the visualization in lists/feeds.
 *
 * @param authorName The name of the visualization's author.
 * @param sharedUsers List of [User] objects representing who the visualization is shared with.
 * @return A [VisualizationCard] object.
 */
fun VisualizationDTO.toVisualizationCard(
    authorName: String,
    teamsSharedWith: List<Team> = emptyList(),
    usersSharedWith: List<User> = emptyList(),
    allUsers: List<User> = emptyList()
): VisualizationCard {
    return VisualizationCard(
        id = this.id,
        title = this.title,
        authorID = this.authorID,
        author = authorName,
        createdAt = this.createdAt.toDate(),
        teamsSharedWith = teamsSharedWith,
        usersSharedWith = usersSharedWith,
        allUsersSharedWith = allUsers,
        chart = ChartMapper.fromPreviewJson(this.previewJSON)
    )
}
