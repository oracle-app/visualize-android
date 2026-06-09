package com.oracle.visualize.domain.usecases.chart

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationCard
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseSingleChartUseCase @Inject constructor(
    private val chartCacheManager: ChartCacheManager
) {
    suspend operator fun invoke(card: VisualizationCard): AppResult<Chart<*>> =
        withContext(Dispatchers.Default) {
            try {
                chartCacheManager.getChart(card.id)?.let { return@withContext AppResult.Success(it) }
                val parsed = ChartMapper.fromPreviewJson(card.previewJSON)
                if (parsed != null) {
                    chartCacheManager.saveChart(card.id, parsed)
                    AppResult.Success(parsed)
                } else {
                    Log.e("ParseSingleChartUseCase", "Failed to parse chart for card ID: ${card.id}")
                    AppResult.Error(AppError.ParsingError("Parsed chart is null for card ${card.id}"))
                }
            } catch (e: Exception) {
                Log.e("ParseSingleChartUseCase", "Exception parsing chart for card ID: ${card.id}", e)
                AppResult.Error(AppError.ParsingError(e.message ?: "JSON parsing failed for card ${card.id}"))
            }
        }
}
