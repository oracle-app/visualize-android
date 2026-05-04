package com.oracle.visualize.domain.usecases

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
            return Result.failure(IllegalArgumentException("No visualizations to publish."))
        }

        for (vis in visualizations) {
            if (vis.title.isBlank() || vis.authorID.isBlank() || vis.configJSON.isBlank()) {
                return Result.failure(IllegalArgumentException("Invalid visualization data."))
            }
        }

        return try {
            visualizationRepository.publishVisualizationsInBulk(visualizations)
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}