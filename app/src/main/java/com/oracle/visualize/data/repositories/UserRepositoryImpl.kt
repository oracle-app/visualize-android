package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.UserDataSource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.datasources.dtos.TeamDto
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val source: UserDataSource
): UserRepository {
    override suspend fun getUserByUserID(userId: String): User? {
        return source.getUserByUserID(userId)
    }

    override suspend fun getTeamsIntegratedByUser(userId: String): List<Team> {
        return source.getTeamsIntegratedByUser(userId).map { it.toDomain() }
    }
}