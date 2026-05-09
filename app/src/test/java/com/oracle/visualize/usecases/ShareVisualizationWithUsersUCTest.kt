package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.ShareVisualizationWithUsersUseCase
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
 * Unit tests for [ShareVisualizationWithUsersUseCase].
 *
 *   ShareVisualizationWithUsersUseCase validates the visualizationID and userIDs before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic, it then adds the userIDs to the list
 *   linked to a visualization (sharedWithUsers).
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class ShareVisualizationWithUsersUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var shareVisualizationWithUsers: ShareVisualizationWithUsersUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        shareVisualizationWithUsers = ShareVisualizationWithUsersUseCase(visualizationRepository)
    }

    @Test
    fun `return success when both visualizationID and all the userIDs are valid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("1", "2", "3")
        coEvery { visualizationRepository.shareVisualizationWithUsers(visualizationID, userIDs) } returns Unit

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.shareVisualizationWithUsers(visualizationID, userIDs) }
    }

    @Test
    fun `return success when both visualizationID and some userIDs are valid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("1", "", "4")
        val expectedList = userIDs.filter { it.isNotBlank() }
        coEvery { visualizationRepository.shareVisualizationWithUsers(visualizationID, expectedList) } returns Unit

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.shareVisualizationWithUsers(visualizationID, expectedList) }
    }

    @Test
    fun `return failure when visualizationID is valid, but all the userIDs are invalid`() = runTest {
        // Given
        val visualizationID = VisualizationFixtures.VALID_VISUALIZATION_ID
        val userIDs = listOf("", "", "")

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but all the userIDs are valid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("1", "2", "3")

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is invalid, but some userIDs are valid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("1", "", "4")

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when both visualizationID and all the userIDs are invalid`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = listOf("", "", "")

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `return failure when visualizationID is valid, but the userIDs list is empty`() = runTest {
        // Given
        val visualizationID = ""
        val userIDs = emptyList<String>()

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

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

        coEvery { visualizationRepository.shareVisualizationWithUsers(visualizationID, userIDs) } throws exception

        // When
        val result = shareVisualizationWithUsers(visualizationID, userIDs)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
