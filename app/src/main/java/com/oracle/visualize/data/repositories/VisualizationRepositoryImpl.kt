package com.oracle.visualize.data.repositories

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class VisualizationRepositoryImpl @Inject constructor(
    private val source: VisualizationDataSource
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        val visualization = Visualization(
            id = "",
            authorID = authorID,
            title = title,
            configJSON = configJSON,
            sharedWithUsers = sharedWithUsers,
            sharedWithTeams = sharedWithTeams,
            createdAt = Timestamp.now(),
            comments = emptyList()
        )
        source.createVisualization(visualization)
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return source.getAllVisualizations().map { it.toDomain() }
    }

    override suspend fun getAllVisualizationsByUserID(userID: String): List<Visualization> {
        return source.getAllVisualizationsByUserID(userID).map { it.toDomain() }
    }

    override suspend fun getPersonalVisualizations(userID: String): List<Visualization> {
        return source.getPersonalVisualizations(userID).map { it.toDomain() }
    }

    override suspend fun getVisualizationsSharedWithUser(userID: String): List<Visualization> {
        return source.getVisualizationsSharedWithUser(userID).map { it.toDomain() }
    }

    override suspend fun getSharedVisualizationsByTeamsIntegratedByUser(userID: String): List<Visualization> {
        return source.getSharedVisualizationsByTeamsIntegratedByUser(userID).map { it.toDomain() }
    }
}