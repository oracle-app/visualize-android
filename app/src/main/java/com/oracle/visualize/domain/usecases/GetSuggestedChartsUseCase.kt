package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to retrieve suggested charts based on a uploaded dataset.
 */
@Singleton
class GetSuggestedChartsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
) {
    /**
     * Executes the use case.
     * Currently returns a list of suggested visualizations.
     */
    suspend operator fun invoke(): List<Visualization> {
        return visualizationRepository.getAllVisualizations()
    }
}
