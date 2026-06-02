package com.oracle.visualize.domain.usecases.visualization

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case that updates both the users and teams a visualization is shared with.
 *
 * Validates inputs and delegates to [com.oracle.visualize.domain.repositories.VisualizationRepository.updateSharedUsers].
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
     * @return [Result.success] on success, [Result.failure] with an [com.oracle.visualize.domain.exceptions.AppError] on failure.
     */
    suspend operator fun invoke(
        visualizationId: String,
        userIds: List<String>,
        teamIds: List<String> = emptyList()
    ): AppResult<Unit> {
        if (visualizationId.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("Visualization ID cannot be empty"))

        return visualizationRepository.updateSharedUsers(visualizationId, userIds, teamIds)
    }
}
