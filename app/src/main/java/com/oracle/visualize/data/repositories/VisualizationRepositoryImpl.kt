package com.oracle.visualize.data.repositories

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.UserDataSource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class VisualizationRepositoryImpl @Inject constructor(
    private val visualizationDataSource: VisualizationDataSource,
    private val userDataSource: UserDataSource
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
        )
        visualizationDataSource.createVisualization(visualization)
    }

    override suspend fun getAllVisualizations(): List<Visualization> {
        return visualizationDataSource.getAllVisualizations().map { it.toDomain() }
    }

    override suspend fun getAllVisualizationsByUserID(userID: String, filter: VisualizationFilter): List<VisualizationCard> {
        val dtos = mutableListOf<VisualizationDTO>()

        when(filter) {
            VisualizationFilter.ALL -> {
                dtos += visualizationDataSource.getAllVisualizationsByUserID(userID)
            }

            VisualizationFilter.SHARED -> {
                val visualizationsSharedWithUser = visualizationDataSource.getVisualizationsSharedWithUser(userID)
                val visualizationsSharedWithTeam = visualizationDataSource.getSharedVisualizationsByTeamsIntegratedByUser(userID)
                dtos += visualizationsSharedWithUser
                dtos += visualizationsSharedWithTeam
            }

            VisualizationFilter.PERSONAL -> {
                dtos += visualizationDataSource.getPersonalVisualizations(userID)
            }
        }

        val visualizationCards = mutableListOf<VisualizationCard>()
        val user = userDataSource.getUserByUserID(userID)
        val hiddenVisualizations = user.hiddenVisualizations

        for (dto in dtos) {
            val author = userDataSource.getUserByUserID(dto.authorID)
            val users = visualizationDataSource.getAllUsersVisualizationIsSharedWith(dto.id)
            val sharedUsers = users.map { it.toDomain() }
            val card = dto.toVisualizationCard(author.username,sharedUsers)
            visualizationCards += card
        }
        val filteredCards = visualizationCards.filter { card ->
            card.id !in hiddenVisualizations
        }
        return visualizationCards
    }
}