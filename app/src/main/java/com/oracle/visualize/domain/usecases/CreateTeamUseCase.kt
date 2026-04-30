package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend fun invoke(memberIDs: List<String>, name: String, ownerID: String) {
        require(name.isNotBlank()) { "Team name cannot be empty" }
        require(memberIDs.isNotEmpty()) { "Team must have at least one member" }
        teamRepository.createTeam(memberIDs, name, ownerID)
    }
}