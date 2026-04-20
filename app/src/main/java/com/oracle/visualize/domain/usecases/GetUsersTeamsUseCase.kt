package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.TeamsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUsersTeamsUseCase @Inject constructor(
    teamsRepository: TeamsRepository
) {
}