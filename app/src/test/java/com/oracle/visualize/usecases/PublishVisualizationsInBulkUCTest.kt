package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.visualization.PublishVisualizationsInBulkUseCase
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
 * Unit tests for [PublishVisualizationsInBulkUseCase].
 *
 *   PublishVisualizationsInBulkUseCase validates the title, authorID, configJSON of each
 *   visualization before delegating to [VisualizationRepository]. The UseCase has no filter logic,
 *   it ensures the filter is forwarded correctly and that repository exceptions are caught
 *   and wrapped in Result.isFailure instead of crashing the caller.
 */

class PublishVisualizationsInBulkUCTest {
    @MockK
    private lateinit var visualizationRepository: VisualizationRepository

    private lateinit var publishVisualizationsInBulkUseCase: PublishVisualizationsInBulkUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        publishVisualizationsInBulkUseCase = PublishVisualizationsInBulkUseCase(visualizationRepository)
    }

    @Test
    fun `return success when all valid visualizations are published`() = runTest {
        // Given
        val visList = VisualizationFixtures.visListWhereAllAreValid
        coEvery { visualizationRepository.publishVisualizationsInBulk(visList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isSuccess)
        coVerify (exactly = 1) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure when a visualizations list is empty`() = runTest {
        // Given
        val visList = emptyList<Visualization>()

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return success even if one visualization has an empty author ID`() = runTest {
        // Given
        val visList = listOf(
            VisualizationFixtures.fakeValidVisualization,
            VisualizationFixtures.fakeValidVisualization.copy(
                id="2", title = "Vis 2", authorID = "", sharedWithUsers = listOf("1")
            )
        )

        val expectedList = visList.filter{ it.authorID.isNotBlank() }
        coEvery { visualizationRepository.publishVisualizationsInBulk(expectedList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isSuccess)
        coVerify (exactly = 1) {
            visualizationRepository.publishVisualizationsInBulk(expectedList)
        }
    }

    @Test
    fun `return success even if one visualization has an empty title`() = runTest {
        // Given
        val visList = listOf(
            VisualizationFixtures.fakeValidVisualization,
            VisualizationFixtures.fakeValidVisualization.copy(
                id="2", title = "", authorID = "2", sharedWithUsers = listOf("1")
            )
        )

        val expectedList = visList.filter{ it.title.isNotBlank() }
        coEvery { visualizationRepository.publishVisualizationsInBulk(expectedList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isSuccess)
        coVerify (exactly = 1) {
            visualizationRepository.publishVisualizationsInBulk(expectedList)
        }
    }

    @Test
    fun `return success even if one visualization has an empty ConfigJSON`() = runTest {
        // Given
        val visList = listOf(
            VisualizationFixtures.fakeValidVisualization,
            VisualizationFixtures.fakeValidVisualization.copy(
                id="2", title = "Vis 2", authorID = "2", configJSON = "",
                sharedWithUsers = listOf("1")
            )
        )

        val expectedList = visList.filter{ it.configJSON.isNotBlank() }
        coEvery { visualizationRepository.publishVisualizationsInBulk(expectedList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isSuccess)
        coVerify (exactly = 1) {
            visualizationRepository.publishVisualizationsInBulk(expectedList)
        }
    }

    @Test
    fun `return success when some visualizations valid and some invalid`() = runTest {
        // Given
        val visList = listOf(
            VisualizationFixtures.fakeValidVisualization,
            VisualizationFixtures.fakeValidVisualization.copy(
                id="2", title = "Vis 2", authorID = "2", configJSON = "{}",
                sharedWithUsers = listOf("1")
            ),
            VisualizationFixtures.fakeValidVisualization.copy(
                id="3", title = "", authorID = "", configJSON = "",
                sharedWithUsers = listOf("2")
            ),
            VisualizationFixtures.fakeValidVisualization.copy(
                id="4", title = "sdasdasd", authorID = "", configJSON = "",
                sharedWithTeams = listOf("2", "3")
            ),
        )

        val expectedList = visList.filter{
            it.authorID.isNotBlank() && it.title.isNotBlank() && it.configJSON.isNotBlank()
        }
        coEvery { visualizationRepository.publishVisualizationsInBulk(expectedList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isSuccess)
        coVerify (exactly = 1) {
            visualizationRepository.publishVisualizationsInBulk(expectedList)
        }
    }

    @Test
    fun `return failure when all visualizations are invalid`() = runTest {
        // Given
        val visList = VisualizationFixtures.visListWhereAllAreInvalid

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure when repository throws exception`() = runTest {
        // Given
        val visList = VisualizationFixtures.visListWhereAllAreValid
        val exception = Exception("Network Error")
        coEvery { visualizationRepository.publishVisualizationsInBulk(any()) } throws exception

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }
}
