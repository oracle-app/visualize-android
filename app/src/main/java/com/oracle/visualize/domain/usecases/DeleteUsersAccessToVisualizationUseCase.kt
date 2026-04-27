package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class DeleteUsersAccessToVisualizationUseCase @Inject constructor(
    private val repository: VisualizationRepository
){
    suspend operator fun invoke(visualizationID: String, userIDs: List<String>) {
        repository.deleteUsersAccessToVisualization(visualizationID, userIDs)
    }
}