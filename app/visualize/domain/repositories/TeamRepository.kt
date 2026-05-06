package com.oracle.visualize.domain.repositories
import com.oracle.visualize.domain.models.ShareTeam

/**
 * Interface defining the operations for team management.
 */
interface TeamRepository {
    suspend fun createTeam(
        memberIDs: List<String>,
        name: String,
        ownerID: String,
    )

    suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam>

    suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam>
}
