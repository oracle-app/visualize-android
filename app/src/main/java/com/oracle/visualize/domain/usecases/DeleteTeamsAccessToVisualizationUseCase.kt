package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case to delete all teams' access associated with a visualization.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
class DeleteTeamsAccessToVisualizationUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    // Return type Result<Unit>
    suspend operator fun invoke(visualizationID: String, teamIDs: List<String>): Result<Unit> {
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is empty"))
        if (teamIDs.isEmpty()) return Result.failure(AppError.ValidationError("Users IDs list is empty"))

        val filterUserIDs = teamIDs.filter { it.isNotBlank() }
        if (filterUserIDs.isEmpty()) return Result.failure(AppError.ValidationError("None of the users IDs in list is valid"))

        return try {
            Result.success(visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, teamIDs))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to delete users access to visualization"))
        }
    }
}