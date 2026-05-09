package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for hiding a visualization from the current user's feed without deleting it.
 * The visualization remains visible to other users who have access to it.
 *
 * @property visualizationRepository Repository for visualization operations.
 */
@Singleton
class HideVisualizationForMeUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    suspend operator fun invoke(userID: String, visualizationId: String): Result<Unit> {
        if (userID.isBlank())
            return Result.failure(AppError.ValidationError("User ID cannot be empty"))
        if (visualizationId.isBlank())
            return Result.failure(AppError.ValidationError("Visualization ID cannot be empty"))
        return try {
            visualizationRepository.hideVisualizationForMe(userID, visualizationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
