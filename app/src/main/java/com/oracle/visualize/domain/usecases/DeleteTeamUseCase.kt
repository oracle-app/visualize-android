package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for deleting a team by its ID.
 *
 * @property teamRepository The repository used for team data operations.
 */
@Singleton
class DeleteTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamID: String): Result<Unit> {
        if (teamID.isBlank()) return Result.failure(AppError.ValidationError("Team ID cannot be empty"))
        return try {
            teamRepository.deleteTeam(teamID)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
