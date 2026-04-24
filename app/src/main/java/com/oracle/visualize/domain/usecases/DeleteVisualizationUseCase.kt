package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class DeleteVisualizationUseCase @Inject constructor(
    private val repository: VisualizationRepository
){
    suspend operator fun invoke(visualizationID: String) {
        repository.deleteVisualization(visualizationID)
    }
}