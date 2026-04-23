package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.TeamDTO
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toShareTeam
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

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
}

