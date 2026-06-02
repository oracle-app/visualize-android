package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.AuthUser

/**
 * Interface defining the operations for authentication.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<AuthUser>
    suspend fun register(name: String, email: String, password: String): AppResult<AuthUser>
    fun logout()
    fun getCurrentUser(): AuthUser?

    fun getCurrentUserID(): String?
    suspend fun resetPassword(email: String): AppResult<Unit>
}
