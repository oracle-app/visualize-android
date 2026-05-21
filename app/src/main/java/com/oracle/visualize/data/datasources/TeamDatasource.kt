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
 * Design note — ownerID inside membersIDs:
 *   The owner is always written into membersIDs so that:
 *     1. whereArrayContains("membersIDs", ownerID) returns their own teams,
 *        which powers the suggestions carousel in Create mode.
 *     2. Member count and member list in the UI always include the owner
 *        without any special-casing in the mapper or repository.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class TeamDatasource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val teamsRef = db.collection("teams")

    /**
     * Creates a new team.
     * The [ownerID] is merged into [membersIDs] before writing so that
     * the owner is always reflected in the full member list.
     */
    suspend fun createTeam(
        membersIDs: List<String>,
        name: String,
        ownerID: String
    ) {
        val allMemberIDs = (membersIDs + ownerID).distinct()
        val teamData = hashMapOf(
            "membersIDs" to allMemberIDs,
            "name"       to name,
            "ownerID"    to ownerID
        )
        teamsRef.add(teamData).await()
    }

    /**
     * Updates an existing team's name and member list.
     * The owner's ID must already be present in [membersIDs]; the caller
     * (ViewModel / UseCase) is responsible for passing the complete list.
     */
    suspend fun updateTeam(
        teamID: String,
        membersIDs: List<String>,
        name: String
    ) {
        val updates = mapOf(
            "membersIDs" to membersIDs,
            "name"       to name
        )
        teamsRef.document(teamID).update(updates).await()
    }

    /**
     * Deletes a team document by its unique ID.
     */
    suspend fun deleteTeam(teamID: String) {
        teamsRef.document(teamID).delete().await()
    }

    /**
     * Fetches a team by its unique ID.
     */
    suspend fun getTeamByTeamID(teamID: String): TeamDTO? {
        val teamSnapshot = teamsRef.document(teamID).get().await()
        if (!teamSnapshot.exists()) return null
        return teamSnapshot.toObject(TeamDTO::class.java)
            ?: throw AppError.ParsingError("Error parsing TeamDTO")
    }

    /**
     * Fetches all teams owned by a specific user.
     */
    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
        return snapshot.toObjects(TeamDTO::class.java)
    }

    /**
     * Fetches all teams that a specific user is a member of (including owned teams,
     * since the owner is stored in membersIDs).
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
