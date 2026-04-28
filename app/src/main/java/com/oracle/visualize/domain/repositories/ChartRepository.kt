package com.oracle.visualize.domain.repositories

import android.net.Uri
import com.oracle.visualize.domain.models.Chart
import retrofit2.http.POST

interface ChartRepository {
    @POST("/analyzeData")
    suspend fun getChartOptionsFromFile(file: Uri?): Result<List<Chart<*>>>
}