package com.oracle.visualize.data.datasources

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Data source for visualization-related operations using Firestore.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class VisualizationDatasource @Inject constructor(
    private val db: FirebaseFirestore,
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
     * @param vDTO The [VisualizationDTO] dtos model to be saved.
     */
    suspend fun createVisualization(vDTO: VisualizationDTO) {
        val formattedVisualization = formatVisualization(vDTO)
        visualizationsRef.add(formattedVisualization).await()
    }

    /**
     * Fetches all visualizations from the database.
     *
     * @return A list of [VisualizationDTO] objects.
     */
    suspend fun getAllVisualizations(): List<VisualizationDTO> {
        val visualizations = visualizationsRef.get().await()

        if (visualizations.isEmpty) return emptyList()

        return visualizations.documents.map { doc ->
            doc.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
        }

    }

    /**
     * Fetches visualizations shared directly with a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects.
     */
    suspend fun getVisualizationsSharedWithUser(userID: String): List<VisualizationDTO> {
        val visualizations = visualizationsRef
            .whereArrayContains("sharedWithUsers", userID)
            .get()
            .await()

        if (visualizations.isEmpty) return emptyList()

        return visualizations.documents.map { doc ->
            doc.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
        }
    }

    /**
     * Fetches visualizations created by a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [VisualizationDTO] objects authored by the user.
     */
    suspend fun getPersonalVisualizations(userID: String): List<VisualizationDTO> {
        val visualizations = visualizationsRef
            .whereEqualTo("authorID", userID)
            .get().await()

        if (visualizations.isEmpty) return emptyList()

        return visualizations.documents.map { doc ->
            doc.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
        }
    }

    /**
     * Publishes all user's visualizations to the database in bulk.
     *
     * @param visualizations The list of visualizations [List<VisualizationDTO>].ß
     */
    suspend fun publishVisualizationsInBulk(visualizations: List<VisualizationDTO>) {
        visualizations.chunked(500).forEach { chunk ->
            val batch = db.batch()
            for (v in chunk) {
                val doc = visualizationsRef.document()
                val formattedVisualization = formatVisualization(v)
                batch.set(doc, formattedVisualization)
            }
            batch.commit().await()
        }
    }


    suspend fun getVisualizationsSharedWithTeams(teamIDs: List<String>): List<VisualizationDTO> {
        if (teamIDs.isEmpty()) return emptyList()

        val chunks = teamIDs.chunked(10)
        val results = mutableListOf<VisualizationDTO>()

        for (chunk in chunks) {
            val snapshot = visualizationsRef.whereArrayContainsAny("sharedWithTeams", chunk)
                .get()
                .await()

            results.addAll(snapshot.toObjects(VisualizationDTO::class.java))
        }
        return results
    }

    /**
     * Permanently deletes a visualization document from the database.
     *
     * @param visualizationId The unique ID of the visualization to delete.
     */
    suspend fun deleteVisualization(visualizationId: String) {
        withTimeout(10_000) {
            visualizationsRef.document(visualizationId).delete().await()
        }
    }

    /**
     * Overwrites the [sharedWithUsers] field of a visualization with the given list of user IDs.
     * Uses [set] with merge to ensure the call succeeds even if the document
     * doesn't exist yet, avoiding the silent hang that [update] causes on missing documents.
     *
     * @param visualizationId The unique ID of the visualization to update.
     * @param userIds         The complete list of user IDs to share the visualization with.
     */
    suspend fun updateSharedUsers(
        visualizationId: String,
        userIds: List<String>,
        teamIds: List<String>
    ) {
        Log.d("VizDatasource", "updateSharedUsers — doc='$visualizationId' users=$userIds teams=$teamIds")
        withTimeout(10_000) {
            visualizationsRef.document(visualizationId)
                .set(
                    mapOf(
                        "sharedWithUsers" to userIds,
                        "sharedWithTeams" to teamIds
                    ),
                    SetOptions.merge()
                )
                .await()
            Log.d("VizDatasource", "updateSharedUsers — Firestore write completed")
        }
    }
}
