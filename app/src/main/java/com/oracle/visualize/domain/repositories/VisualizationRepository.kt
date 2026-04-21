package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Visualization
import java.util.Date

class VisualizationRepository {
    private val _visualizations = mutableListOf<Visualization>(
        Visualization(
            "1",
            "Felipe Bastidas",
            "GOTY (Graph Of The Year)",
            emptyMap(),
            listOf("Sebastián Garrido", "Eduardo Cardenas"),
            emptyList(),
            2,
            true,
            Date(System.currentTimeMillis() - 30 * 60 * 1000)
        ),
        Visualization(
            "2",
            "Eduardo Cardenas",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Jorge Ruiz"),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        ),
        Visualization(
            "3",
            "Jorge Ruiz",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Felipe Bastidas"),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        ),
        Visualization(
            "4",
            "Sebastián Garrido",
            "Relative performance of major currencies",
            emptyMap(),
            listOf("Felipe Bastidas"),
            emptyList(),
            7,
            true,
            Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        )
    )

    fun createVisualization(
        _ownerId: String,
        _title: String,
        _configJson: Map<String, Any>,
        _sharedWith: List<String>,
        _sharedWithGroup: List<String>
    ) {
        var _id = 0
        for (v in _visualizations){
            if (v.id.toInt() > _id) { _id = v.id.toInt() }
        }
        _visualizations.add(
            Visualization(
                id = _id.toString(),
                ownerId = _ownerId,
                title = _title,
                configJson = _configJson,
                sharedWith = _sharedWith,
                sharedWithGroup = _sharedWithGroup,
                commentCount = 0,
                hasNewActivity = false,
                createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000),
            )
        )
    }

    fun getAllVisualizations(): List<Visualization> {
        return _visualizations
    }

    fun getPersonalVisualizations(userId: String): List<Visualization> {
        return _visualizations.filter { it.ownerId == userId }
    }

    fun getGeneralVisualizationsByUser(userId: String): List<Visualization> {
        return _visualizations.filter { it.ownerId == userId
                || it.sharedWith.contains(userId)}
    }
}