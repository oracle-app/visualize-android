package com.oracle.visualize.data.datasources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.VisualizationDto
import com.oracle.visualize.domain.models.Visualization
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.*
import kotlin.collections.emptyList


class VisualizationDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val collectionRef = db.collection("visualizations")

    suspend fun createVisualization(visualization: Visualization) {
        val formattedVisualization = hashMapOf(
            "authorID" to visualization.authorID,
            "title" to visualization.title,
            "configJSON" to visualization.configJSON,
            "sharedWithUsers" to visualization.sharedWithUsers,
            "sharedWithTeams" to visualization.sharedWithTeams,
            "createdAt" to visualization.createdAt,
            "comments" to visualization.comments
        )

        try {
            collectionRef.add(formattedVisualization).await()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }
    suspend fun getAllVisualizations(): List<VisualizationDto> {
        return try {
            val snapshot = collectionRef.get().await()
            snapshot.toObjects(VisualizationDto::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getSharedVisualizationsByUser(userID: String): List<VisualizationDto> {
        return try {
            val snapshot = collectionRef
                .where(
                    Filter.or(
                        Filter.equalTo("authorID", userID),
                        Filter.arrayContains("sharedWithUsers", userID)
                    )
                )
                .get()
                .await()
            snapshot.toObjects(VisualizationDto::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPersonalVisualizations(userID: String): List<VisualizationDto> {
        return try {
            val snapshot = collectionRef
                .whereEqualTo("authorID", userID)
                .get()
                .await()
            snapshot.toObjects(VisualizationDto::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }


}