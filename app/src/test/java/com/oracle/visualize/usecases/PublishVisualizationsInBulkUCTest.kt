package com.oracle.visualize.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.usecases.PublishVisualizationsInBulkUseCase
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
        coEvery { visualizationRepository.publishVisualizationsInBulk(emptyList<Visualization>()) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(emptyList<Visualization>())

        // Then
        assertTrue(result.isFailure)
        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(emptyList<Visualization>())
        }
    }

    @Test
    fun `return success even if one visualization has an empty author ID`() = runTest {
        // Given
        val visList = VisualizationFixtures.visListWhereOneHasEmptyAuthorID
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
        val visList = VisualizationFixtures.visListWhereOneHasEmptyTitle
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
        val visList = VisualizationFixtures.visListWhereOneHasEmptyConfigJSON
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
        val visList = VisualizationFixtures.visListWhereSomeAreValidAndSomeInvalid
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
        coEvery { visualizationRepository.publishVisualizationsInBulk(visList) } returns Unit

        // When
        val result = publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(result.isFailure)
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
        assertEquals(exception, result.exceptionOrNull())
    }
}