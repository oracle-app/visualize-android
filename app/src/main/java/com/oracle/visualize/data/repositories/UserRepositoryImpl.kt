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

/**
 * Implementation of [UserRepository] to manage user-specific data.
 *
 * @property userDatasource Data source for user operations in Firestore.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
) : UserRepository {
    override suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser> {
        return try {
            val usersRaw: List<UserDTO> = userDatasource.getUserSuggestionsForSearch(email)

            usersRaw.map { userDTO -> userDTO.toShareUser() }

        } catch (e: AppError) {
            throw e
        } catch (e: Exception){
            throw AppError.NetworkError("Failed to fetch user suggestions: ${e.message}")
        }
    }

    override suspend fun getUserByUserID(userID: String): User {
        return userDatasource
            .getUserByID(userID)
            .toDomain()
    }

    override suspend fun uploadProfilePicture(userID: String, uri: String): String {
        return userDatasource.uploadProfilePicture(userID, uri)
    }

    override suspend fun setProfilePicture(userId: String, url: String): Unit {
        return userDatasource.setProfilePicture(userId, url)
    }

    override suspend fun setChartTheme(userId: String, selectedPalette: String): Unit {
        return userDatasource.setChartTheme(userId, selectedPalette)
    }

    override suspend fun deleteProfilePicture(userId: String): Unit {
        return userDatasource.deleteProfilePicture(userId)
    }

    override suspend fun updatePfp(userId: String, uri: String): Unit {
        return userDatasource.updatePfp(userId, uri)
    }

}

