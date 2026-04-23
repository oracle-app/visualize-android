package com.oracle.visualize.data.repositories
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import com.oracle.visualize.data.datasources.UserDataSource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.models.ShareUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser> {
        return coroutineScope {
            val usersRaw: List<UserDTO> = userDataSource.getUserSuggestionsForSearch(email)
            val deferredUsers = usersRaw.map { userDTO ->
                async { userDTO.toShareUser() }
            }
            deferredUsers.awaitAll()
        }
    }

    override suspend fun getUserByUserID(userId: String): User {
        return userDataSource.getUserByID(userId).toDomain()
    }

    override suspend fun getTeamsIntegratedByUser(userId: String): List<Team> {
        return userDataSource.getTeamsIntegratedByUser(userId).map { it.toDomain() }
    }
}

