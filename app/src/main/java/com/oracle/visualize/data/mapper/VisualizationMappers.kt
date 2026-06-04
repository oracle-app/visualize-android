package com.oracle.visualize.data.mapper

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.VisualizationFullScreen

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
    previewJSON = previewJSON
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
    previewJSON = previewJSON
)

/**
 * Extension function to map [VisualizationDTO] to [VisualizationCard] domain model.
 * Used for displaying a summary of the visualization in lists/feeds.
 *
 * @param authorName The name of the visualization's author.
 * @param teamsSharedWith List of [Team] objects representing whom the visualization is shared with.
 * @param usersSharedWith List of [User] objects representing whom the visualization is shared with.
 * @return A [VisualizationCard] object.
 */
fun VisualizationDTO.toVisualizationCard(
    authorName: String,
    teamsSharedWith: List<Team>,
    usersSharedWith: List<User>): VisualizationCard {
    val allUsersDict = mutableMapOf<String, User>()

    for (user in usersSharedWith) {
        allUsersDict[user.id] = user
    }

    for (team in teamsSharedWith) {
        for (member in team.members) {
            allUsersDict[member.id] = member
        }
    }

    val allUsers = allUsersDict.values.toList().sortedBy { it.username }

    return VisualizationCard(
        id = this.id,
        title = this.title,
        authorID = this.authorID,
        author = authorName,
        createdAt = this.createdAt.toDate(),
        teamsSharedWith = teamsSharedWith,
        usersSharedWith = usersSharedWith,
        allUsersSharedWith = allUsers,
        previewJSON = this.previewJSON
    )
}

/**
 * Extension function to map [VisualizationDTO] to [VisualizationFullScreen] domain model.
 * Used for displaying a summary of the visualization in lists/feeds.
 *
 * @param authorName The name of the visualization's author.
 * @param teamsSharedWith List of [Team] objects representing whom the visualization is shared with.
 * @param usersSharedWith List of [User] objects representing whom the visualization is shared with.
 * @return A [VisualizationFullScreen] object.
 */
fun VisualizationDTO.toVisualizationFullScreen(
    authorName: String,
    teamsSharedWith: List<Team>,
    usersSharedWith: List<User>): VisualizationFullScreen {
    val allUsersDict = mutableMapOf<String, User>()

    for (user in usersSharedWith) {
        allUsersDict[user.id] = user
    }

    for (team in teamsSharedWith) {
        for (member in team.members) {
            allUsersDict[member.id] = member
        }
    }

    val allUsers = allUsersDict.values.toList().sortedBy { it.username }

    return VisualizationFullScreen(
        id = this.id,
        title = this.title,
        authorID = this.authorID,
        author = authorName,
        createdAt = this.createdAt.toDate(),
        teamsSharedWith = teamsSharedWith,
        usersSharedWith = usersSharedWith,
        allUsersSharedWith = allUsers,
        configJSON = this.configJSON
    )
}
