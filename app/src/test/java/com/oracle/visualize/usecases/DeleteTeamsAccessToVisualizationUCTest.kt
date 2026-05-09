package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.DeleteTeamsAccessToVisualizationUseCase
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
 * Unit tests for [DeleteTeamsAccessToVisualizationUseCase].
 *
 *   DeleteTeamsAccessToVisualizationUseCase validates the visualizationID and teamIDs before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it then deletes the teamIDs from the list
 *   linked to a visualization (sharedWithTeams).
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class DeleteTeamsAccessToVisualizationUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var deleteTeamsAccessToVisualization: DeleteTeamsAccessToVisualizationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteTeamsAccessToVisualization = DeleteTeamsAccessToVisualizationUseCase(visualizationRepository)
    }

    @Test
    fun `return success when deleting teams access with a valid visualizationID`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T123", "T322", "T543")
        coEvery { visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, teamIDs) } returns Unit

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, teamIDs) }
    }

    @Test
    fun `return success even when some teamIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T1", "", "T3", "T4", "")
        val expectedList = teamIDs.filter { it.isNotBlank() }

        coEvery { visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, expectedList) } returns Unit

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, expectedList) }
    }

    @Test
    fun `return failure when visualizationID is invalid, but all teamIDs valid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("T1", "T2", "T3")

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but some teamIDs valid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("T1", "", "T3")

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when both visualizationID and teamIDs are invalid`() = runTest {
        // Given
        val visualizationID = ""
        val teamIDs = listOf("", "", "")

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but all teamIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("", "", "")

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but teamIDs list is empty`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = emptyList<String>()

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when an exception is thrown`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val teamIDs = listOf("T1000", "T2546", "T3178")
        val exception = Exception("Network Error")
        
        coEvery { visualizationRepository.deleteTeamsAccessToVisualization(visualizationID, teamIDs) } throws exception

        // When
        val result = deleteTeamsAccessToVisualization(visualizationID, teamIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
