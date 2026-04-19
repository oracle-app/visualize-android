package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthUser
    suspend fun register(email: String, password: String): AuthUser
    fun logout()
    fun getCurrentUser(): AuthUser?
}