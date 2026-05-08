package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

/**
 * Use case to hide a visualization associated with a user.
 *
 * @property userRepository The repository to fetch visualizations from.
 */
class HideVisualizationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, visualizationId: String): Result<Unit> {
        if (userId.isBlank()) return Result.failure(AppError.ValidationError("Invalid user ID"))
        if (visualizationId.isBlank()) return Result.failure(AppError.ValidationError("Invalid visualization ID"))

        return try {
            Result.success(userRepository.hideVisualization(userId, visualizationId))
        } catch (ex: Exception) {
            Result.failure(AppError.NetworkError("Failed to hide visualization"))
        }
    }
}