package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject


class VisualizationRepositoryImpl @Inject constructor(
    private val source: VisualizationDataSource
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: Map<String, Any>,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        val visualization = Visualization(
            authorID = authorID,
            title = title,
            configJSON = configJSON,
            sharedWithUsers = sharedWithUsers,
            sharedWithTeams = sharedWithTeams
        )
        source.createVisualization(visualization)
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return source.getAllVisualizations().map { it.toDomain() }
    }

    override suspend fun getPersonalVisualizations(userID: String): List<Visualization> {
        return source.getPersonalVisualizations(userID).map { it.toDomain() }
    }

    override suspend fun getSharedVisualizationsByUser(userID: String): List<Visualization> {
        return source.getSharedVisualizationsByUser(userID).map { it.toDomain() }
    }
}