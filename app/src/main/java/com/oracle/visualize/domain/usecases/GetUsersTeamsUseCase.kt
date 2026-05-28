package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for retrieving teams associated with a user.
 *
 * @property teamRepository The repository used for team data operations.
 */
@Singleton
class GetUsersTeamsUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend fun getTeamsUserOwns(userID: String): Result<List<ShareTeam>> {
        if (userID.isBlank()) return Result.failure(AppError.GeneralValidationError("User ID cannot be empty"))
        return try {
            Result.success(teamRepository.getTeamsOwnedByUser(userID))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeamsUserIsIn(userID: String): Result<List<ShareTeam>> {
        if (userID.isBlank()) return Result.failure(AppError.GeneralValidationError("User ID cannot be empty"))
        return try {
            Result.success(teamRepository.getTeamsUserIsIn(userID))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
