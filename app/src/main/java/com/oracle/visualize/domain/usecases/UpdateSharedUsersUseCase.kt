package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case that updates both the users and teams a visualization is shared with.
 *
 * Validates inputs and delegates to [VisualizationRepository.updateSharedUsers].
 * The caller must provide the **complete** desired lists; entries not included
 * will lose access.
 *
 * @property visualizationRepository Repository for visualization operations.
 */
class UpdateSharedUsersUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    /**
     * @param visualizationId The ID of the visualization to update.
     * @param userIds         Complete list of user IDs to share with.
     * @param teamIds         Complete list of team IDs to share with.
     * @return [Result.success] on success, [Result.failure] with an [AppError] on failure.
     */
    suspend operator fun invoke(
        visualizationId: String,
        userIds: List<String>,
        teamIds: List<String> = emptyList()
    ): Result<Unit> {
        if (visualizationId.isBlank())
            return Result.failure(AppError.GeneralValidationError("Visualization ID cannot be empty"))
        return runCatching {
            visualizationRepository.updateSharedUsers(visualizationId, userIds, teamIds)
        }
    }
}
