package com.oracle.visualize.data.datasources

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamDatasource @Inject constructor(
    private val firestore: FirebaseFirestore
){

    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        Log.d("TeamDatasource", "Fetching teams for userID: $userID")
        return try {
            val snapshot = firestore.collection("teams")
                .whereArrayContains("membersIDs",userID)
                .get()
                .await()
            Log.d("TeamDatasource", "Got ${snapshot.size()} teams")
            snapshot.toObjects(TeamDTO::class.java)

        } catch (e: Exception){
            Log.e("TeamDatasource", "Error getting teams user is in: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        return try {
            Log.d("TeamDatasource", "Fetching teams for userID: $userID")
            val snapshot = firestore.collection("teams")
                .whereEqualTo("ownerID", userID)
                .get()
                .await()
            Log.d("TeamDatasource", "Got ${snapshot.size()} teams")
            snapshot.toObjects(TeamDTO::class.java)
        } catch (e: Exception) {
            Log.e("TeamDatasource", "Error getting teams user is in: ${e.message}")
            emptyList()
        }
    }

}