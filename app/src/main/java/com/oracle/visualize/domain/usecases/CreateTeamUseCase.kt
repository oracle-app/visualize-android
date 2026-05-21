package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating a new team.
 *
 * @property teamRepository The repository used for team data operations.
 */
@Singleton
class CreateTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(memberIDs: List<String>, name: String, ownerID: String): Result<Unit> {
        if (name.isBlank()) return Result.failure(AppError.GeneralValidationError("Team name cannot be empty"))
        if (memberIDs.isEmpty()) return Result.failure(AppError.GeneralValidationError("Team must have at least one member"))
        return try {
            teamRepository.createTeam(memberIDs, name, ownerID)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
