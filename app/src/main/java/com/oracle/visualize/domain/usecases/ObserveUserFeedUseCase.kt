package com.oracle.visualize.domain.usecases

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.repositories.VisualizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveUserFeedUseCase @Inject constructor(
    private val visualizationRepository: VisualizationRepository,
    private val chartCacheManager: ChartCacheManager
) {
    operator fun invoke(userID: String, forceRefresh: Boolean): Flow<AppResult<List<FeedItem>>> = flow {
        val result = visualizationRepository.getUserFeedVisualizations(userID, forceRefresh)

        when (result) {
            is AppResult.Success -> {
                val initialItems = result.data.map { card ->
                    val cached = chartCacheManager.getChart(card.id)
                    FeedItem(card = card, chart = cached, isChartLoading = cached == null)
                }
                emit(AppResult.Success(initialItems))
            }
            is AppResult.Error -> {
                emit(AppResult.Error(result.error))
            }
        }



    }
}
