package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.usecases.team.DeleteTeamUseCase
import com.oracle.visualize.fixtures.TeamFixtures
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [DeleteTeamUseCase].
 *
 *   Validates that the use case rejects a blank team ID before reaching
 *   the repository, and correctly propagates both success and repository
 *   failures wrapped in [Result].
 */
class DeleteTeamUCTest {

    @MockK
    private lateinit var teamRepository: TeamRepository
    private lateinit var deleteTeamUseCase: DeleteTeamUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteTeamUseCase = DeleteTeamUseCase(teamRepository)
    }

    // ID Validation

    @Test
    fun blankTeamId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankId = ""

        // when
        val result = deleteTeamUseCase(blankId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { teamRepository.deleteTeam(any()) }
    }

    @Test
    fun whitespaceTeamId_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val whitespaceId = "   "

        // when
        val result = deleteTeamUseCase(whitespaceId)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { teamRepository.deleteTeam(any()) }
    }

    // Repository Calls

    @Test
    fun validTeamId_returnsResultSuccess_callsRepository() = runTest {
        // given
        coEvery { teamRepository.deleteTeam(TeamFixtures.VALID_TEAM_ID) } just Runs

        // when
        val result = deleteTeamUseCase(TeamFixtures.VALID_TEAM_ID)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { teamRepository.deleteTeam(TeamFixtures.VALID_TEAM_ID) }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery { teamRepository.deleteTeam(any()) } throws exception

        // when
        val result = deleteTeamUseCase(TeamFixtures.VALID_TEAM_ID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
