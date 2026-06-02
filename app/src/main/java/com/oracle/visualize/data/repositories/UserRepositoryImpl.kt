package com.oracle.visualize.data.repositories

import android.net.Uri
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.ui.theme.ChartPalette
import javax.inject.Inject

/**
 * Implementation of [UserRepository] to manage user-specific data.
 *
 * @property userDatasource Data source for user operations in Firestore.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
) : UserRepository {

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

    override suspend fun getChartTheme(userID: String): ChartPalette {
        return try {
            val color = userDatasource.getChartTheme(userID)
            when (color) {
                "THEME1" -> ChartPalette.THEME1
                "THEME2" -> ChartPalette.THEME2
                "THEME3" -> ChartPalette.THEME3
                "THEME4" -> ChartPalette.THEME4
                else -> ChartPalette.THEME1
            }
        } catch (e: AppError.NotFound) {
            throw AppError.NotFound("User not found: ${e.message}")
        } catch (e: AppError.NetworkError) {
            throw AppError.NetworkError("Failed to get the chart theme: ${e.message}")
        }
    }
}
