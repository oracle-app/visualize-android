package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
    }
}