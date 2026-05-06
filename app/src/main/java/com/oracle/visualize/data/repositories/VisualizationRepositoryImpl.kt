package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.VisualizationDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Date
import javax.inject.Inject
import kotlin.collections.flatMap

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
        return fetchDetailsAndMapBatch(dtos, userID)
    }

    override suspend fun getPersonalVisualizations(userID: String): List<VisualizationCard> {
        val dtos = visualizationDataSource.getPersonalVisualizations(userID)
        return fetchDetailsAndMapBatch(dtos, userID)
    }

    private suspend fun fetchDetailsAndMapBatch(
        dtos: List<VisualizationDTO>,
        userID: String
    ): List<VisualizationCard> = coroutineScope {
        if (dtos.isEmpty()) return@coroutineScope emptyList()

        val currentUser = userDatasource.getUserByID(userID)
        val hiddenIDs = currentUser.hiddenVisualizations?.toSet() ?: emptySet()

        val visibleDTOs = dtos.filter { dto ->
            val id = dto.id ?: return@filter false
            !hiddenIDs.contains(id)
        }

        if (visibleDTOs.isEmpty()) return@coroutineScope emptyList()

        val authorIDs = visibleDTOs.map { it.authorID }.toSet()
        val sharedUserIDs = visibleDTOs.flatMap { it.sharedWithUsers }.toSet()
        val allUserIDsToFetch = (authorIDs + sharedUserIDs).toList()

        val sharedTeamIDs = visibleDTOs.flatMap { it.sharedWithTeams }.toSet().toList()

        val usersDeferred = async { fetchUsersInChunks(allUserIDsToFetch) }
        val teamsDeferred = async { fetchTeamsInChunks(sharedTeamIDs) }

        val usersDTOs = usersDeferred.await()
        val teamsDTOs = teamsDeferred.await()

        val usersDict = usersDTOs.associateBy { it.id }.toMutableMap()
        val teamsDict = teamsDTOs.associateBy { it.id }

        val teamMemberIDs = teamsDTOs.flatMap { it.membersIDs }.toSet()
        val missingUserIDs = teamMemberIDs.filter { !usersDict.containsKey(it) }

        if (missingUserIDs.isNotEmpty()) {
            val missingUsers = fetchUsersInChunks(missingUserIDs)
            usersDict.putAll(missingUsers.associateBy { it.id })
        }

        visibleDTOs.map { dto ->
            val authorName = usersDict[dto.authorID]?.username ?: "Unknown"

            val usersSharedWith = dto.sharedWithUsers.mapNotNull { usersDict[it]?.toDomain() }

            val teamsSharedWith = dto.sharedWithTeams.mapNotNull { teamID ->
                val teamDTO = teamsDict[teamID] ?: return@mapNotNull null
                val specificTeamMembers = teamDTO.membersIDs.mapNotNull { usersDict[it]?.toDomain() }
                teamDTO.toDomain(specificTeamMembers)
            }

            dto.toVisualizationCard(
                authorName = authorName,
                teamsSharedWith = teamsSharedWith,
                usersSharedWith = usersSharedWith
            )
        }
    }

    private suspend fun fetchUsersInChunks(ids: List<String>): List<UserDTO> {
        val allUsers = mutableListOf<UserDTO>()
        val chunkSize = 30

        // chunked() es equivalente al stride de Swift para dividir el array
        ids.chunked(chunkSize).forEach { chunk ->
            val chunkUsers = userDatasource.getUsersByIDs(chunk)
            allUsers.addAll(chunkUsers)
        }

        return allUsers
    }

    private suspend fun fetchTeamsInChunks(ids: List<String>): List<TeamDTO> {
        val allTeams = mutableListOf<TeamDTO>()
        val chunkSize = 30

        ids.chunked(chunkSize).forEach { chunk ->
            val chunkTeams = teamsDatasource.getTeamsByIDs(chunk)
            allTeams.addAll(chunkTeams)
        }

        return allTeams
    }
}