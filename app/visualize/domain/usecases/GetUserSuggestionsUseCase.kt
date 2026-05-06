package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to fetch user suggestions based on an email query.
 *
 * @property userRepository The repository to fetch user data from.
 */
@Singleton
<<<<<<< Updated upstream
class GetUserSuggestionsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<ShareUser>> {

        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            val suggestions = userRepository.getUserSuggestionsByEmail(query)
            Result.success(suggestions)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
=======
class GetUserSuggestionsUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun execute(query: String): List<ShareUser> = userRepository.getUserSuggestionsByEmail(query)
>>>>>>> Stashed changes
    }
