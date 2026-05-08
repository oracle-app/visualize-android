package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

/**
 * Use case to share a visualization to several teams.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
class ShareVisualizationWithTeamsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    suspend operator fun invoke(visualizationID: String, teamIDs: List<String>): Result<Unit> {
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is invalid"))
        if (visualizationID.isBlank()) return Result.failure(AppError.ValidationError("Visualization ID is invalid"))
        if (teamIDs.isEmpty()) return Result.failure(AppError.ValidationError("Users IDs list is empty"))

        val filterTeamIDs = teamIDs.filter { it.isNotBlank() }
        if (filterTeamIDs.isEmpty()) return Result.failure(AppError.ValidationError("None of the users IDs in list is valid"))

        return try {
            Result.success(visualizationRepository.shareVisualizationWithTeams(visualizationID, filterTeamIDs))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to share visualization with teams."))
        }
    }
}