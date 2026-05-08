package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case to share a visualization to several users.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
class ShareVisualizationWithUsersUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(visualizationID: String, userIDs: List<String>): Result<Unit> {
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is invalid"))
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is invalid"))
        if (userIDs.isEmpty()) return Result.failure(AppError.ValidationError("Users IDs list is empty"))

        val filterUserIDs = userIDs.filter { it.isNotBlank() }
        if (filterUserIDs.isEmpty()) return Result.failure(AppError.ValidationError("None of the users IDs in list is valid"))

        return try {
            Result.success(visualizationRepository.shareVisualizationWithUsers(visualizationID, filterUserIDs))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to share visualizations with users."))
        }
    }
}