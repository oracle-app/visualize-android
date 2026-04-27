package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserSuggestionsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(query: String): List<ShareUser> {
        return userRepository.getUserSuggestionsByEmail(query)
    }
}