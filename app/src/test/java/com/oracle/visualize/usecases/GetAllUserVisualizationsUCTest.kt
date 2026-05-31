package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.visualization.GetAllUserVisualizationsUseCase
import com.oracle.visualize.fixtures.VisualizationFixtures
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
 * Unit tests for [GetAllUserVisualizationsUseCase].
 *
 *   GetAllUserVisualizationsUseCase validates the userID before delegating
 *   to [VisualizationRepository]. The UseCase has no filter logic,it
 *   then fetches shared and personal visualizations concurrently
 *   and combines the results.
 *
 *   Repository exceptions are caught and wrapped in
 *   Result.isFailure instead of crashing the caller.
 */

class GetAllUserVisualizationsUCTest {

    @MockK
    private lateinit var visualizationRepository: VisualizationRepository
    private lateinit var getAllUserVisualizations: GetAllUserVisualizationsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getAllUserVisualizations = GetAllUserVisualizationsUseCase(visualizationRepository)
    }

    // No user ID

    @Test
    fun blankUserID_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankUserID = ""

        // when
        val result = getAllUserVisualizations(blankUserID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) {
            visualizationRepository.getSharedVisualizations(any(), false)
            visualizationRepository.getPersonalVisualizations(any(), false)
        }
    }

    //Calls to the Repository

    @Test
    fun validUserID_callsBothAndReturnsCombined_ResultSuccess() = runTest {
        // given
        coEvery {
            visualizationRepository.getSharedVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns VisualizationFixtures.fakeSharedVisualizations
        coEvery {
            visualizationRepository.getPersonalVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns VisualizationFixtures.fakePersonalVisualizations

        // when
        val result = getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(
            VisualizationFixtures.fakeSharedVisualizations
                    + VisualizationFixtures.fakePersonalVisualizations,
            result.getOrNull()
        )
        coVerify(exactly = 1) {
            visualizationRepository.getSharedVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
            visualizationRepository.getPersonalVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        }
    }

    @Test
    fun validUserID_withNoVisualizations_returnsEmptyList() = runTest {
        // given
        coEvery {
            visualizationRepository.getSharedVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns emptyList()
        coEvery {
            visualizationRepository.getPersonalVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns emptyList()

        // when
        val result = getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<VisualizationCard>(), result.getOrNull())
    }

    @Test
    fun validUserID_withOnlySharedVisualizations_returnsOnlySharedItems() = runTest {
        // given
        coEvery {
            visualizationRepository.getSharedVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns VisualizationFixtures.fakeSharedVisualizations
        coEvery {
            visualizationRepository.getPersonalVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns emptyList()

        // when
        val result = getAllUserVisualizations(VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(VisualizationFixtures.fakeSharedVisualizations,
            result.getOrNull())
    }

    @Test
    fun validUserID_withOnlyPersonalVisualizations_returnsOnlyPersonalItems() = runTest {
        // given
        coEvery {
            visualizationRepository.getSharedVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns emptyList()
        coEvery {
            visualizationRepository.getPersonalVisualizations(
                VisualizationFixtures.VALID_USER_ID, false)
        } returns VisualizationFixtures.fakePersonalVisualizations

        // when
        val result = getAllUserVisualizations(VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(VisualizationFixtures.fakePersonalVisualizations,
            result.getOrNull())
    }

    // Error Handling

    @Test
    fun sharedRepositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Firestore unavailable")
        coEvery {
            visualizationRepository.getSharedVisualizations(any(), false)
        } throws exception
        coEvery {
            visualizationRepository.getPersonalVisualizations(any(), false)
        } returns emptyList()

        // when
        val result = getAllUserVisualizations(VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun personalRepositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Firestore unavailable")
        coEvery {
            visualizationRepository.getSharedVisualizations(any(), false)
        } returns emptyList()
        coEvery {
            visualizationRepository.getPersonalVisualizations(any(), false)
        } throws exception

        // when
        val result = getAllUserVisualizations(VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun bothRepositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Firestore unavailable")
        coEvery {
            visualizationRepository.getSharedVisualizations(any(), false)
        } throws exception
        coEvery {
            visualizationRepository.getPersonalVisualizations(any(), false)
        } throws exception

        // when
        val result = getAllUserVisualizations(VisualizationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
