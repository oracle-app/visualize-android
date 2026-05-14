package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard

/**
 * Interface defining the operations for visualization management.
 */
interface VisualizationRepository {
    suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    )
    suspend fun getAllVisualizations(): List<Visualization>
    suspend fun getSharedVisualizations(userID: String): List<VisualizationCard>
    suspend fun getPersonalVisualizations(userID: String): List<VisualizationCard>
    suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>)
}