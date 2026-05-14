package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source for team-related operations using Firestore.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class TeamDatasource @Inject constructor(
    private val db: FirebaseFirestore
){
    private val teamsRef = db.collection("teams")

    /**
     * Creates a new team in the database.
     *
     * @param memberIDs List of user IDs to be added as members.
     * @param name The name of the team.
     * @param ownerID The user ID of the team owner.
     * @throws AppError.ValidationError If any of the parameters are empty.
     * @throws AppError.NetworkError If the operation fails due to a network issue.
     */
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

    /**
     * Fetches a team by its unique ID.
     *
     * @param teamID The unique ID of the team.
     * @return The [TeamDTO] if found, null otherwise.
     * @throws AppError.ParsingError If the document exists but cannot be parsed.
     * @throws AppError.NetworkError If a network error occurs.
     */
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

    /**
     * Fetches all teams owned by a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects owned by the user.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch owned teams: ${ex.message}")
        }
    }

    /**
     * Fetches all teams that a specific user is a member of.
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects where the user is a member.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = teamsRef.whereArrayContains("membersIDs", userID).get().await()
            if (snapshot.isEmpty) return emptyList()

            snapshot.documents.map { doc ->
                doc.toObject(TeamDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse TeamDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch user's teams: ${ex.message}")
        }
    }

    suspend fun getTeamsByIDs(ids: List<String>): List<TeamDTO> {
        if (ids.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("teams")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()
            snapshot.documents.map { doc ->
                doc.toObject(TeamDTO::class.java)
                    ?: throw AppError.ParsingError("Failed to parse TeamDTO: ${doc.id}")
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch user's teams: ${ex.message}")
        }
    }
}