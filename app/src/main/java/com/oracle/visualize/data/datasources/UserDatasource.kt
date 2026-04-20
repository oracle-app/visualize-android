package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
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
                    ?: throw Exception("Error when parsing TeamDTO")
            } else {
                throw Exception("This user does not exist in the database.")
            }

        } catch (e: Exception) {
            throw e
        }
    }

}

//suspend fun getTeamsUserIsIn(userID: String): List<TeamDTO> {
//    return try {
//        val snapshot = firestore.collection("groups")
//            .whereArrayContains("membersID",userID)
//            .get()
//            .await()
//        snapshot.toObjects(TeamDTO::class.java)
//    } catch (e: Exception){
//        emptyList()
//    }
//}