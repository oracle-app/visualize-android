package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.repositories.VisualizationRepository
import jakarta.inject.Inject
import java.util.Date


class VisualizationRepositoryImpl @Inject constructor(
    private val source: VisualizationDataSource
) : VisualizationRepository {
    // Mock data: Soon to be changed with Firebase DB connection.
    private val _visualizations = mutableListOf<Visualization>(
        Visualization(
            "1",
            "Felipe Bastidas",
            "GOTY (Graph Of The Year)",
            emptyMap(),
            listOf("Sebastián Garrido", "Eduardo Cardenas"),
            emptyList(),
            Date(System.currentTimeMillis() - 30 * 60 * 1000),
            listOf(
                Comment(
                    "1",
                    "Felipe Bastidas",
                    "This is a comment",
                    Date(System.currentTimeMillis() - 30 * 60 * 1000),
                    "",
                    emptyList()
                ),
                Comment(
                    "2",
                    "Eduardo Cardenas",
                    "This is a comment",
                    Date(System.currentTimeMillis() - 30 * 60 * 1000),
                    "",
                    emptyList()
                )
            )
        ),
        Visualization(
            "2",
            "Eduardo Cardenas",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Jorge Ruiz"),
            emptyList(),
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            emptyList()
        ),
        Visualization(
            "3",
            "Jorge Ruiz",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Felipe Bastidas"),
            emptyList(),
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            emptyList()
        ),
        Visualization(
            "4",
            "Sebastián Garrido",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Felipe Bastidas"),
            emptyList(),
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            emptyList()
        )
    )

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: Map<String, Any>,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        var id = 0
        for (v in _visualizations){
            if (v.id.toInt() > id) { id = v.id.toInt() }
        }
        _visualizations.add(
            Visualization(
                id.toString(),
                authorID,
                title,
                configJSON,
                sharedWithUsers,
                sharedWithTeams,
                Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
                emptyList()
            )
        )
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return _visualizations
    }

    override suspend fun getPersonalVisualizations(userID: String): List<Visualization> {
        return _visualizations.filter { it.authorID == userID }
    }

    override suspend fun getSharedVisualizationsByUser(userID: String): List<Visualization> {
        return _visualizations.filter {
            it.authorID == userID || it.sharedWithUsers.contains(userID)
        }
    }
}