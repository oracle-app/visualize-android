package com.oracle.visualize.domain.usecases.chart

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationFullScreen
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseFullScreenChartUseCase @Inject constructor(
    private val chartCacheManager: ChartCacheManager
) {
    suspend operator fun invoke(visualization: VisualizationFullScreen): AppResult<Chart<*>> =
        withContext(Dispatchers.Default) {
            try {
                chartCacheManager.getChart(visualization.id)?.let { return@withContext AppResult.Success(it) }
                val parsed = ChartMapper.fromPreviewJson(visualization.configJSON)
                if (parsed != null) {
                    chartCacheManager.saveChart(visualization.id, parsed)
                    AppResult.Success(parsed)
                } else {
                    Log.e("ParseFullScreenChartUseCase", "Failed to parse chart for visualization ID: ${visualization.id}")
                    AppResult.Error(AppError.ParsingError("Parsed chart is null for visualization ${visualization.id}"))
                }
            } catch (e: Exception) {
                Log.e("ParseFullScreenChartUseCase", "Exception parsing chart for visualization ID: ${visualization.id}", e)
                AppResult.Error(AppError.ParsingError(e.message ?: "JSON parsing failed for visualization ${visualization.id}"))
            }
        }
}
