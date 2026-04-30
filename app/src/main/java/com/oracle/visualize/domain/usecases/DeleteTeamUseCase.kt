package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend fun invoke(teamID: String) {
        teamRepository.deleteTeam(teamID)
    }
}