package com.oracle.visualize.data.repositories

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.UserDatasource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.datasources.dtos.VisualizationDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.data.mapper.toVisualizationCard
import com.oracle.visualize.data.mapper.toVisualizationDTO
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.VisualizationRepository
import javax.inject.Inject

class VisualizationRepositoryImpl @Inject constructor(
    private val visualizationDataSource: VisualizationDataSource,
    private val userDatasource: UserDatasource
) : VisualizationRepository {

    override suspend fun createVisualization(
        authorID: String,
        title: String,
        configJSON: String,
        sharedWithUsers: List<String>,
        sharedWithTeams: List<String>
    ) {
        val visualization = VisualizationDTO(
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
        val user = userDatasource.getUserByID(userID)
        val hiddenVisualizations = user.hiddenVisualizations

        for (dto in dtos) {
            val author = userDatasource.getUserByID(dto.authorID)
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

    override suspend fun publishVisualizationsInBulk(visualizations: List<Visualization>) {
        val visualizationsDTO = visualizations.map { it.toVisualizationDTO() }
        visualizationDataSource.publishVisualizationsInBulk(visualizationsDTO)
    }
}