package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

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

    override suspend fun getUserByUserID(userID: String): User? {
        return userDatasource.getUserByID(userID).toDomain()
    }
}
