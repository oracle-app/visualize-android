package com.oracle.visualize.data.datasources

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
) {
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
    suspend fun createTeam(memberIDs: List<String>, name: String, ownerID: String) {
        try {
            if (ownerID.isNotEmpty() && name.isNotEmpty() && memberIDs.isNotEmpty()) {
                val formattedTeam = hashMapOf(
                    "membersIDs" to memberIDs,  // ← era "memberIDs"
                    "name"       to name,
                    "ownerID"    to ownerID
                )
                teamsRef.add(formattedTeam).await()
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
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch user's teams: ${ex.message}")
        }
    }

    /**
     * Updates an existing team's name and member list.
     *
     * @param teamID The unique ID of the team to update.
     * @param memberIDs The new list of member user IDs.
     * @param name The new team name.
     * @throws AppError.ValidationError If any parameter is blank.
     * @throws AppError.NetworkError If the operation fails.
     */
    suspend fun updateTeam(teamID: String, memberIDs: List<String>, name: String) {
        try {
            require(teamID.isNotBlank()) { "Team ID cannot be empty" }
            require(name.isNotBlank())   { "Team name cannot be empty" }
            teamsRef.document(teamID).update(
                mapOf(
                    "name"       to name,
                    "membersIDs" to memberIDs  // ← era "memberIDs"
                )
            ).await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to update team: ${ex.message}")
        }
    }

    /**
     * Deletes a team document by its ID.
     *
     * @param teamID The unique ID of the team to delete.
     * @throws AppError.NetworkError If the operation fails.
     */
    suspend fun deleteTeam(teamID: String) {
        try {
            teamsRef.document(teamID).delete().await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to delete team: ${ex.message}")
        }
    }

    /**
     * Fetches multiple teams by a list of IDs (max 30 per Firestore whereIn limit).
     *
     * @param teamIDs List of team IDs to fetch (max 30).
     * @return A list of [TeamDTO] matching the provided IDs.
     * @throws AppError.NetworkError If the operation fails.
     */
    suspend fun getTeamsByIDs(teamIDs: List<String>): List<TeamDTO> {
        if (teamIDs.isEmpty()) return emptyList()
        return try {
            val snapshot = teamsRef.whereIn("__name__", teamIDs).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to fetch teams by IDs: ${ex.message}")
        }
    }
}
