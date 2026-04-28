package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.logout()
}