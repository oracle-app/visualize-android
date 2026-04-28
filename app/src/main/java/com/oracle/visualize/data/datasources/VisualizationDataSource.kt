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
            } else {
                throw AppError.ValidationError("AuthorID, title, and configJSON cannot be empty")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to create visualization: ${ex.message}")
        }
    }

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
}