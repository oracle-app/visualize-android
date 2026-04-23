package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import kotlin.String

class CreateVisualizationUseCase @Inject constructor(
    private val repository: VisualizationRepository
) {
    suspend operator fun invoke(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ){
        repository.createVisualization(
            authorID,
            title,
            configJSON,
            sharedWithUsers,
            sharedWithTeams
        )
    }
}