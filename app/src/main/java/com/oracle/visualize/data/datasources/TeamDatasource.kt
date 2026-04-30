package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeamDatasource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val teamsRef = db.collection("teams")

    suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String) {
        try {
            if (ownerID.isNotEmpty() && name.isNotEmpty() && memberIDs.isNotEmpty()) {
                val data = hashMapOf(
                    "memberIDs" to memberIDs,
                    "name"      to name,
                    "ownerID"   to ownerID
                )
                teamsRef.add(data).await()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String) {
        try {
            val updates = mapOf(
                "memberIDs" to memberIDs,
                "name"      to name
            )
            teamsRef.document(teamID).update(updates).await()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    suspend fun deleteTeam(teamID: String) {
        try {
            teamsRef.document(teamID).delete().await()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    suspend fun getTeamByTeamID(teamID: String): TeamDTO? {
        return try {
            val snapshot = teamsRef.document(teamID).get().await()
            if (snapshot.exists()) snapshot.toObject(TeamDTO::class.java) else null
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