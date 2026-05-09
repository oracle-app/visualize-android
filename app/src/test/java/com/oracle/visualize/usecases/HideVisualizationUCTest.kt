package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.HideVisualizationUseCase
import com.oracle.visualize.fixtures.UserFixtures
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
 * Unit tests for [HideVisualizationUseCase].
 *
 *   HideVisualizationUseCase validates the visualizationID before delegating
 *   to [UserRepository]. The UseCase has no filter logic, it
 *   then adds the visualization to a user's hiddenVisualizations list.
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class HideVisualizationUCTest {

    @MockK
    private lateinit var userRepository: UserRepository
    private lateinit var hideVisualization: HideVisualizationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        hideVisualization = HideVisualizationUseCase(userRepository)
    }

    @Test
    fun `return success when hiding a visualization with valid ID`() = runTest {
        // Given
        val userID = UserFixtures.VALID_UID
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        coEvery { userRepository.hideVisualization(userID, visualizationID) } returns Unit

        // When
        val result = hideVisualization(userID, visualizationID)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userRepository.hideVisualization(userID, visualizationID) }
    }

    @Test
    fun `return failure when a visualizationID is invalid, but userID is valid`() = runTest {
        // Given
        val userID = UserFixtures.VALID_UID
        val visualizationID = ""

        // When
        val result = hideVisualization(userID, visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError)
    }

    @Test
    fun `return failure when a userID is invalid, but visualizationID is valid`() = runTest {
        // Given
        val userID = ""
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID

        // When
        val result = hideVisualization(userID, visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError)
    }

    @Test
    fun `return failure when both userID and visualizationID are invalid`() = runTest {
        // Given
        val userID = ""
        val visualizationID = ""

        // When
        val result = hideVisualization(userID, visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError)
    }

    @Test
    fun `return failure when a network exception is thrown`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userID = UserFixtures.VALID_UID
        val exception = Exception("Network Error")
        coEvery { userRepository.hideVisualization(userID, visualizationID) } throws exception

        // When
        val result = hideVisualization(userID, visualizationID)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
