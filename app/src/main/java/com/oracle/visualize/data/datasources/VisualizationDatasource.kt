package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FieldValue
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
     * Deletes a specific visualization.
     *
     * @param visualizationID The unique ID of the visualization.
     * @throws AppError.NotFound If the visualization ID does not exist.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun deleteVisualization(visualizationID: String) {
        try {
            val visExists = db.collection("users")
                .document(visualizationID).get().await().exists()

            if (!visExists) {
                throw AppError.NotFound("Visualization not found")
            }

            val batch = db.batch()
            val usersWithHiddenVisualizations = db.collection("users")
                .whereArrayContains("hiddenVisualizations", visualizationID)
                .get().await()

            usersWithHiddenVisualizations.documents.forEach { user ->
                batch.update(
                    user.reference,
                    "hiddenVisualizations",
                    FieldValue.arrayRemove(visualizationID)
                )
            }
            batch.delete(visualizationsRef.document(visualizationID))
            batch.commit().await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }

    /**
     * Shares a visualization with multiple users.
     *
     * @param visualizationID The unique ID of the visualization.
     * @param userIDs The lists of users IDs.
     * @throws AppError.NotFound If the visualization ID does not exist or if users don't exist in DB.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun shareVisualizationWithUsers(visualizationID: String, userIDs: List<String>){
        try {
            val vis = visualizationsRef.document(visualizationID)
            val visExists = vis.get().await().exists()
            val filteredUsers = mutableListOf<String>()

            if (!visExists) {
                throw AppError.NotFound("Visualization not found")
            }

            for (u in userIDs) {
                val userExists = db.collection("users").document(u).get().await().exists()
                if (userExists) { filteredUsers.add(u) }
            }

            if (filteredUsers.isEmpty()) {
                throw AppError.NotFound("Users not found in db")
            }

            vis.update("sharedWithUsers", FieldValue.arrayUnion(*filteredUsers.toTypedArray())).await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }

    /**
     * Shares a visualization with multiple teams.
     *
     * @param visualizationID The unique ID of the visualization.
     * @param teamIDs The lists of teams IDs.
     * @throws AppError.NotFound If the visualization ID does not exist or if teams don't exist in DB.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun shareVisualizationWithTeams(visualizationID: String, teamIDs: List<String>){
        try {
            val vis = visualizationsRef.document(visualizationID)
            val visExists = vis.get().await().exists()
            val filteredTeams = mutableListOf<String>()

            if (!visExists) {
                throw AppError.NotFound("Visualization not found")
            }

            for (t in teamIDs) {
                val teamExists = db.collection("teams").document(t).get().await().exists()
                if (teamExists) { filteredTeams.add(t) }
            }

            if (filteredTeams.isEmpty()) {
                throw AppError.NotFound("Teams not found in db")
            }

            vis.update("sharedWithTeams", FieldValue.arrayUnion(*filteredTeams.toTypedArray())).await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }

    /**
     * Deletes the access of several users to a visualization.
     *
     * @param visualizationID The unique ID of the visualization.
     * @param userIDs The lists of users IDs.
     * @throws AppError.NotFound If the visualization ID does not exist or if users don't exist in DB.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun deleteUsersAccessToVisualization(visualizationID: String, userIDs: List<String>){
        try {
            val vis = visualizationsRef.document(visualizationID)
            val visExists = vis.get().await().exists()
            val filteredUsers = mutableListOf<String>()

            if (!visExists) {
                throw AppError.NotFound("Visualization not found")
            }

            for (u in userIDs) {
                val userExists = db.collection("users").document(u).get().await().exists()
                if (userExists) { filteredUsers.add(u) }
            }

            if (filteredUsers.isEmpty()) {
                throw AppError.NotFound("Users not found in db")
            }

            vis.update("sharedWithUsers", FieldValue.arrayRemove(*filteredUsers.toTypedArray())).await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }

    /**
     * Deletes the access of several teams to a visualization.
     *
     * @param visualizationID The unique ID of the visualization.
     * @param teamIDs The lists of teams IDs.
     * @throws AppError.NotFound If the visualization ID does not exist or if teams don't exist in DB.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun deleteTeamsAccessToVisualization(visualizationID: String, teamIDs: List<String>){
        try {
            val vis = visualizationsRef.document(visualizationID)
            val visExists = vis.get().await().exists()
            val filteredTeams = mutableListOf<String>()

            if (!visExists) {
                throw AppError.NotFound("Visualization not found")
            }

            for (t in teamIDs) {
                val teamExists = db.collection("teams").document(t).get().await().exists()
                if (teamExists) { filteredTeams.add(t) }
            }

            if (filteredTeams.isEmpty()) {
                throw AppError.NotFound("Teams not found in db")
            }

            vis.update("sharedWithTeams", FieldValue.arrayRemove(*filteredTeams.toTypedArray())).await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching team visualizations: ${ex.message}")
        }
    }
}