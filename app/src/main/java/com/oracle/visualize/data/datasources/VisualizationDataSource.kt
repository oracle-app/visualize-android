package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.models.Visualization
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlin.collections.emptyList


class VisualizationDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val visualizationsRef = db.collection("visualizations")
    private val teamsRef = db.collection("teams")

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
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getAllVisualizations(): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef.get().await()

            if (visualizations.isEmpty) emptyList<VisualizationDTO>()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDTO::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getVisualizationsSharedWithUser(userID: String): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef
                .whereArrayContains("sharedWithUsers", userID)
                .get().await()

            if (visualizations.isEmpty) return emptyList()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDTO::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getPersonalVisualizations(userID: String): List<VisualizationDTO> {
        return try {
            val visualizations = visualizationsRef
                .whereEqualTo("authorID", userID)
                .get().await()

            if (visualizations.isEmpty) return emptyList()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDTO::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

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

            sharedWithTeams.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDTO::class.java)
                } catch (ex: Exception) {
                    throw ex
                }
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

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
            emptyList()
        }
    }

    suspend fun getAllUsersVisualizationIsSharedWith(visualizationID: String): List<UserDTO> {
        val snapshot = db.collection("visualizations")
            .document(visualizationID)
            .get()
            .await()

        if (!snapshot.exists()) {
            throw Exception("This visualization ID does not exist.")
        }
        val visualizationDTO = snapshot.toObject(VisualizationDTO::class.java)
            ?: throw Exception("Visualization could not be mapped.")
        val sharedUserIDs = visualizationDTO.sharedWithUsers.filter { it.isNotBlank() }

        return coroutineScope {
            sharedUserIDs.map { userId ->
                async {
                    try {
                        val userSnapshot = db.collection("users")
                            .document(userId)
                            .get()
                            .await()

                        userSnapshot.toObject(UserDTO::class.java)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
                .awaitAll()
                .filterNotNull()
        }
    }

    suspend fun deleteVisualization(visualizationID: String) {
        try {
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
            throw ex
        }
    }

    suspend fun shareVisualizationWithUsers(visualizationID: String, userIDs: List<String>){
        try {
            visualizationsRef.document(visualizationID)
                .update("sharedWithUsers", FieldValue.arrayUnion(*userIDs.toTypedArray()))
                .await()
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun shareVisualizationWithTeams(visualizationID: String, teamIDs: List<String>){
        try {
            visualizationsRef.document(visualizationID)
                .update("sharedWithTeams", FieldValue.arrayUnion(*teamIDs.toTypedArray()))
                .await()
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun deleteUsersAccessToVisualization(visualizationID: String, userIDs: List<String>){
        try {
            visualizationsRef.document(visualizationID)
                .update("sharedWithUsers", FieldValue.arrayRemove(*userIDs.toTypedArray()))
                .await()
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun deleteTeamsAccessToVisualization(visualizationID: String, teamIDs: List<String>){
        try {
            visualizationsRef.document(visualizationID)
                .update("sharedWithTeams", FieldValue.arrayRemove(*teamIDs.toTypedArray()))
                .await()
        } catch (ex: Exception) {
            throw ex
        }
    }
}