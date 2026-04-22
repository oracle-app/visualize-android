package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.serialization.json.JsonObject
import kotlin.String

class CreateVisualizationUseCase(private val repository: VisualizationRepository) {
    suspend operator fun invoke(
        authorID: String,
        title: String,
        configJSON: JsonObject,
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