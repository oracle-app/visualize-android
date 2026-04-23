package com.oracle.visualize.domain.repositories
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.Team

interface TeamRepository {
    suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String)

    suspend fun getTeamsOwnedByUser(userID: String): List<ShareTeam>

    suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam>

}