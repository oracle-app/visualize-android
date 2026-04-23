package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
): UserRepository {
    override suspend fun getUserByUserID(userId: String): User {
        return userDatasource.getUserByUserID(userId).toDomain()
    }

    override suspend fun getTeamsIntegratedByUser(userId: String): List<Team> {
        return userDatasource.getTeamsIntegratedByUser(userId).map { it.toDomain() }
    }
}