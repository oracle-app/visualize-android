package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppResult
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
class GetUserSuggestionsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<ShareUser>> {

        if (query.isBlank()) {
            return AppResult.Success(emptyList())
        }

        return userRepository.getUserSuggestionsByEmail(query)
    }
}
