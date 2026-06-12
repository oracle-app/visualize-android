package com.oracle.visualize.domain.usecases.team

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
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
    suspend operator fun invoke(teamID: String, memberIDs: List<String>, name: String): AppResult<Unit> {
        if (name.isBlank()) return AppResult.Error(AppError.GeneralValidationError("Team name cannot be empty"))
        if (memberIDs.isEmpty())
            return AppResult.Error(AppError.GeneralValidationError("Team must have at least one member"))

        return teamRepository.updateTeam(teamID, memberIDs, name)
    }
}
