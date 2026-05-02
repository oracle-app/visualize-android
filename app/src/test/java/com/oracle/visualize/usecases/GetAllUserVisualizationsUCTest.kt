package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
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
 *   to [VisualizationRepository]. The UseCase has no filter logic,
 *   it ensures the filter is forwarded
 *   correctly and that repository exceptions are caught and wrapped in
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
        val result = getAllUserVisualizations(blankUserID, VisualizationFilter.ALL)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) {
            visualizationRepository.getAllVisualizationsByUserID(any(), any())
        }
    }

    //Calls to the Repository

    @Test
    fun validUserID_returnsResultSuccess_withVisualizationList() = runTest {
        // given
        coEvery {
            visualizationRepository.getAllVisualizationsByUserID(
                VisualizationFixtures.VALID_USER_ID,
                VisualizationFilter.ALL
            )
        } returns VisualizationFixtures.fakeVisualizations

        // when
        val result = getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID,
            VisualizationFilter.ALL
        )

        // then
        assertTrue(result.isSuccess)
        assertEquals(VisualizationFixtures.fakeVisualizations, result.getOrNull())
        coVerify(exactly = 1) {
            visualizationRepository.getAllVisualizationsByUserID(
                VisualizationFixtures.VALID_USER_ID,
                VisualizationFilter.ALL
            )
        }
    }

    @Test
    fun validUserID_withNoVisualizations_returnsEmptyList() = runTest {
        // given
        coEvery {
            visualizationRepository.getAllVisualizationsByUserID(
                VisualizationFixtures.VALID_USER_ID,
                VisualizationFilter.ALL
            )
        } returns emptyList()

        // when
        val result = getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID,
            VisualizationFilter.ALL
        )

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<VisualizationCard>(), result.getOrNull())
    }

    @Test
    fun filterIsForwardedToRepository() = runTest {
        // given - verify the filter enum is forwarded, not hardcoded
        coEvery {
            visualizationRepository.getAllVisualizationsByUserID(
                VisualizationFixtures.VALID_USER_ID,
                VisualizationFilter.PERSONAL
            )
        } returns VisualizationFixtures.fakeVisualizations

        // when
        getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID,
            VisualizationFilter.PERSONAL
        )

        // then
        coVerify(exactly = 1) {
            visualizationRepository.getAllVisualizationsByUserID(
                VisualizationFixtures.VALID_USER_ID,
                VisualizationFilter.PERSONAL
            )
        }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Firestore unavailable")
        coEvery {
            visualizationRepository.getAllVisualizationsByUserID(any(), any())
        } throws exception

        // when
        val result = getAllUserVisualizations(
            VisualizationFixtures.VALID_USER_ID,
            VisualizationFilter.ALL
        )

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }


}