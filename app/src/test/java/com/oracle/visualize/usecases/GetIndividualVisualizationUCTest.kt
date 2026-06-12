package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.visualization.GetIndividualVisualizationUseCase
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
 * Unit tests for [GetIndividualVisualizationUseCase].
 *
 *   GetIndividualVisualizationUseCase validates the visualizationID before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it then fetches
 *   the visualization.
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class GetIndividualVisualizationUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var getIndividualVisualization: GetIndividualVisualizationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getIndividualVisualization = GetIndividualVisualizationUseCase(visualizationRepository)
    }

    // No visualization ID
    @Test
    fun `returns ValidationError when visualizationID is invalid, not calling the Repository`() = runTest {
        // Given
        val invalidVisualizationID = ""

        // When
        val result = getIndividualVisualization(invalidVisualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) {
            visualizationRepository.getIndividualVisualization(any())
        }
    }

    // Has visualization ID
    @Test
    fun `returns success when visualizationID is valid, calling the Repository once`() = runTest {
        // Given
        val validVisualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val fakeVisualization = VisualizationFixtures.fakeValidVisualizationFullScreen

        // When
        coEvery { visualizationRepository.getIndividualVisualization(validVisualizationID) } returns fakeVisualization
        val result = getIndividualVisualization(validVisualizationID)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            visualizationRepository.getIndividualVisualization(visualizationID = validVisualizationID)
        }
    }

    // Network Error
    @Test
    fun `return failure when repository throws an exception`() = runTest {
        // Given
        val validVisualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val exception = Exception("Network Error")
        coEvery { visualizationRepository.getIndividualVisualization(any()) } throws exception

        // When
        val result = getIndividualVisualization(validVisualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
