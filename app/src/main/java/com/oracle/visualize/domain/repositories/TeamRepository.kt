package com.oracle.visualize.domain.repositories
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.ShareTeam

/**
 * Interface defining the operations for team management.
 */
interface TeamRepository {
    suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String): AppResult<Unit>

    suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String): AppResult<Unit>

    suspend fun getTeamsOwnedByUser(userID: String): AppResult<List<ShareTeam>>

    suspend fun deleteTeam(teamID: String): AppResult<Unit>


    suspend fun getTeamsUserIsIn(userID: String): AppResult<List<ShareTeam>>

}
