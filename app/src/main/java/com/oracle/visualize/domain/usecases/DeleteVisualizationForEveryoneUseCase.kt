package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for permanently deleting a visualization for the author and all recipients.
 * Only the author of the visualization should be allowed to call this.
 *
 * @property visualizationRepository Repository for visualization operations.
 */
@Singleton
class DeleteVisualizationForEveryoneUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    suspend operator fun invoke(visualizationId: String): Result<Unit> {
        if (visualizationId.isBlank())
            return Result.failure(AppError.ValidationError("Visualization ID cannot be empty"))
        return try {
            visualizationRepository.deleteVisualizationForEveryone(visualizationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
