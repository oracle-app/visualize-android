package com.oracle.visualize.domain.usecases.chart

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseSingleChartUseCase @Inject constructor(
    private val chartCacheManager: ChartCacheManager
) {
    suspend operator fun invoke(card: VisualizationCard): Chart<*>? =
        withContext(Dispatchers.Default) {
            chartCacheManager.getChart(card.id)?.let { return@withContext it }
            val parsed = ChartMapper.fromPreviewJson(card.previewJSON)
            parsed?.also { chartCacheManager.saveChart(card.id, it) }

            return@withContext parsed
        }
}
