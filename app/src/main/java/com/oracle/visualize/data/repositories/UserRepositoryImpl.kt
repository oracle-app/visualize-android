package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.UserDataSource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val source: UserDataSource
) {
    suspend fun getUserByUserID(userId: String): User? {
        return source.getUserByUserID(userId)
    }

    suspend fun getTeamsIntegratedByUser(userId: String): List<Team> {
        return source.getTeamsIntegratedByUser(userId)
    }
}