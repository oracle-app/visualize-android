package com.oracle.visualize.domain.usecases.team

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
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
    suspend operator fun invoke(memberIDs: List<String>, name: String, ownerID: String): AppResult<Unit> {
        if (name.isBlank()) return AppResult.Error(AppError.GeneralValidationError("Team name cannot be empty"))
        if (memberIDs.isEmpty())
            return AppResult.Error(AppError.GeneralValidationError("Team must have at least one member"))

        return teamRepository.createTeam(memberIDs, name, ownerID)
    }
}
