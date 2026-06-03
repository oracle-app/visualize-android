package com.oracle.visualize.data.repositories

import com.oracle.visualize.core.utils.safeApiCall
import com.oracle.visualize.data.datasources.AnalyzeApiMicroService
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO
import com.oracle.visualize.data.datasources.dtos.toDomain
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class AnalyzeRepositoryImpl @Inject constructor(
    private val apiService: AnalyzeApiMicroService
) : AnalyzeRepository {

    override suspend fun analyzeData(file: File): AppResult<String> {
        return safeApiCall {
            val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            apiService.analyzeData(filePart).taskId
        }
    }

    override suspend fun overviewResults(taskId: String): AppResult<List<Chart<*>>> {
        return safeApiCall {
            val res = apiService.overviewResults(taskId)
            res.charts.mapNotNull { rawJson ->
                ChartMapper.fromPreviewJson(rawJson.toString())
            }
        }
    }

    override suspend fun pagedResults(taskId: String, chart: Int, page: Int): AppResult<Chart<*>?> {
        return safeApiCall {
            apiService.pagedResults(taskId, chart, page).toDomain()
        }
    }

    override suspend fun previewedResults(taskId: String, chart: Int, preview: Boolean): AppResult<Chart<*>?> {
        return safeApiCall {
            apiService.previewedResults(taskId, chart, preview).toDomain()
        }
    }

    override suspend fun getPagedResultsDto(taskId: String, chart: Int, page: Int): AppResult<ChartResponseDTO> {
        return safeApiCall {
            apiService.pagedResults(taskId, chart, page)
        }
    }
}
