package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import jakarta.inject.Inject
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import java.util.Date


class VisualizationRepositoryImpl @Inject constructor(
    private val source: VisualizationDataSource
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: JsonObject,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        val visualization = Visualization(
            "5",
            authorID,
            title,
            configJSON,
            sharedWithUsers,
            sharedWithTeams,
            Date(System.currentTimeMillis()),
            emptyList()
        )
        source.createVisualization(visualization)
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return source.getAllVisualizations()
    }

    override suspend fun getPersonalVisualizations(userID: String): List<Visualization> {
        return source.getPersonalVisualizations(userID)
    }

    override suspend fun getSharedVisualizationsByUser(userID: String): List<Visualization> {
        return source.getSharedVisualizationsByUser(userID)
    }
}