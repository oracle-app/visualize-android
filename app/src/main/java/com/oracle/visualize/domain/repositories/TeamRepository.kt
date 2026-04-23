package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Team

interface TeamRepository {
    suspend fun createTeam(
        memberIDs: List<String>,
        name: String,
        ownerID: String
    )
    suspend fun getTeamByTeamID(teamID: String): Team?
}