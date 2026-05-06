package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard

<<<<<<< Updated upstream
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
 * Extension function to map [VisualizationDTO] to [VisualizationCard] domain model.
 * Used for displaying a summary of the visualization in lists/feeds.
 *
 * @param authorName The name of the visualization's author.
 * @param sharedUsers List of [User] objects representing who the visualization is shared with.
 * @return A [VisualizationCard] object.
 */
fun VisualizationDTO.toVisualizationCard(authorName: String, sharedUsers: List<User>): VisualizationCard {
    return VisualizationCard(
=======
fun VisualizationDTO.toDomain(): Visualization =
    Visualization(
        id = id,
        authorID = authorID,
        title = title,
        configJSON = configJSON,
        sharedWithUsers = sharedWithUsers,
        sharedWithTeams = sharedWithTeams,
        createdAt = createdAt,
    )

fun VisualizationDTO.toVisualizationCard(
    authorName: String,
    sharedUsers: List<User>,
): VisualizationCard =
    VisualizationCard(
>>>>>>> Stashed changes
        id = this.id,
        title = this.title,
        author = authorName,
        createdAt = this.createdAt.toDate(),
        configJSON = this.configJSON,
        sharedWith = sharedUsers,
    )
