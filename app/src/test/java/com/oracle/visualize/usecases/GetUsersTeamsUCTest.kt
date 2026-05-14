package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import com.oracle.visualize.fixtures.TeamFixtures
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

/**
 * Unit tests for [GetUsersTeamsUseCase].
 *
 *   Covers both [getTeamsUserOwns] and [getTeamsUserIsIn], validating
 *   that blank user IDs are rejected before reaching the repository and
 *   that repository results and failures are propagated correctly.
 */
class GetUsersTeamsUCTest {

    @MockK
    private lateinit var teamRepository: TeamRepository
    private lateinit var getUsersTeamsUseCase: GetUsersTeamsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getUsersTeamsUseCase = GetUsersTeamsUseCase(teamRepository)
    }

    // getTeamsUserOwns — ID Validation

    @Test
    fun getTeamsUserOwns_blankUserId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankId = ""

        // when
        val result = getUsersTeamsUseCase.getTeamsUserOwns(blankId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { teamRepository.getTeamsOwnedByUser(any()) }
    }

    @Test
    fun getTeamsUserOwns_whitespaceUserId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val whitespaceId = "   "

        // when
        val result = getUsersTeamsUseCase.getTeamsUserOwns(whitespaceId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { teamRepository.getTeamsOwnedByUser(any()) }
    }

    // getTeamsUserOwns — Repository Calls

    @Test
    fun getTeamsUserOwns_validUserId_returnsResultSuccess_withTeamList() = runTest {
        // given
        coEvery {
            teamRepository.getTeamsOwnedByUser(UserFixtures.VALID_UID)
        } returns TeamFixtures.fakeTeamList

        // when
        val result = getUsersTeamsUseCase.getTeamsUserOwns(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(TeamFixtures.fakeTeamList, result.getOrNull())
        coVerify(exactly = 1) { teamRepository.getTeamsOwnedByUser(UserFixtures.VALID_UID) }
    }

    @Test
    fun getTeamsUserOwns_validUserId_withNoTeams_returnsSuccessEmptyList() = runTest {
        // given
        coEvery {
            teamRepository.getTeamsOwnedByUser(UserFixtures.VALID_UID)
        } returns emptyList()

        // when
        val result = getUsersTeamsUseCase.getTeamsUserOwns(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<ShareTeam>(), result.getOrNull())
    }

    @Test
    fun getTeamsUserOwns_repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery { teamRepository.getTeamsOwnedByUser(any()) } throws exception

        // when
        val result = getUsersTeamsUseCase.getTeamsUserOwns(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // getTeamsUserIsIn — ID Validation

    @Test
    fun getTeamsUserIsIn_blankUserId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankId = ""

        // when
        val result = getUsersTeamsUseCase.getTeamsUserIsIn(blankId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { teamRepository.getTeamsUserIsIn(any()) }
    }

    @Test
    fun getTeamsUserIsIn_whitespaceUserId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val whitespaceId = "   "

        // when
        val result = getUsersTeamsUseCase.getTeamsUserIsIn(whitespaceId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { teamRepository.getTeamsUserIsIn(any()) }
    }

    // getTeamsUserIsIn — Repository Calls

    @Test
    fun getTeamsUserIsIn_validUserId_returnsResultSuccess_withTeamList() = runTest {
        // given
        coEvery {
            teamRepository.getTeamsUserIsIn(UserFixtures.VALID_UID)
        } returns TeamFixtures.fakeTeamList

        // when
        val result = getUsersTeamsUseCase.getTeamsUserIsIn(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(TeamFixtures.fakeTeamList, result.getOrNull())
        coVerify(exactly = 1) { teamRepository.getTeamsUserIsIn(UserFixtures.VALID_UID) }
    }

    @Test
    fun getTeamsUserIsIn_validUserId_withNoTeams_returnsSuccessEmptyList() = runTest {
        // given
        coEvery {
            teamRepository.getTeamsUserIsIn(UserFixtures.VALID_UID)
        } returns emptyList()

        // when
        val result = getUsersTeamsUseCase.getTeamsUserIsIn(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<ShareTeam>(), result.getOrNull())
    }

    @Test
    fun getTeamsUserIsIn_repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery { teamRepository.getTeamsUserIsIn(any()) } throws exception

        // when
        val result = getUsersTeamsUseCase.getTeamsUserIsIn(UserFixtures.VALID_UID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
