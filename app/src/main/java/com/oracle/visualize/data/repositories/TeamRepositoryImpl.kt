package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.repositories.TeamRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamsDatasource: TeamDatasource,
    private val userDataSource: UserDatasource
) : TeamRepository {

    override suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String) {
        teamsDatasource.createTeam(memberIDs, name, ownerID)
    }

    override suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String) {
        teamsDatasource.updateTeam(teamID, memberIDs, name)
    }

    override suspend fun deleteTeam(teamID: String) {
        teamsDatasource.deleteTeam(teamID)
    }

    override suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam> {
        return coroutineScope {
            val teamsRaw: List<TeamDTO> = teamsDatasource.getTeamsUserOwns(userID)
            teamsRaw.map { teamDTO ->
                async {
                    val users: List<UserDTO> = teamDTO.membersIDs
                        .map { id -> async { userDataSource.getUserByID(id) } }
                        .awaitAll()
                    teamDTO.toShareTeam(users.map { it.toShareUser() })
                }
            }.awaitAll()
        }
    }

    override suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam> {
        return coroutineScope {
            val teamsRaw: List<TeamDTO> = teamsDatasource.getTeamsUserIsIn(userID)
            teamsRaw.map { teamDTO ->
                async {
                    val users: List<UserDTO> = teamDTO.membersIDs
                        .map { id -> async { userDataSource.getUserByID(id) } }
                        .awaitAll()
                    teamDTO.toShareTeam(users.map { it.toShareUser() })
                }
            }.awaitAll()
        }
    }
}