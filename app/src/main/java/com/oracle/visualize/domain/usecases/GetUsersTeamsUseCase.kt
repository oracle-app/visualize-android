package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUsersTeamsUseCase @Inject constructor(
    private val teamsRepository: TeamRepository
) {
}