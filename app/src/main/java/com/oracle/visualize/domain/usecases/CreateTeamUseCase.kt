package com.oracle.visualize.domain.usecases

import com.oracle.visualize.data.repositories.TeamRepositoryImpl
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.repositories.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
){
    suspend operator fun invoke(
        memberIDs: List<String>,
        name: String,
        ownerID: String
    ) {
        teamRepository.createTeam(memberIDs, name, ownerID)
    }
}