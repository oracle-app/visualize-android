package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class GetAllUserVisualizationsUseCase @Inject constructor(
    private val repository: VisualizationRepository
){
    suspend operator fun invoke(userID: String): List<Visualization> {
        return repository.getAllVisualizationsByUserID(userID)
    }
}