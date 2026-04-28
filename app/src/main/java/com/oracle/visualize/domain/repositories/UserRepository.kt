package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User

/**
 * Interface defining the operations for user management.
 */
interface UserRepository {
    suspend fun getUserByUserID(userId: String): User?
    suspend fun getTeamsIntegratedByUser(userId: String): List<Team>
    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>

}