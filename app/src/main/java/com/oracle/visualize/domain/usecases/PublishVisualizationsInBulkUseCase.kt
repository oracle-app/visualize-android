package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to publish many visualizations in bulk.
 *
 * @property visualizationRepository: The repository to create / publish visualization.
 */
@Singleton
class PublishVisualizationsInBulkUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(visualizations: List<Visualization>): Result<Unit> {
        if (visualizations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No visualizations to publish."))
        }

        for (visualization in visualizations) {
            if (visualization.title.isEmpty() || visualization.authorID.isEmpty() ||
                visualization.configJSON.isEmpty()) {
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