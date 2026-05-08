package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case to delete a visualization.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
class DeleteVisualizationUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(visualizationID: String): Result<Unit> {
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is invalid"))
        return try {
            Result.success(visualizationRepository.deleteVisualization(visualizationID))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to delete visualization"))
        }
    }
}