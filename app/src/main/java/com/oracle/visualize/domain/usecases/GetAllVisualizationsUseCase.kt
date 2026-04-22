package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository

class GetAllVisualizationsUseCase(private val repository: VisualizationRepository) {
    suspend operator fun invoke() {
        repository.getAllVisualizations()
    }
}