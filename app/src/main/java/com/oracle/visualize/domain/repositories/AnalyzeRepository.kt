package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Chart
import java.io.File

interface AnalyzeRepository {
    suspend fun analyzeData(file: File): AppResult<String>
    suspend fun overviewResults(taskId: String): AppResult<List<Chart<*>>>
    suspend fun pagedResults(taskId: String, chart: Int, page: Int): AppResult<Chart<*>?>
    suspend fun previewedResults(taskId: String, chart: Int, preview: Boolean): AppResult<Chart<*>?>
    suspend fun getPagedResultsDto(taskId: String, chart: Int, page: Int): AppResult<com.oracle.visualize.data.datasources.dtos.ChartResponseDTO>

}
