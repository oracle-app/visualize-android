package com.oracle.visualize.data.datasources

import com.google.firebase.Timestamp
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
    private val db: FirebaseFirestore
) {
    private val visualizationsRef = db.collection("visualizations")
    private val teamsRef = db.collection("teams")

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
                .get().await()

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

    /**
     * Fetches visualizations shared with any team that the user is a member of.
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects shared with the user's teams.
     * @throws AppError.ParsingError If any document cannot be parsed.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getSharedVisualizationsByTeamsIntegratedByUser(userID: String): List<VisualizationDTO> {
        return try {
            val teams = teamsRef.whereArrayContains("membersIDs", userID).get().await()
            val teamIDs = teams.documents.map { it.id }

            if (teamIDs.isEmpty()) return emptyList()

            val sharedWithTeams = visualizationsRef
                .whereArrayContainsAny("sharedWithTeams", teamIDs)
                .get()
                .await()

            if (sharedWithTeams.isEmpty) return emptyList()

            sharedWithTeams.documents.map { doc ->
                doc.toObject(VisualizationDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch team visualizations: ${ex.message}")
        }
    }

    /**
     * Aggregates all visualizations relevant to a user (personal, shared with user, shared with teams).
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getAllVisualizationsByUserID(userID: String): List<VisualizationDTO> {
        return try {
            val finalArray = mutableListOf<VisualizationDTO>()
            val personal = getPersonalVisualizations(userID)
            val sharedWithUsers = getVisualizationsSharedWithUser(userID)
            val sharedWithTeams = getSharedVisualizationsByTeamsIntegratedByUser(userID)

            finalArray.addAll(personal)
            finalArray.addAll(sharedWithUsers)
            finalArray.addAll(sharedWithTeams)
            finalArray
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to aggregate visualizations: ${ex.message}")
        }
    }

    /**
     * Fetches all users that a specific visualization is shared with.
     *
     * @param visualizationID The unique ID of the visualization.
     * @return A list of [UserDTO] objects representing the users.
     * @throws AppError.NotFound If the visualization ID does not exist.
     * @throws AppError.ParsingError If documentation mapping fails.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getAllUsersVisualizationIsSharedWith(visualizationID: String): List<UserDTO> {
        return try {
            val snapshot = db.collection("visualizations")
                .document(visualizationID)
                .get()
                .await()

            if (!snapshot.exists()) {
                throw AppError.NotFound("This visualization ID does not exist.")
            }

            val visualizationDTO = snapshot.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Visualization could not be mapped.")

            val sharedUserIDs = visualizationDTO.sharedWithUsers.filter { it.isNotBlank() }

            coroutineScope {
                sharedUserIDs.map { userId ->
                    async {
                        val userSnapshot = db.collection("users")
                            .document(userId)
                            .get()
                            .await()

                        if (userSnapshot.exists()) {
                            userSnapshot.toObject(UserDTO::class.java)
                                ?: throw AppError.ParsingError("Failed to map UserDTO: $userId")
                        } else {
                            null
                        }
                    }
                }
                    .awaitAll()
                    .filterNotNull()
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch shared users: ${ex.message}")
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
}