package com.oracle.visualize.domain.repositories
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.ui.theme.ChartPalette

interface UserRepository {
    suspend fun getUsersByIDs(userIDs: List<String>): List<ShareUser>
    suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser>
    suspend fun getUserByUserID(userId: String): User

    suspend fun uploadProfilePicture(userID: String, uri: String): String

    suspend fun setProfilePicture(userID: String, url: String): Unit

    suspend fun setChartTheme(userID: String, url: String): Unit

    suspend fun deleteProfilePicture(userID: String): Unit

    suspend fun updatePfp(userID: String, uri: String): Unit

    suspend fun getChartTheme(userID: String): ChartPalette
}
