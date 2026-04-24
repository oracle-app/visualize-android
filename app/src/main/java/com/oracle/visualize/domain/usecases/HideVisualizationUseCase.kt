package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class HideVisualizationUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, visualizationId: String) {
        repository.hideVisualization(userId, visualizationId)
    }
}