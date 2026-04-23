package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val source: TeamDatasource
) : TeamRepository {
        override suspend fun createTeam(
        memberIDs: List<String>,
        name: String,
        ownerID: String
    ){
        source.createTeam(memberIDs, name, ownerID)
    }

    override suspend fun getTeamByTeamID(teamID: String): Team? {
        return source.getTeamByTeamID(teamID)?.toDomain()
    }
}