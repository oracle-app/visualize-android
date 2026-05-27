package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

interface UserRepository {
    suspend fun getUsersByIDs(userIDs: List<String>): List<ShareUser>
    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>
    suspend fun getUserByUserID(userID: String): User?
}
