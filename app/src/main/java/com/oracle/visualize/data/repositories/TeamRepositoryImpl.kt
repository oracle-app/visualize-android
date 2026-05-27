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

/**
 * Implementation of [TeamRepository] that coordinates team-related data operations.
 * It uses both [TeamDatasource] and [UserDatasource] to fetch complete team information,
 * including member details.
 *
 * @property teamsDatasource Data source for team operations in Firestore.
 * @property userDataSource Data source for user operations in Firestore.
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

    override suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam> {

        // Uses coroutines for a more efficient search since it uses parallel computing.
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
