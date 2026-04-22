package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlin.String

class CreateVisualizationUseCase(private val repository: VisualizationRepository) {
    suspend operator fun invoke(
        authorID: String,
        title: String,
        configJSON: Map<String, Any>,
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