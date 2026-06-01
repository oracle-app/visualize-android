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
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.core.utils.safeApiCall
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.VisualizationFullScreen
import com.oracle.visualize.domain.models.VisualizationSharedData
import com.oracle.visualize.domain.repositories.VisualizationRepository
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
    ): AppResult<Unit> {
        return safeApiCall {
            visualizationDataSource.createVisualization(
                VisualizationDTO(
                    id = "", authorID = authorID, title = title,
                    configJSON = configJSON, sharedWithUsers = sharedWithUsers,
                    sharedWithTeams = sharedWithTeams, createdAt = Timestamp.now()
                )
            )
        }
    }

    override suspend fun getAllVisualizations(): AppResult<List<Visualization>> {
        return safeApiCall {
            visualizationDataSource.getAllVisualizations().map { it.toDomain() }
        }
    }

    override suspend fun getSharedVisualizations(
        userID: String,
        forceRefresh: Boolean): AppResult<List<VisualizationCard>> {
        return safeApiCall {
            val cached = feedCacheManager.cachedFeed
            if (!forceRefresh && cached != null) return@safeApiCall cached.filter { it.authorID != userID }

            val userVisualizations = visualizationDataSource.getVisualizationsSharedWithUser(userID)
            val teamIDs = teamsDatasource.getTeamsUserIsIn(userID).mapNotNull { it.id }
            val teamVisualizations = if (teamIDs.isNotEmpty())
                visualizationDataSource.getVisualizationsSharedWithTeams(teamIDs) else emptyList()
            fetchDetailsAndMapBatch((userVisualizations + teamVisualizations).distinctBy { it.id }, userID)

        }
    }

    override suspend fun getPersonalVisualizations(
        userID: String,
        forceRefresh: Boolean): AppResult<List<VisualizationCard>> {
        return safeApiCall {
            val cached = feedCacheManager.cachedFeed
            if (!forceRefresh && cached != null) return@safeApiCall cached.filter { it.authorID == userID }
            fetchDetailsAndMapBatch(visualizationDataSource.getPersonalVisualizations(userID), userID)
        }
    }

    override suspend fun getUserFeedVisualizations(
        userID: String,
        forceRefresh: Boolean): AppResult<List<VisualizationCard>> = coroutineScope {
            val cached = feedCacheManager.cachedFeed
            if (!forceRefresh && cached != null) return@coroutineScope AppResult.Success(cached)
            val sharedDeferred   = async { getSharedVisualizations(userID, forceRefresh = true) }
            val personalDeferred = async { getPersonalVisualizations(userID, forceRefresh = true) }
            val sharedResult = sharedDeferred.await()
            val personalResult = personalDeferred.await()

            if (sharedResult is AppResult.Success && personalResult is AppResult.Success) {
                val combinedFeed = sharedResult.data + personalResult.data
                feedCacheManager.cachedFeed = combinedFeed
                return@coroutineScope AppResult.Success(combinedFeed)
            } else {
                val error = if (sharedResult is AppResult.Error) {
                    sharedResult.error}
                else {
                    (personalResult as AppResult.Error).error
                }
                return@coroutineScope AppResult.Error(error)
            }
    }

    private suspend fun fetchDetailsAndMapBatch(
        dtos: List<VisualizationDTO>, userID: String
    ): List<VisualizationCard> = coroutineScope {
        if (dtos.isEmpty()) return@coroutineScope emptyList()
        val hiddenIDs   = userDatasource.getUserByID(userID).hiddenVisualizations?.toSet() ?: emptySet()
        val visibleDTOs = dtos.filter { !hiddenIDs.contains(it.id) }
        if (visibleDTOs.isEmpty()) return@coroutineScope emptyList()

        val allUserIDs    = (visibleDTOs.map { it.authorID } + visibleDTOs.flatMap { it.sharedWithUsers }).toSet().toList()
        val sharedTeamIDs = visibleDTOs.flatMap { it.sharedWithTeams }.toSet().toList()

        val usersDeferred = async { fetchUsersInChunks(allUserIDs) }
        val teamsDeferred = async { fetchTeamsInChunks(sharedTeamIDs) }
        val usersDTOs     = usersDeferred.await()
        val teamsDTOs     = teamsDeferred.await()

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

    override suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>): AppResult<Unit> {
        return safeApiCall {
            visualizationDataSource.publishVisualizationsInBulk(visualizations.map { it.toVisualizationDTO() })
        }
    }

    override suspend fun deleteVisualizationForEveryone(visualizationId: String): AppResult<Unit> {
        return safeApiCall{
            visualizationDataSource.deleteVisualization(visualizationId)
        }
    }

    // ─── feature/feed-share-and-delete methods ─────────────────────────────────

    override suspend fun getVisualizationById(visualizationId: String): VisualizationSharedData? {
        return try {
            // Reuses getIndividualVisualization datasource method — same Firestore document
            val dto = visualizationDataSource.getIndividualVisualization(visualizationId) ?: return null
            VisualizationSharedData(
                sharedWithUsers = dto.sharedWithUsers,
                sharedWithTeams = dto.sharedWithTeams
            )
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch visualization: ${e.message}")
        }
    }

    override suspend fun hideVisualizationForMe(userID: String, visualizationId: String): AppResult<Unit> {
        return safeApiCall{
            userDatasource.hideVisualizationForUser(userID, visualizationId)
        }
    }

    override suspend fun updateSharedUsers(
        visualizationId: String, userIds: List<String>, teamIds: List<String>
    ): AppResult<Unit> {
        return safeApiCall{
            visualizationDataSource.updateSharedUsers(visualizationId, userIds, teamIds)
        }
    }

    override suspend fun getIndividualVisualization(visualizationID: String): AppResult<VisualizationFullScreen?> {
        return safeApiCall {
            coroutineScope {
                val visualizationDTO =
                    visualizationDataSource.getIndividualVisualization(visualizationID) ?: return@coroutineScope null

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
            }
        }
    }
}
