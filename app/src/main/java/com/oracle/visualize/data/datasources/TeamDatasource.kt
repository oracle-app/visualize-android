package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.models.Team
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

    suspend fun getTeamByTeamID(teamID: String): TeamDTO? {
        return try {
            val teamSnapshot = teamsRef.document(teamID)
                .get().await()
            if (teamSnapshot.exists()){
                teamSnapshot.toObject(TeamDTO::class.java)
            } else {
                null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereArrayContains("membersIDs", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            emptyList()
        }
    }
}
