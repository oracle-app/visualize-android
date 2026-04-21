package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUsersTeamsUseCase @Inject constructor(
    private val teamsRepository: TeamRepository
) {
    suspend fun getTeamsUserIsIn(userID: String): List<ShareTeam> {
        return teamsRepository.getTeamsUserIsIn(userID)
    }

    suspend fun getTeamsUserOwns(userID: String): List<ShareTeam> {
        return teamsRepository.getTeamsOwnedByUser(userID)
    }
}