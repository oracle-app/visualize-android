package com.oracle.visualize.domain.repositories
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User

/**
 * Interface defining the operations for user management.
 */
interface UserRepository {
    suspend fun getUserSuggestionsByEmail(email: String): AppResult<List<ShareUser>>
    suspend fun getUserByUserID(userId: String): AppResult<User>

    suspend fun uploadProfilePicture(userID: String, uri: String): AppResult<String>

    suspend fun setProfilePicture(userID: String, url: String): AppResult<Unit>

    suspend fun setChartTheme(userID: String, url: String): AppResult<Unit>

    suspend fun deleteProfilePicture(userID: String): AppResult<Unit>

    suspend fun updatePfp(userID: String, uri: String): AppResult<Unit>

}
