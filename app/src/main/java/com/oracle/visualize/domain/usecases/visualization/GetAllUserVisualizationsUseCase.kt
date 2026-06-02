package com.oracle.visualize.domain.usecases.visualization

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to fetch all visualizations associated with a user, filtered by a specific criteria.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
@Singleton
class GetAllUserVisualizationsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(
        userID: String,
        forceRefresh: Boolean = false
    ): AppResult<List<VisualizationCard>> {
        if (userID.isBlank()) return AppResult.Error(AppError.GeneralValidationError("User ID empty"))

        return visualizationRepository.getUserFeedVisualizations(userID, forceRefresh)
    }
}
