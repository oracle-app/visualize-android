package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeamDatasource @Inject constructor(
    private val db: FirebaseFirestore
){
    private val teamsRef = db.collection("teams")

    suspend fun createTeam(
        memberIDs: List<String>,
        name: String,
        ownerID: String
    ) {
        try {
            if (ownerID.isNotEmpty() && name.isNotEmpty() &&
                memberIDs.isNotEmpty()) {

                val formattedVisualization = hashMapOf(
                    "memberIDs" to memberIDs,
                    "name" to name,
                    "ownerID" to ownerID
                )

                teamsRef.add(formattedVisualization).await()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    suspend fun getTeamByTeamID(teamID: String): TeamDto? {
        return try {
            val teamSnapshot = teamsRef.document(teamID)
                .get().await()
            if (teamSnapshot.exists()){
                teamSnapshot.toObject(TeamDto::class.java)
            } else {
                null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}