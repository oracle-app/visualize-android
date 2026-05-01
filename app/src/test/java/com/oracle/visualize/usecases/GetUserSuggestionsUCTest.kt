package com.oracle.visualize.usecases

import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.fixtures.UserFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetUserSuggestionsUCTest {
    @MockK
    private lateinit var userRepository: UserRepository
    private lateinit var getUserSuggestionsUseCase: GetUserSuggestionsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getUserSuggestionsUseCase = GetUserSuggestionsUseCase(userRepository)
    }

    //Query Validation

    @Test
    fun blankQuery_returnsSuccessEmptyList_doesNotCallRepository() = runTest {
        // given
        val blankQuery = ""

        // when
        val result = getUserSuggestionsUseCase(blankQuery)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<ShareUser>(), result.getOrNull())
        coVerify(exactly = 0) { userRepository.getUserSuggestionsByEmail(any()) }
    }

    @Test
    fun whitespaceQuery_returnsSuccessEmptyList_doesNotCallRepository() = runTest {
        // given
        val whitespaceQuery = "   "

        // when
        val result = getUserSuggestionsUseCase(whitespaceQuery)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<ShareUser>(), result.getOrNull())
        coVerify(exactly = 0) { userRepository.getUserSuggestionsByEmail(any()) }
    }

    //Call Repository

    @Test
    fun validQuery_returnsResultSuccess_withUserList() = runTest {
        // given
        coEvery {
            userRepository.getUserSuggestionsByEmail(UserFixtures.VALID_QUERY)
        } returns UserFixtures.fakeShareUsers

        // when
        val result = getUserSuggestionsUseCase(UserFixtures.VALID_QUERY)

        // then
        assertTrue(result.isSuccess)
        assertEquals(UserFixtures.fakeShareUsers, result.getOrNull())
        coVerify(exactly = 1) { userRepository.getUserSuggestionsByEmail(UserFixtures.VALID_QUERY) }
    }

    @Test
    fun validQuery_withNoMatches_returnsSuccessEmptyList() = runTest {
        // given
        coEvery {
            userRepository.getUserSuggestionsByEmail(UserFixtures.VALID_QUERY)
        } returns emptyList()

        // when
        val result = getUserSuggestionsUseCase(UserFixtures.VALID_QUERY)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<ShareUser>(), result.getOrNull())
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery {
            userRepository.getUserSuggestionsByEmail(any())
        } throws exception

        // when
        val result = getUserSuggestionsUseCase(UserFixtures.VALID_QUERY)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

}