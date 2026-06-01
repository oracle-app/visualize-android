package com.oracle.visualize.data.repositories

import android.net.Uri
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.core.utils.safeApiCall
import com.oracle.visualize.domain.models.ShareUser

/**
 * Implementation of [UserRepository] to manage user-specific data.
 *
 * @property userDatasource Data source for user operations in Firestore.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
) : UserRepository {
    override suspend fun getUserSuggestionsByEmail(email: String): AppResult<List<ShareUser>> {
        return safeApiCall {

    override suspend fun getUsersByIDs(userIDs: List<String>): List<ShareUser> {
        return try {
            userDatasource.getUsersByIDs(userIDs).map { it.toShareUser() }
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch users by IDs: ${e.message}")
        }
    }

    override suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser> {
        return try {
            val usersRaw: List<UserDTO> = userDatasource.getUserSuggestionsForSearch(email)
            usersRaw.map { userDTO -> userDTO.toShareUser() }

        } catch (e: AppError) {
            throw e
        } catch (e: Exception) {
            throw AppError.NetworkError("Failed to fetch user suggestions: ${e.message}")
        }
    }

    override suspend fun getUserByUserID(userID: String): AppResult<User> {
        return safeApiCall {
            userDatasource
                .getUserByID(userID)
                .toDomain()
        }
    }

    override suspend fun uploadProfilePicture(userID: String, uri: String): AppResult<String> {
        return safeApiCall {
            userDatasource.uploadProfilePicture(userID, uri)
        }
    }

    override suspend fun setProfilePicture(userId: String, url: String): AppResult<Unit> {
        return safeApiCall {
            userDatasource.setProfilePicture(userId, url)
        }
    }

    override suspend fun setChartTheme(userId: String, selectedPalette: String): AppResult<Unit> {
        return safeApiCall {
            userDatasource.setChartTheme(userId, selectedPalette)
        }
    }

    override suspend fun deleteProfilePicture(userId: String): AppResult<Unit> {
        return safeApiCall {
            userDatasource.deleteProfilePicture(userId)
        }
    }

    override suspend fun updatePfp(userId: String, uri: String): AppResult<Unit> {
        return safeApiCall {
            userDatasource.updatePfp(userId, uri)
        }
    }
}
