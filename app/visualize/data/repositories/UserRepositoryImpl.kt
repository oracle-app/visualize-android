package com.oracle.visualize.data.repositories
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.dtos.UserDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toShareUser
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

<<<<<<< Updated upstream
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
=======
class UserRepositoryImpl
    @Inject
    constructor(
        private val userDatasource: UserDatasource,
    ) : UserRepository {
        override suspend fun getUserSuggestionsByEmail(email: String): List<ShareUser> =
            coroutineScope {
                val usersRaw: List<UserDTO> = userDatasource.getUserSuggestionsForSearch(email)
                val deferredUsers =
                    usersRaw.map { userDTO ->
                        async { userDTO.toShareUser() }
                    }
                deferredUsers.awaitAll()
>>>>>>> Stashed changes
            }

        override suspend fun getUserByUserID(userId: String): User = userDatasource.getUserByID(userId).toDomain()

        override suspend fun getTeamsIntegratedByUser(userId: String): List<Team> =
            userDatasource.getTeamsIntegratedByUser(userId).map {
                it.toDomain()
            }
    }
