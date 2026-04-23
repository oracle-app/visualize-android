package com.oracle.visualize.data.datasources

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.VisualizationDto
import com.oracle.visualize.domain.models.Visualization
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
                    "comments" to visualization.comments
                )

                visualizationsRef.add(formattedVisualization).await()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    suspend fun getAllVisualizations(): List<VisualizationDto> {
        return try {
            val visualizations = visualizationsRef.get().await()

            if (visualizations.isEmpty) emptyList<VisualizationDto>()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDto::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getVisualizationsSharedWithUser(userID: String): List<VisualizationDto> {
        return try {
            val visualizations = visualizationsRef
                .whereArrayContains("sharedWithUsers", userID)
                .get().await()

            if (visualizations.isEmpty) emptyList<VisualizationDto>()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDto::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPersonalVisualizations(userID: String): List<VisualizationDto> {
        return try {
            val visualizations = visualizationsRef
                .whereEqualTo("authorID", userID)
                .get().await()

            if (visualizations.isEmpty) emptyList<VisualizationDto>()

            visualizations.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDto::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getSharedVisualizationsByTeamsIntegratedByUser(userID: String): List<VisualizationDto> {
        return try {
            val teams = teamsRef.whereArrayContains("memberIDs", userID).get().await()
            val teamIDs = teams.documents.map { it.id }

            if (teamIDs.isEmpty()) emptyList<VisualizationDto>()

            val sharedWithTeams = visualizationsRef
                .whereArrayContainsAny("sharedWithTeams", teamIDs)
                .get()
                .await()

            if (sharedWithTeams.isEmpty) emptyList<VisualizationDto>()

            sharedWithTeams.documents.mapNotNull { doc ->
                try {
                    doc.toObject(VisualizationDto::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAllVisualizationsByUserID(userID: String): List<VisualizationDto> {
        return try {
            val finalArray = mutableListOf<VisualizationDto>()
            val personal = getPersonalVisualizations(userID)
            val sharedWithUsers = getVisualizationsSharedWithUser(userID)
            val sharedWithTeams = getSharedVisualizationsByTeamsIntegratedByUser(userID)
            finalArray.addAll(personal)
            finalArray.addAll(sharedWithUsers)
            finalArray.addAll(sharedWithTeams)
            finalArray
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun publishVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>?,
        sharedWithTeams: List<String>?,
        isPersonal: Boolean
    ){
        try {
            // Publish to personal feed
            if (isPersonal) {
                createVisualization(Visualization(
                    "",
                    authorID,
                    title,
                    configJSON,
                    emptyList(),        // Not shared to users.
                    emptyList(),       // Not shared to teams.
                    Timestamp.now(),
                    emptyList()
                ))
            }
            // Share and post
            else {
                createVisualization(Visualization(
                    "",
                    authorID,
                    title,
                    configJSON,
                    sharedWithUsers ?: emptyList(),     // May include users instead of teams.
                    sharedWithTeams ?: emptyList(),     // May include teams instead of users.
                    Timestamp.now(),
                    emptyList()
                ))
            }
        } catch(ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }
}