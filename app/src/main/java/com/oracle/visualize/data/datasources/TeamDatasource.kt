package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
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
     * @param membersIDs List of user IDs to be added as members.
     * @param name The name of the team.
     * @param ownerID The user ID of the team owner.
     * @throws AppError.TeamValidationError If any of the parameters are empty.
     * @throws AppError.NetworkError If the operation fails due to a network issue.
     */
    suspend fun createTeam(
        membersIDs: List<String>,
        name: String,
        ownerID: String
    ) {
        val teamData = hashMapOf(
            "membersIDs" to membersIDs,
            "name" to name,
            "ownerID" to ownerID
        )
        teamsRef.add(teamData).await()
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
        val teamSnapshot = teamsRef.document(teamID).get().await()

        if (!teamSnapshot.exists()) return null

        return teamSnapshot.toObject(
            TeamDTO::class.java) ?: throw AppError.ParsingError("Error parsing TeamDTO")
    }

    /**
     * Fetches all teams owned by a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects owned by the user.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
        return snapshot.toObjects(TeamDTO::class.java)
    }

    /**
     * Fetches all teams that a specific user is a member of.
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects where the user is a member.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        val snapshot = teamsRef.whereArrayContains("membersIDs", userID).get().await()
        if (snapshot.isEmpty) return emptyList()

        return snapshot.documents.map { doc ->
            doc.toObject(TeamDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse TeamDTO: ${doc.id}")
        }
    }

    suspend fun getTeamsByIDs(ids: List<String>): List<TeamDTO> {
        if (ids.isEmpty()) return emptyList()

        val snapshot = db.collection("teams")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            doc.toObject(TeamDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse TeamDTO: ${doc.id}")
        }
    }
}
