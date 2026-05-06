package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

<<<<<<< Updated upstream
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
=======
class LogoutUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke() = repository.logout()
}
>>>>>>> Stashed changes
