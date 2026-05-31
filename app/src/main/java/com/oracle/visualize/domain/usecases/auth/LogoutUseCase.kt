package com.oracle.visualize.domain.usecases.auth

import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for logging out the current user.
 *
 * @property repository The repository used for authentication operations.
 */
@Singleton
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.logout()
}
