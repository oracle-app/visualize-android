package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case to delete all users' access associated with a visualization.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
class DeleteUsersAccessToVisualizationUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(visualizationID: String, userIDs: List<String>): Result<Unit> {
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is empty"))
        if (userIDs.isEmpty()) return Result.failure(AppError.ValidationError("Users IDs list is empty"))

        val filterUserIDs = userIDs.filter { it.isNotBlank() }
        if (filterUserIDs.isEmpty()) return Result.failure(AppError.ValidationError("None of the users IDs in list is valid"))

        return try {
            Result.success(visualizationRepository.deleteUsersAccessToVisualization(visualizationID, filterUserIDs))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to delete users access to visualization"))
        }
    }
}