package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating an existing team's name and member list.
 *
 * @property teamRepository The repository used for team data operations.
 */
@Singleton
class UpdateTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamID: String, memberIDs: List<String>, name: String): Result<Unit> {
        if (name.isBlank()) return Result.failure(AppError.ValidationError("Team name cannot be empty"))
        if (memberIDs.isEmpty()) return Result.failure(AppError.ValidationError("Team must have at least one member"))
        return try {
            teamRepository.updateTeam(teamID, memberIDs, name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
