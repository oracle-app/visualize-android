package com.oracle.visualize.usecases

import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.PublishVisualizationsInBulkUseCase
import com.oracle.visualize.fixtures.VisualizationFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
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
    fun `return success if all visualizations are published`() = runBlocking {
        val visList = VisualizationFixtures.visListWhereAllAreValid

        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(visList).isSuccess)

        coVerify (exactly = 2) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure if visualizations list is empty`() = runBlocking {
        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(emptyList())
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(emptyList())

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(emptyList()).isFailure)

        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(emptyList())
        }
    }

    @Test
    fun `return failure if one visualization has an empty author ID`() = runBlocking {
        val visList = VisualizationFixtures.visListWhereOneHasEmptyAuthorID

        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(visList).isFailure)

        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure if one visualization has an empty title`() = runBlocking {
        val visList = VisualizationFixtures.visListWhereOneHasEmptyTitle

        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(visList).isFailure)

        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure if one visualization has an empty ConfigJSON`() = runBlocking {
        val visList = VisualizationFixtures.visListWhereOneHasEmptyConfigJSON

        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(visList).isFailure)

        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }

    @Test
    fun `return failure when are visualizations are invalid`() = runBlocking {
        val visList = VisualizationFixtures.visListWhereAllAreInvalid

        // Given
        coEvery {
            visualizationRepository.publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assertTrue(publishVisualizationsInBulkUseCase(visList).isFailure)

        coVerify (exactly = 0) {
            visualizationRepository.publishVisualizationsInBulk(visList)
        }
    }
}