package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.domain.exceptions.AppError
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
            if (ownerID.isNotEmpty() && name.isNotEmpty() && memberIDs.isNotEmpty()) {
                val formattedVisualization = hashMapOf(
                    "memberIDs" to memberIDs,
                    "name" to name,
                    "ownerID" to ownerID
                )
                teamsRef.add(formattedVisualization).await()
            } else {
                throw AppError.ValidationError("OwnerID, Name and MemberIDs cannot be empty")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to create team: ${ex.message}")
        }
    }

    suspend fun getTeamByTeamID(teamID: String): TeamDTO? {
        return try {
            val teamSnapshot = teamsRef.document(teamID).get().await()
            if (teamSnapshot.exists()) {
                teamSnapshot.toObject(TeamDTO::class.java)
                    ?: throw AppError.ParsingError("Error parsing TeamDTO")
            } else {
                null
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch team: ${ex.message}")
        }
    }

    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch owned teams: ${ex.message}")
        }
    }

    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereArrayContains("membersIDs", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch user's teams: ${ex.message}")
        }
    }
}