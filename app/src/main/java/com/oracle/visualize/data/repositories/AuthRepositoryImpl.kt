package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.AuthFirebasesource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 *
 * @property source The [AuthFirebasesource] to interact with Firebase Auth.
 */
class AuthRepositoryImpl @Inject constructor(private val source: AuthFirebasesource): AuthRepository {
    override suspend fun login(email: String, password: String): AuthUser {
        return source.login(email,password).toDomain()
    }

    override suspend fun register(email: String, password: String): AuthUser {
        return source.register(email, password).toDomain()
    }

    override fun logout() = source.logout()

    override fun getCurrentUser(): AuthUser? {
      return source.getCurrentUser()?.toDomain()
    }
}