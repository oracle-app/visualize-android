package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.DeleteUsersAccessToVisualizationUseCase
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
 * Unit tests for [DeleteUsersAccessToVisualizationUseCase].
 *
 *   DeleteUsersAccessToVisualizationUseCase validates the visualizationID and userIDs before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it then deletes the userIDs from the list
 *   linked to a visualization (sharedWithUsers).
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class DeleteUsersAccessToVisualizationUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var deleteUsersAccessToVisualization: DeleteUsersAccessToVisualizationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteUsersAccessToVisualization = DeleteUsersAccessToVisualizationUseCase(visualizationRepository)
    }

    @Test
    fun `return success when deleting users access with a valid visualizationID`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("1", "2", "3")
        coEvery { visualizationRepository.deleteUsersAccessToVisualization(visualizationID, userIDs) } returns Unit

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteUsersAccessToVisualization(visualizationID, userIDs) }
    }

    @Test
    fun `return success even when some userIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("1", "", "3", "4", "")
        val expectedList = userIDs.filter { it.isNotBlank() }

        coEvery { visualizationRepository.deleteUsersAccessToVisualization(visualizationID, expectedList) } returns Unit

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteUsersAccessToVisualization(visualizationID, expectedList) }
    }

    @Test
    fun `return failure when visualizationID is invalid, but all userIDs valid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("1", "2", "3")

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but some userIDs valid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("1", "", "3")

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when both visualizationID and userIDs are invalid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("", "", "")

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but all userIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("", "", "")

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but the userIDs list is empty`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = emptyList<String>()

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when an exception is thrown`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("1", "2", "3")
        val exception = Exception("Network Error")

        coEvery { visualizationRepository.deleteUsersAccessToVisualization(visualizationID, userIDs) } throws exception

        // When
        val result = deleteUsersAccessToVisualization(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
