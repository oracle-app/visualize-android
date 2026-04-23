package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDto
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val userRef = db.collection("users")
    private val teamRef = db.collection("teams")

    suspend fun getUserByUserID(userID: String): UserDTO {
        return try {
            val snapshot = userRef.document(userID).get().await()
            if(!snapshot.exists()){
                throw Exception("This user ID does not exist.")
            }
            snapshot.toObject(UserDTO::class.java) ?: throw Exception()
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getTeamsIntegratedByUser(userID: String): List<TeamDto> {
        return try {
            val snapshot = teamRef.whereArrayContains("memberIDs", userID).get().await()
            snapshot.toObjects(TeamDto::class.java)
        } catch (ex: Exception) {
            throw ex
        }
    }
}