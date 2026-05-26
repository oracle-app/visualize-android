package com.oracle.visualize.data.datasources


import androidx.core.net.toUri
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
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
class UserDatasource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
        val snapshot = firestore.collection("users")
            .document(userID)
            .get()
            .await()

        if(!snapshot.exists()) {
            throw AppError.NotFound("User with ID $userID does not exist in the database.")
        }

        return snapshot.toObject(UserDTO::class.java)
            ?: throw AppError.ParsingError("Error when parsing UserDTO for ID: $userID")
    }

    /**
     * Fetches user suggestions based on a partial email address.
     *
     * @param email The partial email string to search for.
     * @return A list of [UserDTO] objects matching the search criteria.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getUserSuggestionsForSearch(email: String): List<UserDTO> {
        val snapshot = firestore.collection("users")
            .whereGreaterThanOrEqualTo("email", email)
            .whereLessThanOrEqualTo("email", email + "\uf8ff")
            .limit(5)
            .get()
            .await()

        return snapshot.toObjects(UserDTO::class.java)
    }

    suspend fun getUsersByIDs(ids: List<String>): List<UserDTO> {
        if (ids.isEmpty()) return emptyList()

        val snapshot = firestore.collection("users")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .await()

        return snapshot.toObjects(UserDTO::class.java)
    }

    suspend fun saveUserProfile(uid: String, user: UserDTO){
        firestore.collection("users").document(uid).set(user).await()
    }

    // UPLOAD FUNCTIONS

    // This function uploads an image in Uri format and returns the upload URL.

    suspend fun uploadProfilePicture(userID: String, uri: String): String {
        val processedUri = uri.toUri()
        val storageRef = storage.reference.child("users/$userID/profilePicture")
        storageRef.putFile(processedUri).await()

        return storageRef.downloadUrl.await().toString()
    }

    // This function uses the uid to update the user's profile picture URL with a new value.

    suspend fun setProfilePicture(userID: String, url: String) {
        firestore.collection("users")
            .document(userID)
            .update("profilePictureURL", url)
            .await()
    }

    suspend fun updatePfp(userID: String, uri: String) {
        val url = uploadProfilePicture(userID, uri)
        setProfilePicture(userID, url)
    }

    suspend fun setChartTheme(userID: String, selectedPalette: String) {
        firestore.collection("users")
            .document(userID)
            .update("chartTheme", selectedPalette)
            .await()
    }


    // DELETE

    //This function deletes the files at user/userID/profilePicture

    suspend fun deleteProfilePicture(userID: String) {
        try {
            storage.reference.child("users/$userID/profilePicture").delete().await()
        } catch (e: StorageException) {
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw AppError.NotFound("Image located at users/$userID/profilePicture could not be found.")
            } else throw e
        }
    }

}
