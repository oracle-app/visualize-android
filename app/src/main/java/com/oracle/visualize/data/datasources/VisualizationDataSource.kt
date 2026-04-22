package com.oracle.visualize.data.datasources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.domain.models.Visualization
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await


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
            collectionRef.add(formattedVisualization)
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getAllVisualizations(): List<Visualization> {
        return try {
            val snapshot = collectionRef.get().await()
            snapshot.toObjects(Visualization::class.java)
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getSharedVisualizationsByUser(userID: String): List<Visualization> {
        return try {
            val snapshot = collectionRef.get().await()
            snapshot.toObjects(Visualization::class.java).filter {
                it.authorID == userID || it.sharedWithUsers.contains(userID)
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getPersonalVisualizations(userID: String): List<Visualization> {
        return try {
            val snapshot = collectionRef.get().await()
            snapshot.toObjects(Visualization::class.java).filter {
                it.authorID == userID
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }


}