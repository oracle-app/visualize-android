package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Visualization
import java.util.Date

interface VisualizationRepository {
    suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: Map<String, Any>,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    )
    suspend fun getAllVisualizations(): List<Visualization>
    suspend fun getPersonalVisualizations(userID: String): List<Visualization>
    suspend fun getSharedVisualizationsByUser(userID: String): List<Visualization>
}