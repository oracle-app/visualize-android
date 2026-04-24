package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import kotlinx.serialization.json.JsonObject
import java.util.Date

interface VisualizationRepository {
    suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    )
    suspend fun getAllVisualizations(): List<Visualization>
    suspend fun getAllVisualizationsByUserID(userID: String, filter: VisualizationFilter): List<VisualizationCard>
    suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>)
}