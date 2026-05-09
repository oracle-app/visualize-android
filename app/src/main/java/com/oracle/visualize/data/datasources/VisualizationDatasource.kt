package com.oracle.visualize.data.datasources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source for visualization-related operations using Firestore.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class VisualizationDatasource @Inject constructor(
    private val db: FirebaseFirestore,
    private val teamsDatasource: TeamDatasource
) {
    private val visualizationsRef = db.collection("visualizations")

    /**
     * Formats a visualization object.
     *
     * @param v The [Visualization] object to be formatted.
     * @return A [HashMap] representing the formatted visualization.
     */
    private fun formatVisualization(v: VisualizationDTO): HashMap<String, Any> {
        return hashMapOf(
            "authorID" to v.authorID,
            "title" to v.title,
            "configJSON" to v.configJSON,
            "sharedWithUsers" to v.sharedWithUsers,
            "sharedWithTeams" to v.sharedWithTeams,
            "createdAt" to v.createdAt
        )
    }


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
                val vDTO = VisualizationDTO(
                    id = visualization.id,
                    authorID = visualization.authorID,
                    title = visualization.title,
                    configJSON = visualization.configJSON,
                    sharedWithUsers = visualization.sharedWithUsers,
                    sharedWithTeams = visualization.sharedWithTeams,
                    createdAt = Timestamp(visualization.createdAt)
                )
                val formattedVisualization = formatVisualization(vDTO)
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
        return try {
            val userTeams = teamsDatasource.getTeamsUserIsIn(userID)
            val teamIDs = userTeams.mapNotNull { it.id }

            if (teamIDs.isEmpty()) return emptyList()

            val snapshot = db.collection("visualizations")
                .whereArrayContainsAny("sharedWithTeams", teamIDs)
                .get()
                .await()

            if (snapshot.isEmpty) return emptyList()

            snapshot.documents.map { doc ->
                doc.toObject(VisualizationDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }

    suspend fun getAllSharedVisualizations(userID: String): List<VisualizationDTO> {
        return try {
            val sharedWithUser = getVisualizationsSharedWithUser(userID)
            val sharedWithTeams = getVisualizationsSharedWithTeamsUserIsIn(userID)

            val allShared = sharedWithUser + sharedWithTeams
            allShared.distinctBy { it.id }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching all shared visualizations: ${ex.message}")
        }
    }

    /**
     * Publishes all user's visualizations to the database in bulk.
     *
     * @param visualizations The list of visualizations [List<VisualizationDTO>].
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun publishVisualizationsInBulk(visualizations: List<VisualizationDTO>) {
        try {
            visualizations.chunked(500).forEach { chunk ->
                val batch = db.batch()
                for (v in chunk) {
                    val doc = visualizationsRef.document()
                    val formattedVisualization = formatVisualization(v)
                    batch.set(doc, formattedVisualization)
                }
                batch.commit().await()
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to publish visualizations: ${ex.message}")
        }
    }

    /**
     * Permanently deletes a visualization document from Firestore.
     *
     * @param visualizationId The unique ID of the visualization to delete.
     * @throws AppError.NetworkError If the operation fails.
     */
    suspend fun deleteVisualization(visualizationId: String) {
        try {
            visualizationsRef.document(visualizationId).delete().await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to delete visualization: ${ex.message}")
        }
    }
}


