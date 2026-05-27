package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.VisualizationFullScreen
import com.oracle.visualize.domain.models.VisualizationSharedData

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
    suspend fun getSharedVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard>
    suspend fun getPersonalVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard>
    suspend fun getUserFeedVisualizations(userID: String, forceRefresh: Boolean): List<VisualizationCard>
    suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>)

    /** Returns the full screen model for a single visualization. */
    suspend fun getIndividualVisualization(visualizationID: String): VisualizationFullScreen?

    /** Returns the sharing metadata (sharedWithUsers, sharedWithTeams) for a single visualization. */
    suspend fun getVisualizationById(visualizationId: String): VisualizationSharedData?

    /** Permanently deletes a visualization and removes it from every recipient's feed. */
    suspend fun deleteVisualizationForEveryone(visualizationId: String)

    /** Hides a visualization from the current user's feed without deleting it. */
    suspend fun hideVisualizationForMe(userID: String, visualizationId: String)

    /**
     * Overwrites the sharedWithUsers and sharedWithTeams lists of a visualization.
     * Callers must pass the complete desired lists.
     */
    suspend fun updateSharedUsers(
        visualizationId: String,
        userIds: List<String>,
        teamIds: List<String>
    )
}
