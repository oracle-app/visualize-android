package com.oracle.visualize.data.datasources

import android.net.Uri
import com.oracle.visualize.data.datasources.dtos.ChartDTO
import retrofit2.http.POST
import javax.inject.Inject

class ChartDataSource @Inject constructor(
    private val API_URL: String = "http://localhost:8080",
){
    @POST("/analyzeData")
    suspend fun getChartsFromFile(file: Uri?): List<ChartDTO<*>> {
        TODO("Wait until the microservice is ready")
    }
}