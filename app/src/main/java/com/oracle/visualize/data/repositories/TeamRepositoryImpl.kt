package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.core.utils.safeApiCall
import com.oracle.visualize.domain.models.ShareTeam
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class TeamRepositoryImpl @Inject constructor(
    private val teamsDatasource: TeamDatasource,
    private val userDataSource: UserDatasource
) : TeamRepository {

    override suspend fun deleteTeam(teamID: String): AppResult<Unit> {
        return safeApiCall {
            teamsDatasource.deleteTeam(teamID)
        }
    }

    override suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String): AppResult<Unit> {
        return safeApiCall {
            teamsDatasource.createTeam(memberIDs, name, ownerID)
        }
    }

    override suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String): AppResult<Unit> {
        return safeApiCall {
            teamsDatasource.updateTeam(teamID, memberIDs, name)
        }
    }

    override suspend fun getTeamsOwnedByUser(userID: String): AppResult<List<ShareTeam>> {
        return safeApiCall {
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
        }
    }

    override suspend fun getTeamsUserIsIn(userID: String): AppResult<List<ShareTeam>> {
        return safeApiCall {
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
        }
    }
}
