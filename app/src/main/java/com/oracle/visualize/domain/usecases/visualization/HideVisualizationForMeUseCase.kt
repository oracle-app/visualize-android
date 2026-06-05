package com.oracle.visualize.domain.usecases.visualization

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
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
    suspend operator fun invoke(userID: String, visualizationId: String): AppResult<Unit> {
        if (userID.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("User ID cannot be empty"))
        if (visualizationId.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("Visualization ID cannot be empty"))

        return visualizationRepository.hideVisualizationForMe(userID, visualizationId)
    }
}
