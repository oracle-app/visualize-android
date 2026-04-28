package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDatasource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

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

    suspend fun getTeamsIntegratedByUser(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("teams").whereArrayContains("memberIDs", userID).get().await()
            snapshot.toObjects(TeamDTO::class.java)
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Error fetching integrated teams: ${ex.message}")
        }
    }

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

    suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
        return try {
            val snapshot = firestore.collection("groups") // Nota: Revisa si esto debería ser "teams" o "groups"
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