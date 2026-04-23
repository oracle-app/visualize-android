package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
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
    suspend fun getAllVisualizationsByUserID(userID: String): List<Visualization>
    suspend fun getPersonalVisualizations(userID: String): List<Visualization>
    suspend fun getVisualizationsSharedWithUser(userID: String): List<Visualization>
    suspend fun getSharedVisualizationsByTeamsIntegratedByUser(userID: String): List<Visualization>
    suspend fun publishVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>?,
        sharedWithTeams: List<String>?,
        isPersonal: Boolean
    )
}