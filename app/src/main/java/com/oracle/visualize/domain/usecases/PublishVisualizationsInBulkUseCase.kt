package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to publish many visualizations in bulk.
 *
 * @returns Result<Unit> A Result that only indicates if the operation succeeded or failed.
 * @property visualizationRepository The repository to create / publish visualization.
 */
@Singleton
class PublishVisualizationsInBulkUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(visualizations: List<Visualization>): Result<Unit> {
        if (visualizations.isEmpty()) {
            return Result.failure(AppError.ValidationError("Visualizations list is empty"))
        }

        val validVisualizations = visualizations.filter {
            it.title.isNotBlank() && it.authorID.isNotBlank() && it.configJSON.isNotBlank()
        }

        if (validVisualizations.isEmpty()) {
            return Result.failure(AppError.ValidationError("No valid visualizations to publish"))
        }

        return try {
            visualizationRepository.publishVisualizationsInBulk(validVisualizations)
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to publish visualizations"))
        }
    }
}