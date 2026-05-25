package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.AnalyzeApiMicroService
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO
import com.oracle.visualize.data.datasources.dtos.toDomain
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AnalyzeRepositoryImpl @Inject constructor(
    private val apiService: AnalyzeApiMicroService
) : AnalyzeRepository {

    override suspend fun analyzeData(file: File): AppResult<String> {
        return try {
            val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = apiService.analyzeData(filePart)
            AppResult.Success(response.taskId)
        } catch (e: Exception) {
            AppResult.Error(mapError(e))
        }
    }

    override suspend fun overviewResults(taskId: String): AppResult<List<Chart<*>>> {
        return try {
            val res = apiService.overviewResults(taskId)
            val charts = res.charts.mapNotNull { rawJson ->
                ChartMapper.fromPreviewJson(rawJson.toString())
            }
            AppResult.Success(charts)
        } catch (e: Exception) {
            AppResult.Error(mapError(e))
        }
    }

    override suspend fun pagedResults(taskId: String, chart: Int, page: Int): AppResult<Chart<*>?> {
        return try {
            val res = apiService.pagedResults(taskId, chart, page)
            AppResult.Success(res.toDomain())
        } catch (e: Exception) {
            AppResult.Error(mapError(e))
        }
    }

    override suspend fun previewedResults(taskId: String, chart: Int, preview: Boolean): AppResult<Chart<*>?> {
        return try {
            val res = apiService.previewedResults(taskId, chart, preview)
            AppResult.Success(res.toDomain())
        } catch (e: Exception) {
            AppResult.Error(mapError(e))
        }
    }

    override suspend fun getPagedResultsDto(taskId: String, chart: Int, page: Int): AppResult<com.oracle.visualize.data.datasources.dtos.ChartResponseDTO> {
        return try {
            val res = apiService.pagedResults(taskId, chart, page)
            AppResult.Success(res)
        } catch (e: Exception) {
            AppResult.Error(mapError(e))
        }
    }

    private fun mapError(e: Exception): AppError {
        return when (e) {
            is IOException -> AppError.NetworkError("Network connection failed. Please check your internet.")
            is HttpException -> {
                when (e.code()) {
                    404 -> AppError.NotFound("Analysis results not found. It might still be processing.")
                    else -> AppError.NetworkError("Server returned an error: ${e.code()}")
                }
            }
            else -> AppError.NetworkError(e.message ?: "An unexpected error occurred")
        }
    }
}
