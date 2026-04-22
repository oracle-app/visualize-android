package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository

class GetAllVisualizationsUseCase(private val repository: VisualizationRepository) {
    suspend operator fun invoke(): List<Visualization> {
        val visualizations = repository.getAllVisualizations()
        return visualizations
    }
}