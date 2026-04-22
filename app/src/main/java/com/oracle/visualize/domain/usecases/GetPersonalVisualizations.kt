package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository

class GetPersonalVisualizations(private val repository: VisualizationRepository) {
    suspend operator fun invoke(userID: String) {
        repository.getPersonalVisualizations(userID)
    }
}