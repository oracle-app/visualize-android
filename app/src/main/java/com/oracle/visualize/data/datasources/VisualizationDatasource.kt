package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

/**
 * Data source for visualization-related operations using Firestore.
 * This class does not perform error handling — all exceptions propagate
 * to [VisualizationRepositoryImpl] where they are caught and mapped to [AppError].
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class VisualizationDatasource @Inject constructor(
    private val db: FirebaseFirestore,
) {
    private val visualizationsRef = db.collection("visualizations")

    private fun formatVisualization(v: VisualizationDTO): HashMap<String, Any> {
        return hashMapOf(
            "authorID"        to v.authorID,
            "title"           to v.title,
            "configJSON"      to v.configJSON,
            "sharedWithUsers" to v.sharedWithUsers,
            "sharedWithTeams" to v.sharedWithTeams,
            "createdAt"       to v.createdAt
        )
    }

    suspend fun createVisualization(vDTO: VisualizationDTO) {
        val formattedVisualization = formatVisualization(vDTO)
        visualizationsRef.add(formattedVisualization).await()
    }

    suspend fun getAllVisualizations(): List<VisualizationDTO> {
        val visualizations = visualizationsRef.get().await()
        if (visualizations.isEmpty) return emptyList()
        return visualizations.documents.map { doc ->
            doc.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
        }
    }

    suspend fun getVisualizationsSharedWithUser(userID: String): List<VisualizationDTO> {
        val visualizations = visualizationsRef
            .whereArrayContains("sharedWithUsers", userID)
            .get().await()
        if (visualizations.isEmpty) return emptyList()
        return visualizations.documents.map { doc ->
            doc.toObject(VisualizationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse VisualizationDTO: ${doc.id}")
        }
    }

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

    suspend fun publishVisualizationsInBulk(visualizations: List<VisualizationDTO>) {
        visualizations.chunked(500).forEach { chunk ->
            val batch = db.batch()
            for (v in chunk) {
                val doc = visualizationsRef.document()
                batch.set(doc, formatVisualization(v))
            }
            batch.commit().await()
        }
    }

    suspend fun getVisualizationsSharedWithTeams(teamIDs: List<String>): List<VisualizationDTO> {
        if (teamIDs.isEmpty()) return emptyList()
        val results = mutableListOf<VisualizationDTO>()
        for (chunk in teamIDs.chunked(10)) {
            val snapshot = visualizationsRef
                .whereArrayContainsAny("sharedWithTeams", chunk)
                .get().await()
            results.addAll(snapshot.toObjects(VisualizationDTO::class.java))
        }
        return results
    }

    /**
     * Permanently deletes a visualization document from the database.
     * Uses [withTimeout] to prevent indefinite hanging on slow or offline connections.
     * [kotlinx.coroutines.TimeoutCancellationException] propagates to the repository.
     */
    suspend fun deleteVisualization(visualizationId: String) {
        withTimeout(10_000) {
            suspendCancellableCoroutine { cont ->
                val task = visualizationsRef.document(visualizationId).delete()
                task.addOnSuccessListener {
                    if (cont.isActive) cont.resume(Unit) {}
                }
                task.addOnFailureListener { e ->
                    if (cont.isActive) cont.resumeWithException(e)
                }
                cont.invokeOnCancellation { }
            }
        }
    }


    /**
     * Overwrites [sharedWithUsers] and [sharedWithTeams] on a visualization using
     * [SetOptions.merge] so other fields are not affected.
     * Uses [withTimeout] to prevent indefinite hanging.
     * [kotlinx.coroutines.TimeoutCancellationException] propagates to the repository.
     */
    suspend fun updateSharedUsers(
        visualizationId: String,
        userIds: List<String>,
        teamIds: List<String>
    ) {
        withTimeout(10_000) {
            suspendCancellableCoroutine { cont ->
                val task = visualizationsRef.document(visualizationId)
                    .set(
                        mapOf(
                            "sharedWithUsers" to userIds,
                            "sharedWithTeams" to teamIds
                        ),
                        SetOptions.merge()
                    )
                task.addOnSuccessListener {
                    if (cont.isActive) cont.resume(Unit) {}
                }
                task.addOnFailureListener { e ->
                    if (cont.isActive) cont.resumeWithException(e)
                }
                cont.invokeOnCancellation { }
            }
        }
    }
}
