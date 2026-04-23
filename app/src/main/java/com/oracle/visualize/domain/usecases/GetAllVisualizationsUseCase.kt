package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class GetAllVisualizationsUseCase @Inject constructor(private val repository: VisualizationRepository) {
    suspend operator fun invoke(): List<Visualization> {
        return repository.getAllVisualizations()
    }
}