package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for user-related operations using Firestore.
 *
 * @property firestore The [FirebaseFirestore] instance used for database operations.
 */
@Singleton
class UserDatasource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

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
        }
    }

    suspend fun getUsersByIDs(ids: List<String>): List<UserDTO> {
        if (ids.isEmpty()) return emptyList()

        val snapshot = firestore.collection("users")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .await()
        return snapshot.toObjects(UserDTO::class.java)
    }

    /**
     * Adds a visualization ID to the user's hidden visualizations list in Firestore.
     *
     * @param userID The unique ID of the user.
     * @param visualizationId The visualization ID to hide.
     * @throws AppError.NetworkError If the operation fails.
     */
    suspend fun hideVisualizationForUser(userID: String, visualizationId: String) {
        try {
            firestore.collection("users").document(userID)
                .update("hiddenVisualizations", FieldValue.arrayUnion(visualizationId))
                .await()
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to hide visualization: ${ex.message}")
        }
    }
}
