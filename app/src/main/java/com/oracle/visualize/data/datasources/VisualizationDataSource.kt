package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source for visualization-related operations using Firestore.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class VisualizationDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val teamsDatasource: TeamDatasource
) {
    private val visualizationsRef = db.collection("visualizations")

    /**
     * Creates a new visualization in the database.
     *
     * @param visualization The [Visualization] domain model to be saved.
     * @throws AppError.ValidationError If required fields are empty.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun createVisualization(visualization: Visualization) {
        try {
            if (visualization.authorID.isNotEmpty() && visualization.title.isNotEmpty() &&
                visualization.configJSON.isNotEmpty()) {

                val formattedVisualization = hashMapOf(
                    "authorID" to visualization.authorID,
                    "title" to visualization.title,
                    "configJSON" to visualization.configJSON,
                    "sharedWithUsers" to visualization.sharedWithUsers,
                    "sharedWithTeams" to visualization.sharedWithTeams,
                    "createdAt" to visualization.createdAt,
                )
                visualizationsRef.add(formattedVisualization).await()
            } else {
                throw AppError.ValidationError("AuthorID, title, and configJSON cannot be empty")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to create visualization: ${ex.message}")
        }
    }

    /**
     * Fetches all visualizations from the database.
     *
     * @return A list of [VisualizationDTO] objects.
     * @throws AppError.ParsingError If any document cannot be parsed.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getAllVisualizations(): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef.get().await()
            if (visualizations.isEmpty) return emptyList()

            visualizations.documents.map { doc ->
                doc.toObject(VisualizationDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch all visualizations: ${ex.message}")
        }
    }

    /**
     * Fetches visualizations shared directly with a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects.
     * @throws AppError.ParsingError If any document cannot be parsed.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getVisualizationsSharedWithUser(userID: String): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef
                .whereArrayContains("sharedWithUsers", userID)
                .get()
                .await()

            if (visualizations.isEmpty) return emptyList()

            visualizations.documents.map { doc ->
                doc.toObject(VisualizationDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch shared visualizations: ${ex.message}")
        }
    }

    /**
     * Fetches visualizations created by a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects authored by the user.
     * @throws AppError.ParsingError If any document cannot be parsed.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getPersonalVisualizations(userID: String): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef
                .whereEqualTo("authorID", userID)
                .get().await()

            if (visualizations.isEmpty) return emptyList()

            visualizations.documents.map { doc ->
                doc.toObject(VisualizationDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch personal visualizations: ${ex.message}")
        }
    }

    private suspend fun getVisualizationsSharedWithTeamsUserIsIn(userID: String): List<VisualizationDTO> {
        val userTeams = teamsDatasource.getTeamsUserIsIn(userID)
        val teamIDs = userTeams.mapNotNull { it.id }

        if (teamIDs.isEmpty()) return emptyList()

        val snapshot = db.collection("visualizations")
            .whereArrayContainsAny("sharedWithTeams", teamIDs)
            .get()
            .await()
        return snapshot.toObjects(VisualizationDTO::class.java)
    }

    suspend fun getAllSharedVisualizations(userID: String): List<VisualizationDTO> {
        val sharedWithUser = getVisualizationsSharedWithUser(userID)
        val sharedWithTeams = getVisualizationsSharedWithTeamsUserIsIn(userID)

        val allShared = sharedWithUser + sharedWithTeams
        return allShared.distinctBy { it.id }
    }
}