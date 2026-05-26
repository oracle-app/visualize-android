package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.coroutineScope
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
    ): Result<List<VisualizationCard>> {
        if (userID.isBlank()) return Result.failure(AppError.GeneralValidationError("User ID empty"))
        return runCatching {
            coroutineScope {
                visualizationRepository.getUserFeedVisualizations(userID, forceRefresh)
            }
        }
    }
}
