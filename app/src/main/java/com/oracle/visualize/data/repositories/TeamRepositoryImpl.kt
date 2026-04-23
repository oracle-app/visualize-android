package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDataSource
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDataSource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.models.ShareTeam
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class TeamRepositoryImpl @Inject constructor(
    private val teamsDataSource: TeamDataSource,
    private val userDataSource: UserDataSource
) : TeamRepository {

    override suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String) {
        teamsDataSource.createTeam(memberIDs, name, ownerID)
    }

    override suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam> {

        // Uses coroutines for a more efficient search since it uses parallel computing.
        return coroutineScope {
            val teamsOwnedByUserRaw: List<TeamDTO> = teamsDataSource.getTeamsUserOwns(userID)

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

    override suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam> {
        return coroutineScope {
            val teamsUserIsIn: List<TeamDTO> = teamsDataSource.getTeamsUserIsIn(userID)

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