package com.oracle.visualize.data.repositories

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.VisualizationDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.datasources.local.FeedCacheManager
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.data.mapper.toVisualizationDTO
import com.oracle.visualize.data.mapper.toVisualizationFullScreen
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.VisualizationFullScreen
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class VisualizationRepositoryImpl @Inject constructor(
    private val visualizationDataSource: VisualizationDatasource,
    private val userDatasource: UserDatasource,
    private val teamsDatasource: TeamDatasource,
    private val feedCacheManager: FeedCacheManager
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String, title: String, configJSON: String,
        sharedWithUsers: List<String>, sharedWithTeams: List<String>
    ) {
        try {
            visualizationDataSource.createVisualization(
                VisualizationDTO(
                    id = "", authorID = authorID, title = title,
                    configJSON = configJSON, sharedWithUsers = sharedWithUsers,
                    sharedWithTeams = sharedWithTeams, createdAt = Timestamp.now()
                )
            )
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

    override suspend fun getSharedVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard> {
        val cached = feedCacheManager.cachedFeed
        if (!forceRefresh && cached != null) return cached.filter { it.authorID != userID }
        return try {
            val userVisualizations = visualizationDataSource.getVisualizationsSharedWithUser(userID)
            val teamIDs = teamsDatasource.getTeamsUserIsIn(userID).mapNotNull { it.id }
            val teamVisualizations = if (teamIDs.isNotEmpty())
                visualizationDataSource.getVisualizationsSharedWithTeams(teamIDs) else emptyList()
            fetchDetailsAndMapBatch((userVisualizations + teamVisualizations).distinctBy { it.id }, userID)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch shared visualizations: ${e.message}")
        }
    }

    override suspend fun getPersonalVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard> {
        val cached = feedCacheManager.cachedFeed
        if (!forceRefresh && cached != null) return cached.filter { it.authorID == userID }
        return try {
            fetchDetailsAndMapBatch(visualizationDataSource.getPersonalVisualizations(userID), userID)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch personal visualizations: ${e.message}")
        }
    }

    override suspend fun getUserFeedVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard> = coroutineScope {
        val cached = feedCacheManager.cachedFeed
        if (!forceRefresh && cached != null) return@coroutineScope cached
        val sharedDeferred = async { getSharedVisualizations(userID, forceRefresh = true) }
        val personalDeferred = async { getPersonalVisualizations(userID, forceRefresh = true) }
        val combinedFeed = (sharedDeferred.await() + personalDeferred.await())
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt }
        feedCacheManager.cachedFeed = combinedFeed
        return@coroutineScope combinedFeed
    }

    private suspend fun fetchDetailsAndMapBatch(
        dtos: List<VisualizationDTO>, userID: String
    ): List<VisualizationCard> = coroutineScope {
        if (dtos.isEmpty()) return@coroutineScope emptyList()
        val hiddenIDs   = userDatasource.getUserByID(userID).hiddenVisualizations?.toSet() ?: emptySet()
        val visibleDTOs = dtos
            .filter { !hiddenIDs.contains(it.id) }
            .sortedByDescending { it.createdAt }

        if (visibleDTOs.isEmpty()) return@coroutineScope emptyList()

        val allUserIDs  = (visibleDTOs.map { it.authorID } + visibleDTOs.flatMap { it.sharedWithUsers }).toSet().toList()
        val sharedTeamIDs = visibleDTOs.flatMap { it.sharedWithTeams }.toSet().toList()

        val usersDeferred = async { fetchUsersInChunks(allUserIDs) }
        val teamsDeferred = async { fetchTeamsInChunks(sharedTeamIDs) }
        val usersDTOs = usersDeferred.await()
        val teamsDTOs = teamsDeferred.await()

        val usersDict = usersDTOs.associateBy { it.id }.toMutableMap()
        val teamsDict = teamsDTOs.associateBy { it.id }

        val missingIDs = teamsDTOs.flatMap { it.membersIDs }.filter { !usersDict.containsKey(it) }
        if (missingIDs.isNotEmpty()) usersDict.putAll(fetchUsersInChunks(missingIDs).associateBy { it.id })

        visibleDTOs.map { dto ->
            dto.toVisualizationCard(
                authorName      = usersDict[dto.authorID]?.username ?: "Unknown",
                usersSharedWith = dto.sharedWithUsers.mapNotNull { usersDict[it]?.toDomain() },
                teamsSharedWith = dto.sharedWithTeams.mapNotNull { teamID ->
                    val t = teamsDict[teamID] ?: return@mapNotNull null
                    t.toDomain(t.membersIDs.mapNotNull { usersDict[it]?.toDomain() })
                }
            )
        }
    }

    private suspend fun fetchUsersInChunks(ids: List<String>): List<UserDTO> {
        val result = mutableListOf<UserDTO>()
        ids.chunked(10).forEach { result.addAll(userDatasource.getUsersByIDs(it)) }
        return result
    }

    private suspend fun fetchTeamsInChunks(ids: List<String>): List<TeamDTO> {
        val result = mutableListOf<TeamDTO>()
        ids.chunked(30).forEach { result.addAll(teamsDatasource.getTeamsByIDs(it)) }
        return result
    }

    override suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>) {
        try {
            visualizationDataSource.publishVisualizationsInBulk(visualizations.map { it.toVisualizationDTO() })
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to publish visualizations: ${e.message}")
        }
    }

    override suspend fun deleteVisualizationForEveryone(visualizationId: String) {
        try {
            visualizationDataSource.deleteVisualization(visualizationId)
        } catch (e: TimeoutCancellationException) {
            throw AppError.NetworkError("Delete timed out. Check your connection and try again.")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to delete visualization: ${e.message}")
        }
    }

    override suspend fun hideVisualizationForMe(userID: String, visualizationId: String) {
        try {
            userDatasource.hideVisualizationForUser(userID, visualizationId)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to hide visualization for user: ${e.message}")
        }
    }

    override suspend fun updateSharedUsers(
        visualizationId: String, userIds: List<String>, teamIds: List<String>
    ) {
        try {
            visualizationDataSource.updateSharedUsers(visualizationId, userIds, teamIds)
        } catch (e: TimeoutCancellationException) {
            throw AppError.NetworkError("Share timed out. Check your connection and try again.")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to update shared users: ${e.message}")
        }
    }

    override suspend fun getIndividualVisualization(visualizationID: String): VisualizationFullScreen? = coroutineScope {
        try {
            val visualizationDTO = visualizationDataSource.getIndividualVisualization(visualizationID) ?: return@coroutineScope null

            val authorID = visualizationDTO.authorID
            val sharedWithTeams = visualizationDTO.sharedWithTeams
            val sharedWithUsers = visualizationDTO.sharedWithUsers

            val allUserIDsToFetch = listOf(authorID) + sharedWithUsers
            val sharedTeamIDs = sharedWithTeams.toSet().toList()

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

            val authorName = usersDict[authorID]?.username ?: "Unknown"
            val usersSharedWith = sharedWithUsers.mapNotNull { usersDict[it]?.toDomain() }
            val teamsSharedWith = sharedWithTeams.mapNotNull { teamID ->
                val teamDTO = teamsDict[teamID] ?: return@mapNotNull null
                val specificTeamMembers = teamDTO.membersIDs.mapNotNull { usersDict[it]?.toDomain() }
                teamDTO.toDomain(specificTeamMembers)
            }

            visualizationDTO.toVisualizationFullScreen(
                authorName = authorName,
                teamsSharedWith = teamsSharedWith,
                usersSharedWith = usersSharedWith
            )
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to get visualization: ${e.message}")
        }
    }
}
