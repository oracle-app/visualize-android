package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

/**
 * Interface defining the operations for user management.
 */
interface UserRepository {
    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>
    suspend fun getUserByUserID(userID: String): User?
}
