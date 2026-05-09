package com.oracle.visualize.data.repositories
import android.net.Uri
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [UserRepository] to manage user-specific data.
 *
 * @property userDatasource Data source for user operations in Firestore.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
) : UserRepository {
    override suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser> {
        return coroutineScope {
            val usersRaw: List<UserDTO> = userDatasource.getUserSuggestionsForSearch(email)
            val deferredUsers = usersRaw.map { userDTO ->
                async { userDTO.toShareUser() }
            }
            deferredUsers.awaitAll()
        }
    }

    override suspend fun getUserByUserID(userId: String): User {
        return userDatasource.getUserByID(userId).toDomain()
    }

    override suspend fun getTeamsIntegratedByUser(userId: String): List<Team> {
        return userDatasource.getTeamsIntegratedByUser(userId).map { it.toDomain() }
    }

    override suspend fun uploadProfilePicture(userID: String, uri: Uri): String {
        return uploadProfilePicture(userID, uri)
    }

    override suspend fun setProfilePicture(userId: String, url: String): Boolean {
        return userDatasource.setProfilePicture(userId, url)
    }

    override suspend fun setChartTheme(userId: String, selectedPalette: String): Boolean {
        return userDatasource.setChartTheme(userId, selectedPalette)
    }
}

