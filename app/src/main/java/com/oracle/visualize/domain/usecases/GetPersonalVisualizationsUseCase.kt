package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository

class GetPersonalVisualizationsUseCase(private val repository: VisualizationRepository) {
    suspend operator fun invoke(userID: String): List<Visualization> {
        val visualizations = repository.getPersonalVisualizations(userID)
        return visualizations
    }
}