package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllUserVisualizationsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    suspend operator fun invoke(userID: String, filter: VisualizationFilter): List<VisualizationCard> {
        return visualizationRepository.getAllVisualizationsByUserID(userID, filter)
    }
}