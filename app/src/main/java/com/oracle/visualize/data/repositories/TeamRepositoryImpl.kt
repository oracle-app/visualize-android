package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.repositories.TeamRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Implementation of [TeamRepository].
 *
 * Owner resolution:
 *   New teams always have the ownerID included in membersIDs (enforced by
 *   TeamDatasource.createTeam). For legacy documents that pre-date this
 *   contract, [resolveMembersIncludingOwner] ensures the owner is fetched
 *   and prepended to the members list so the UI always shows them.
 */
class TeamRepositoryImpl @Inject constructor(
    private val teamsDatasource: TeamDatasource,
    private val userDataSource: UserDatasource
) : TeamRepository {

    override suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String) {
        try {
            teamsDatasource.createTeam(memberIDs, name, ownerID)
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to create team: ${e.message}")
        }
    }

    override suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String) {
        try {
            teamsDatasource.updateTeam(teamID, memberIDs, name)
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to update team: ${e.message}")
        }
    }

    override suspend fun deleteTeam(teamID: String) {
        try {
            teamsDatasource.deleteTeam(teamID)
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to delete team: ${e.message}")
        }
    }

    override suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam> {
        return try {
            coroutineScope {
                val rawTeams: List<TeamDTO> = teamsDatasource.getTeamsUserOwns(userID)
                rawTeams.map { teamDTO ->
                    async { resolveMembersIncludingOwner(teamDTO) }
                }.awaitAll()
            }
        } catch (e: AppError) {
            throw e
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to fetch owned teams: ${e.message}")
        }
    }

    override suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam> {
        return try {
            coroutineScope {
                val rawTeams: List<TeamDTO> = teamsDatasource.getTeamsUserIsIn(userID)
                rawTeams.map { teamDTO ->
                    async { resolveMembersIncludingOwner(teamDTO) }
                }.awaitAll()
            }
        } catch (e: AppError) {
            throw e
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to fetch teams user is in: ${e.message}")
        }
    }

    /**
     * Resolves all member [ShareUser] objects for a [TeamDTO], guaranteeing
     * the owner is always present in the returned list.
     *
     * Steps:
     *  1. Collect the unique IDs to fetch: membersIDs union {ownerID}
     *  2. Fetch all users in parallel
     *  3. Sort so the owner appears first
     */
    private suspend fun resolveMembersIncludingOwner(teamDTO: TeamDTO): ShareTeam {
        return coroutineScope {
            // Ensure ownerID is included even for legacy documents
            val allIDs = (teamDTO.membersIDs + teamDTO.ownerID).distinct()

            val deferredUsers = allIDs.map { id ->
                async { userDataSource.getUserByID(id) }
            }
            val rawUsers: List<UserDTO> = deferredUsers.awaitAll()
            val users = rawUsers
                .map { dto -> dto.toShareUser() }
                // Owner first, then the rest in original order
                .sortedByDescending { it.id == teamDTO.ownerID }

            teamDTO.toShareTeam(users)
        }
    }
}
