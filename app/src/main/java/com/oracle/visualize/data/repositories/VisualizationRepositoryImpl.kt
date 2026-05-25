package com.oracle.visualize.data.repositories

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.VisualizationDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.data.mapper.toVisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        try {
            val dto = VisualizationDTO(
                id = "",
                authorID = authorID,
                title = title,
                configJSON = configJSON,
                sharedWithUsers = sharedWithUsers,
                sharedWithTeams = sharedWithTeams,
                createdAt = Timestamp.now(),
            )
            visualizationDataSource.createVisualization(dto)

        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to create visualization: ${e.message}")
        }

    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return try {
            visualizationDataSource.getAllVisualizations().map { it.toDomain() }
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch all visualizations: ${e.message}")
        }
    }

    override suspend fun getSharedVisualizations(userID: String): List<VisualizationCard> {
        return try {
            val userVisualizations = visualizationDataSource.getVisualizationsSharedWithUser(userID)

            val userTeams = teamsDatasource.getTeamsUserIsIn(userID)
            val teamIDs = userTeams.mapNotNull { it.id }

            val teamVisualizations = if (teamIDs.isNotEmpty()) {
                visualizationDataSource.getVisualizationsSharedWithTeams(teamIDs)
            } else {
                emptyList()
            }

            val allSharedDTOs = (userVisualizations + teamVisualizations).distinctBy { it.id }

            fetchDetailsAndMapBatch(allSharedDTOs, userID)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch shared visualizations: ${e.message}")
        }
    }

    override suspend fun getPersonalVisualizations(userID: String): List<VisualizationCard> {
        return try {
            val dtos = visualizationDataSource.getPersonalVisualizations(userID)
            fetchDetailsAndMapBatch(dtos, userID)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch personal visualizations: ${e.message}")
        }

    }

    private suspend fun fetchDetailsAndMapBatch(
        dtos: List<VisualizationDTO>,
        userID: String
    ): List<VisualizationCard> = coroutineScope {
        if (dtos.isEmpty()) return@coroutineScope emptyList()

        val currentUser = userDatasource.getUserByID(userID)
        val hiddenIDs = currentUser.hiddenVisualizations?.toSet() ?: emptySet()

        val visibleDTOs = dtos.filter { !hiddenIDs.contains(it.id) }
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
        val chunkSize = 10

        // ´chunked()´ is equivalent to Swift's ´stride´ for splitting the array
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

    override suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>) {
        try{
            val visualizationsDTO = visualizations.map { it.toVisualizationDTO() }
            visualizationDataSource.publishVisualizationsInBulk(visualizationsDTO)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to publish visualizations: ${e.message}")
        }

    }
}
