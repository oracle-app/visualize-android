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
 * Design note — ownerID is NOT stored inside membersIDs:
 *   The owner and members are kept as separate concepts so that:
 *     1. "My Teams" (owned) and "Teams I'm In" (member-only) sections
 *        remain meaningful and non-overlapping.
 *     2. Visualizations shared with a team are not fetched twice
 *        (once for personal, once for shared) causing duplicate feed items.
 *   The UI layer is responsible for displaying the owner alongside members
 *   when needed, using the ownerID field.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class TeamDatasource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val teamsRef = db.collection("teams")

    /**
     * Creates a new team.
     * The [ownerID] is stored separately and is NOT added to [membersIDs].
     */
    suspend fun createTeam(
        membersIDs: List<String>,
        name: String,
        ownerID: String
    ) {
        // Ensure the owner is never duplicated inside membersIDs
        val cleanMemberIDs = membersIDs.filter { it != ownerID }
        val teamData = hashMapOf(
            "membersIDs" to cleanMemberIDs,
            "name"       to name,
            "ownerID"    to ownerID
        )
        teamsRef.add(teamData).await()
    }

    /**
     * Updates an existing team's name and member list.
     * The caller must ensure [membersIDs] does NOT include the ownerID.
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
     * Fetches all teams owned by a specific user (ownerID == userID).
     */
    suspend fun getTeamsUserOwns(userID: String): List<TeamDTO> {
        val snapshot = teamsRef.whereEqualTo("ownerID", userID).get().await()
        return snapshot.toObjects(TeamDTO::class.java)
    }

    /**
     * Fetches all teams where the user is a member but NOT the owner.
     * Since ownerID is not in membersIDs, this query returns only non-owned teams.
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
