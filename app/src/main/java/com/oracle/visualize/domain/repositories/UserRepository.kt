package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareUser

interface UserRepository {

    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>

}