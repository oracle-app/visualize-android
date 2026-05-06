package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.VisualizationDatasource
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import java.util.Date
import javax.inject.Inject

/**
 * Implementation of [VisualizationRepository] that manages visualization data.
 * It combines data from [VisualizationDatasource] and [UserDatasource] to create
 * comprehensive [VisualizationCard] objects for the UI.
 *
 * @property visualizationDataSource Data source for visualization operations.
 * @property userDatasource Data source for user information.
 */
class VisualizationRepositoryImpl @Inject constructor(
    private val visualizationDataSource: VisualizationDatasource,
    private val userDatasource: UserDatasource,
    private val teamsDatasource: TeamDatasource
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        val visualization = Visualization(
            id = "",
            authorID = authorID,
            title = title,
            configJSON = configJSON,
            sharedWithUsers = sharedWithUsers,
            sharedWithTeams = sharedWithTeams,
            createdAt = Date(),
        )
        visualizationDataSource.createVisualization(visualization)
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return visualizationDataSource.getAllVisualizations().map { it.toDomain() }
    }

    override suspend fun getSharedVisualizations(userID: String): List<VisualizationCard> {
        val dtos = visualizationDataSource.getAllSharedVisualizations(userID)
        return fetchDetailsAndMap(dtos)
    }

    override suspend fun getPersonalVisualizations(userID: String): List<VisualizationCard> {
        val dtos = visualizationDataSource.getPersonalVisualizations(userID)
        return fetchDetailsAndMap(dtos)
    }

    private suspend fun fetchDetailsAndMap(dtos: List<VisualizationDTO>): List<VisualizationCard> {
        val cards = mutableListOf<VisualizationCard>()
        for (dto in dtos) {
            val authorDTO = userDatasource.getUserByID(dto.authorID)
            val authorName = authorDTO.username

            val usersDTOs = userDatasource.getUsersByIDs(dto.sharedWithUsers)
            val usersSharedWith = usersDTOs.map { it.toDomain() }

            val teamsDTOs = teamsDatasource.getTeamsByIDs(dto.sharedWithTeams)

            val uniqueMemberIDs = teamsDTOs.flatMap { it.membersIDs }.toSet().toList()
            val membersDTOs = userDatasource.getUsersByIDs(uniqueMemberIDs)
            val allMembers = membersDTOs.map { it.toDomain() }

            val teamsSharedWith = teamsDTOs.map { teamDTO ->
                val specificTeamMembers = allMembers.filter { member ->
                    teamDTO.membersIDs.contains(member.id)
                }
                teamDTO.toDomain(specificTeamMembers)
            }

            val card = dto.toVisualizationCard(
                authorName = authorName,
                teamsSharedWith = teamsSharedWith,
                usersSharedWith = usersSharedWith
            )
            cards.add(card)
        }
        return cards
    }
    override suspend fun deleteVisualization(visualizationID: String) {
        visualizationDataSource.deleteVisualization(visualizationID)
    }

    override suspend fun shareVisualizationWithUsers(visualizationID: String, userIDs: List<String>) {
        visualizationDataSource.shareVisualizationWithUsers(visualizationID, userIDs)
    }

    override suspend fun shareVisualizationWithTeams(visualizationID: String, teamIDs: List<String>) {
        visualizationDataSource.shareVisualizationWithTeams(visualizationID, teamIDs)
    }

    override suspend fun deleteUsersAccessToVisualization(
        visualizationID: String,
        userIDs: List<String>
    ) {
        visualizationDataSource.deleteUsersAccessToVisualization(visualizationID, userIDs)
    }

    override suspend fun deleteTeamsAccessToVisualization(
        visualizationID: String,
        teamIDs: List<String>
    ) {
        visualizationDataSource.deleteTeamsAccessToVisualization(visualizationID, teamIDs)
    }
}