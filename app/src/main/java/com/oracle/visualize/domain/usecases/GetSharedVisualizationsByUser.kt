package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository

class GetSharedVisualizationsByUser(private val repository: VisualizationRepository) {
    suspend operator fun invoke(userID: String) {
        repository.getSharedVisualizationsByUser(userID)
    }
}