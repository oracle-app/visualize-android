package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.DeleteVisualizationUseCase
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
 * Unit tests for [DeleteVisualizationUseCase].
 *
 *   DeleteVisualizationUseCase validates the visualizationID before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it
 *   then deletes the visualization.
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class DeleteVisualizationUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var deleteVisualization: DeleteVisualizationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteVisualization = DeleteVisualizationUseCase(visualizationRepository)
    }

    @Test
    fun `return success when deleting a visualization with valid ID`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        coEvery { visualizationRepository.deleteVisualization(visualizationID) } returns Unit

        // When
        val result = deleteVisualization(visualizationID)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteVisualization(visualizationID) }
    }

    @Test
    fun `return failure when deleting a visualization with invalid ID`() = runTest {
        // Given
        val visualizationID = ""

        // When
        val result = deleteVisualization(visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when an exception is thrown`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val exception = Exception("Network Error")
        coEvery { visualizationRepository.deleteVisualization(visualizationID) } throws exception

        // When
        val result = deleteVisualization(visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
