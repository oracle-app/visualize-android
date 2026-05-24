package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to fetch a visualization by its ID.
 *
 * @property visualizationRepository The repository to fetch the visualization.
 */
@Singleton
class GetIndividualVisualizationUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    // Return type Result<VisualizationCard?>
    suspend operator fun invoke(visualizationID: String): Result<VisualizationCard?> {
        if (visualizationID.isBlank()) {
            return Result.failure(AppError.GeneralValidationError("Visualization ID is empty"))
        }

        return runCatching { visualizationRepository.getIndividualVisualization(visualizationID) }
    }
}
