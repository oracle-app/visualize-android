package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.ShareVisualizationWithTeamsUseCase
import com.oracle.visualize.fixtures.VisualizationFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ShareVisualizationWithTeamsUseCase].
 *
 *   ShareVisualizationWithTeamsUseCase validates the visualizationID and teamIDs before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it then adds the teamIDs to the list
 *   linked to a visualization (sharedWithTeams).
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class ShareVisualizationWithTeamsUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var shareVisualizationWithTeams: ShareVisualizationWithTeamsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        shareVisualizationWithTeams = ShareVisualizationWithTeamsUseCase(visualizationRepository)
    }

    @Test
    fun `return success when both visualizationID and all the teamIDs are valid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T1", "T2", "T3")
        coEvery { visualizationRepository.shareVisualizationWithTeams(visualizationID, teamIDs) } returns Unit

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.shareVisualizationWithTeams(visualizationID, teamIDs) }
    }

    @Test
    fun `return success when both visualizationID and some teamIDs are valid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T100", "", "T500")
        val expectedList = teamIDs.filter { it.isNotBlank() }
        coEvery { visualizationRepository.shareVisualizationWithTeams(visualizationID, expectedList) } returns Unit

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.shareVisualizationWithTeams(visualizationID, expectedList) }
    }

    @Test
    fun `return failure when visualizationID is valid, but all the teamIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("", "", "")

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but all the teamIDs are valid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("T46", "T56", "T78")

        // Whent
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but some teamIDs are valid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("T1", "", "T4")

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when both visualizationID and all the teamIDs are invalid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("", "", "")

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but the teamIDs list is empty`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = emptyList<String>()

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when an exception is thrown`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T1056", "T345", "T659")
        val exception = Exception("Network Error")

        coEvery { visualizationRepository.shareVisualizationWithTeams(visualizationID, teamIDs) } throws exception

        // When
        val result = shareVisualizationWithTeams(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
