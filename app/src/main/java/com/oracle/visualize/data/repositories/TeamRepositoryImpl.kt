package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareTeam
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class TeamRepositoryImpl @Inject constructor(
    private val teamsDatasource: TeamDatasource,
    private val userDataSource: UserDatasource
) : TeamRepository {

    override suspend fun deleteTeam(teamID: String) {
        try {
            teamsDatasource.deleteTeam(teamID)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to delete team: ${e.message}")
        }
    }

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
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to update team: ${e.message}")
        }
    }

    override suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam> {
        return try {
            coroutineScope {
                val teamsOwnedByUserRaw: List<TeamDTO> = teamsDatasource.getTeamsUserOwns(userID)
                val deferredTeams = teamsOwnedByUserRaw.map { teamDTO ->
                    async {
                        val deferredUsers = teamDTO.membersIDs.map { id ->
                            async { userDataSource.getUserByID(id) }
                        }
                        val rawUsers: List<UserDTO> = deferredUsers.awaitAll()
                        val users = rawUsers.map { dto -> dto.toShareUser() }
                        teamDTO.toShareTeam(users)
                    }
                }
                deferredTeams.awaitAll()
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
                val teamsUserIsIn: List<TeamDTO> = teamsDatasource.getTeamsUserIsIn(userID)
                val deferredTeams = teamsUserIsIn.map { teamDTO ->
                    async {
                        val deferredUsers = teamDTO.membersIDs.map { id ->
                            async { userDataSource.getUserByID(id) }
                        }
                        val rawUsers: List<UserDTO> = deferredUsers.awaitAll()
                        val users = rawUsers.map { dto -> dto.toShareUser() }
                        teamDTO.toShareTeam(users)
                    }
                }
                deferredTeams.awaitAll()
            }
        } catch (e: AppError) {
            throw e
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to fetch user is in ${e.message}")
        }
    }
}
