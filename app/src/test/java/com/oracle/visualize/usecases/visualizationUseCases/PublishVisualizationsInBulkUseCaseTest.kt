package com.oracle.visualize.usecases.visualizationUseCases

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.PublishVisualizationsInBulkUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.Date

class PublishVisualizationsInBulkUseCaseTest {
    @RelaxedMockK
    private lateinit var visualizationRepository: VisualizationRepository

    lateinit var publishVisualizationsInBulkUseCase: PublishVisualizationsInBulkUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        publishVisualizationsInBulkUseCase =
            PublishVisualizationsInBulkUseCase(visualizationRepository)
    }

    @Test
    fun `return success if all visualizations are published`() = runBlocking {
        val visList = listOf(
            Visualization(
                id = "1",
                authorID = "1",
                title = "Vis",
                configJSON = "{}",
                sharedWithUsers = emptyList(),
                sharedWithTeams = emptyList(),
                createdAt = Date()
            )
        )

        // Given
        coEvery {
            visualizationRepository
                .publishVisualizationsInBulk(visList)
        } returns Unit

        // When
        publishVisualizationsInBulkUseCase(visList)

        // Then
        assert(publishVisualizationsInBulkUseCase(visList).isSuccess)
    }
}