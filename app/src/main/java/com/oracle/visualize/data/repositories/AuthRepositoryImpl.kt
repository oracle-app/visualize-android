package com.oracle.visualize.data.repositories

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.AuthFirebasesource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 *
 * @property authDatasource The [AuthFirebasesource] to interact with Firebase Auth.
 */
class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthFirebasesource,
    private val userDatasource: UserDatasource

    ): AuthRepository {
    override suspend fun login(email: String, password: String): AuthUser {

        return try {
            authDatasource.login(email, password).toDomain()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AppError.InvalidCredentials()
        } catch (e: FirebaseNetworkException){
            throw AppError.NetworkError("No internet connection.")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.AuthFailed(e.message ?: "An unexpected login error occurred.")
        }
    }

    override suspend fun register(name: String, email: String, password: String): AuthUser {
        return try {

            // 1. Register in Firebase Auth (Receive a DTO or data model)
            val authUserDTO = authDatasource.register(email, password)

            // 2. Map to the Domain Entity
            val authUser = authUserDTO.toDomain()

            // 3. Persist the profile in Firestore using the raw ID (String)
            val userDto = UserDTO(username = name, email = email)
            userDatasource.saveUserProfile(authUser.uid, userDto)

            // 4. Return the raw entity to fulfill the contract
            authUser

        } catch (e: FirebaseAuthUserCollisionException) {
            throw AppError.EmailAlreadyExists("This email is already registered.")
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw AppError.AuthValidationError(
                field = AppError.AuthField.PASSWORD,
                message = "Password must be at least 6 characters."
            )
        } catch (e: FirebaseNetworkException) {
            throw AppError.NetworkError("No internet connection.")

        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.AuthFailed(e.message ?: "Register error")
        }
    }

    override fun logout() = authDatasource.logout()

    override fun getCurrentUser(): AuthUser? {
      return authDatasource.getCurrentUser()?.toDomain()
    }

    override fun getCurrentUserID(): String {
        val currentUser = authDatasource.getCurrentUser()

        return currentUser?.uid ?: throw AppError.AuthFailed("No user logged in")
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            authDatasource.resetPassword(email)
            Result.success(Unit)

        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.AuthFailed(e.message ?: "Reset password error")
        }
    }

}

