package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import android.net.Uri

/**
 * Interface defining the operations for user management.
 */
interface UserRepository {
    suspend fun getUserByUserID(userId: String): User?
    suspend fun getTeamsIntegratedByUser(userId: String): List<Team>
    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>

    suspend fun setProfilePicture(userID: String, uri: Uri): Boolean

}
