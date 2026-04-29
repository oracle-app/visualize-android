package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.AuthFirebaseSource
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 *
 * @property authDataSource The [AuthFirebaseSource] to interact with Firebase Auth.
 */
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthFirebaseSource,
    private val userDataSource: UserDatasource

    ): AuthRepository {
    override suspend fun login(email: String, password: String): AuthUser {
        return authDataSource.login(email,password).toDomain()
    }

    override suspend fun register(name: String, email: String, password: String): AuthUser {

        // 1. Register in Firebase Auth (Receive a DTO or data model)
        val authUserDTO = authDataSource.register(email, password)

        // 2. Map to the Domain Entity
        val authUser = authUserDTO.toDomain()

        // 3. Persist the profile in Firestore using the raw ID (String)
        val userDto = UserDTO(username = name, email = email)
        userDataSource.saveUserProfile(authUser.uid, userDto)

        // 4. Return the raw entity to fulfill the contract
        return authUser
    }

    override fun logout() = authDataSource.logout()

    override fun getCurrentUser(): AuthUser? = authDataSource.getCurrentUser()?.toDomain()
}