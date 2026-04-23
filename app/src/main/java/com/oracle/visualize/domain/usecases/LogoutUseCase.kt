package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.logout()
}