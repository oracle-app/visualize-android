package com.oracle.visualize.domain.usecases.chart

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationFullScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseFullScreenChartUseCase @Inject constructor(
    private val chartCacheManager: ChartCacheManager
) {
    suspend operator fun invoke(visualization: VisualizationFullScreen): Chart<*>? =
        withContext(Dispatchers.Default) {
            chartCacheManager.getChart(visualization.id)?.let { return@withContext it }
            val parsed = ChartMapper.fromPreviewJson(visualization.configJSON)
            parsed?.also { chartCacheManager.saveChart(visualization.id, it) }

            return@withContext parsed
        }
}
