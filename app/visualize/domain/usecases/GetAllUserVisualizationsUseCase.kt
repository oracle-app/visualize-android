package com.oracle.visualize.domain.usecases

<<<<<<< Updated upstream
import com.oracle.visualize.domain.exceptions.AppError
=======
>>>>>>> Stashed changes
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject
import javax.inject.Singleton

<<<<<<< Updated upstream
/**
 * Use case to fetch all visualizations associated with a user, filtered by a specific criteria.
 *
 * @property visualizationRepository The repository to fetch visualizations from.
 */
@Singleton
class GetAllUserVisualizationsUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository
){
    // Return type Result<List<VisualizationCard>>
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
=======
class GetAllUserVisualizationsUseCase
    @Inject
    constructor(
        private val repository: VisualizationRepository,
    ) {
        suspend operator fun invoke(
            userID: String,
            filter: VisualizationFilter,
        ): List<VisualizationCard> = repository.getAllVisualizationsByUserID(userID, filter)
>>>>>>> Stashed changes
    }
