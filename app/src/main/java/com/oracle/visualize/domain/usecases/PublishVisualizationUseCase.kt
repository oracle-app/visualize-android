package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class PublishVisualizationUseCase @Inject constructor(private val repository: VisualizationRepository) {
    suspend operator fun invoke(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>?,
        sharedWithTeams: List<String>?,
        isPersonal: Boolean
    ) {
        return repository.publishVisualization(
            authorID,
            title,
            configJSON,
            sharedWithUsers,
            sharedWithTeams,
            isPersonal
        )
    }
}