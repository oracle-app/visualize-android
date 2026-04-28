package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllUserVisualizationsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    // Cambiamos el tipo de retorno a Result<List<VisualizationCard>>
    suspend operator fun invoke(userID: String, filter: VisualizationFilter): Result<List<VisualizationCard>> {

        if (userID.isBlank()) {
            return Result.failure(AppError.ValidationError("User ID does not exist."))
        }

        return try {
            val visualizations = visualizationRepository.getAllVisualizationsByUserID(userID, filter)
            Result.success(visualizations)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}