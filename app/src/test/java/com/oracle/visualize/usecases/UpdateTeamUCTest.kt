package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.usecases.UpdateTeamUseCase
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
 * Unit tests for [UpdateTeamUseCase].
 *
 *   Validates that the use case rejects blank names and empty member lists
 *   before reaching the repository, and correctly propagates both success
 *   and repository failures wrapped in [Result].
 */
class UpdateTeamUCTest {

    @MockK
    private lateinit var teamRepository: TeamRepository
    private lateinit var updateTeamUseCase: UpdateTeamUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        updateTeamUseCase = UpdateTeamUseCase(teamRepository)
    }

    // Name Validation

    @Test
    fun blankName_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankName = ""

        // when
        val result = updateTeamUseCase(TeamFixtures.VALID_TEAM_ID, TeamFixtures.VALID_MEMBER_IDS, blankName)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { teamRepository.updateTeam(any(), any(), any()) }
    }

    @Test
    fun whitespaceName_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val whitespaceName = "   "

        // when
        val result = updateTeamUseCase(TeamFixtures.VALID_TEAM_ID, TeamFixtures.VALID_MEMBER_IDS, whitespaceName)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { teamRepository.updateTeam(any(), any(), any()) }
    }

    // Member List Validation

    @Test
    fun emptyMemberList_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val emptyMembers = emptyList<String>()

        // when
        val result = updateTeamUseCase(TeamFixtures.VALID_TEAM_ID, emptyMembers, TeamFixtures.VALID_TEAM_NAME)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { teamRepository.updateTeam(any(), any(), any()) }
    }

    // Repository Calls

    @Test
    fun validParams_returnsResultSuccess_callsRepository() = runTest {
        // given
        coEvery {
            teamRepository.updateTeam(
                TeamFixtures.VALID_TEAM_ID,
                TeamFixtures.VALID_MEMBER_IDS,
                TeamFixtures.VALID_TEAM_NAME
            )
        } just Runs

        // when
        val result = updateTeamUseCase(
            TeamFixtures.VALID_TEAM_ID,
            TeamFixtures.VALID_MEMBER_IDS,
            TeamFixtures.VALID_TEAM_NAME
        )

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            teamRepository.updateTeam(
                TeamFixtures.VALID_TEAM_ID,
                TeamFixtures.VALID_MEMBER_IDS,
                TeamFixtures.VALID_TEAM_NAME
            )
        }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery { teamRepository.updateTeam(any(), any(), any()) } throws exception

        // when
        val result = updateTeamUseCase(
            TeamFixtures.VALID_TEAM_ID,
            TeamFixtures.VALID_MEMBER_IDS,
            TeamFixtures.VALID_TEAM_NAME
        )

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
