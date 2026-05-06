package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for user-related operations using Firestore.
 *
 * @property firestore The [FirebaseFirestore] instance used for database operations.
 */
@Singleton
class UserDatasource
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) {
        suspend fun getUserByID(userID: String): UserDTO {
            try {
                val snapshot =
                    firestore
                        .collection("users")
                        .document(userID)
                        .get()
                        .await()

<<<<<<< Updated upstream
    /**
     * Fetches a user by their unique identifier.
     *
     * @param userID The unique ID of the user.
     * @return The [UserDTO] representing the user.
     * @throws AppError.ParsingError If the document exists but cannot be parsed.
     * @throws AppError.NotFound If the user does not exist.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getUserByID(userID: String): UserDTO {
        try {
            val snapshot = firestore.collection("users")
                .document(userID)
                .get()
                .await()

            if (snapshot.exists()) {
                return snapshot.toObject(UserDTO::class.java)
                    ?: throw AppError.ParsingError("Error when parsing UserDTO for ID: $userID")
            } else {
                throw AppError.NotFound("User with ID $userID does not exist in the database.")
            }
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Network error while fetching user: ${e.message}")
        }
    }

    /**
     * Fetches teams that the specified user is a member of.
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsIntegratedByUser(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("teams").whereArrayContains("memberIDs", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching integrated teams: ${ex.message}")
        }
    }

    /**
     * Fetches user suggestions based on a partial email address.
     *
     * @param email The partial email string to search for.
     * @return A list of [UserDTO] objects matching the search criteria.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getUserSuggestionsForSearch(email: String): List<UserDTO> {
        return try {
            val snapshot = firestore.collection("users")
                .whereGreaterThanOrEqualTo("email", email)
                .whereLessThanOrEqualTo("email", email + "\uf8ff")
                .limit(5)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emptyList()
            } else {
                snapshot.toObjects(UserDTO::class.java)
            }
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Error fetching user suggestions: ${e.message}")
=======
                if (snapshot.exists()) {
                    return snapshot.toObject(UserDTO::class.java)
                        ?: throw Exception("Error when parsing TeamDTO")
                } else {
                    throw Exception("This user does not exist in the database.")
                }
            } catch (e: Exception) {
                throw e
            }
        }

        suspend fun getTeamsIntegratedByUser(userID: String): List<TeamDTO> =
            try {
                val snapshot =
                    firestore
                        .collection("teams")
                        .whereArrayContains("memberIDs", userID)
                        .get()
                        .await()
                snapshot.toObjects(TeamDTO::class.java)
            } catch (ex: Exception) {
                throw ex
            }

        suspend fun getUserSuggestionsForSearch(email: String): List<UserDTO> {
            try {
                val snapshot =
                    firestore
                        .collection("users")
                        .whereGreaterThanOrEqualTo("email", email)
                        .whereLessThanOrEqualTo("email", email + "\uf8ff")
                        .limit(5)
                        .get()
                        .await()
                if (snapshot.isEmpty) {
                    return emptyList()
                } else {
                    return snapshot.toObjects(UserDTO::class.java)
                }
            } catch (e: Exception) {
                throw e
            }
>>>>>>> Stashed changes
        }

<<<<<<< Updated upstream
    /**
     * Fetches groups (teams) that the specified user is in.
     * Note: Uses "groups" collection; verify if it should be "teams".
     *
     * @param userID The unique ID of the user.
     * @return A list of [TeamDTO] objects.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("groups")
                .whereArrayContains("membersID", userID)
                .get()
                .await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Error fetching user groups: ${e.message}")
        }
    }
}
=======
        suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> =
            try {
                val snapshot =
                    firestore
                        .collection("groups")
                        .whereArrayContains("membersID", userID)
                        .get()
                        .await()
                snapshot.toObjects(TeamDTO::class.java)
            } catch (e: Exception) {
                emptyList()
            }
    }
>>>>>>> Stashed changes
