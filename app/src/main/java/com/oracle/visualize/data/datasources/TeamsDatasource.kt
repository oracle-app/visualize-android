package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeamsDatasource @Inject constructor(
    private val firestore: FirebaseFirestore
){

    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("groups")
                .whereArrayContains("membersID",userID)
                .get()
                .await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (e: Exception){
            emptyList()
        }
    }

    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("groups")
                .whereEqualTo("ownerID", userID)
                .get()
                .await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

}