package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.async
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
    // Return type Result<List<VisualizationCard>>
    suspend operator fun invoke(userID: String, filter: VisualizationFilter): Result<List<VisualizationCard>> {
        if (userID.isBlank()) return Result.failure(AppError.ValidationError("User ID empty"))

        return try {
            coroutineScope {
                val cards = when (filter) {
                    VisualizationFilter.ALL -> {
                        val shared = async { visualizationRepository.getSharedVisualizations(userID) }
                        val personal = async { visualizationRepository.getPersonalVisualizations(userID) }
                        shared.await() + personal.await()
                    }
                    VisualizationFilter.SHARED -> visualizationRepository.getSharedVisualizations(userID)
                    VisualizationFilter.PERSONAL -> visualizationRepository.getPersonalVisualizations(userID)
                }
                Result.success(cards)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}