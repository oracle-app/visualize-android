package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.DeleteVisualizationUseCase
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
 *   to [VisualizationRepository]. The UseCase has no filter logic,it
 *   then fetches shared and personal visualizations concurrently
 *   and combines the results.
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

        // When
        val result = deleteVisualization(visualizationID)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { visualizationRepository.deleteVisualization(visualizationID) }
    }
}