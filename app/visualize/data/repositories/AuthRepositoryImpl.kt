package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.AuthFirebaseSource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

<<<<<<< Updated upstream
/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 *
 * @property source The [AuthFirebaseSource] to interact with Firebase Auth.
 */
class AuthRepositoryImpl @Inject constructor(private val source: AuthFirebaseSource): AuthRepository {
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
=======
class AuthRepositoryImpl
    @Inject
    constructor(
        private val source: AuthFirebaseSource,
    ) : AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): AuthUser = source.login(email, password).toDomain()

        override suspend fun register(
            email: String,
            password: String,
        ): AuthUser = source.register(email, password).toDomain()

        override fun logout() = source.logout()

        override fun getCurrentUser(): AuthUser? = source.getCurrentUser()?.toDomain()
    }
>>>>>>> Stashed changes
