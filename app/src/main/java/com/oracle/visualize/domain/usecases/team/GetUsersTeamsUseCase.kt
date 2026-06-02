package com.oracle.visualize.domain.usecases.team

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
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
    suspend fun getTeamsUserOwns(userID: String): AppResult<List<ShareTeam>> {
        if (userID.isBlank()) return AppResult.Error(AppError.GeneralValidationError("User ID cannot be empty"))
        return teamRepository.getTeamsOwnedByUser(userID)
    }

    suspend fun getTeamsUserIsIn(userID: String): AppResult<List<ShareTeam>> {
        if (userID.isBlank()) return AppResult.Error(AppError.GeneralValidationError("User ID cannot be empty"))
        return teamRepository.getTeamsUserIsIn(userID)
    }
}
