package com.oracle.visualize.data.datasources
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO
import com.oracle.visualize.data.datasources.dtos.OnTaskCreatingResponseDTO
import com.oracle.visualize.data.datasources.dtos.OverviewResultsResponseDTO
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File

interface AnalyzeApiMicroService {
    //Upload a file, get a taskID
    @Multipart
    @POST("analyzeData")
    suspend fun analyzeData(@Part file: MultipartBody.Part): OnTaskCreatingResponseDTO
    //Get the charts once they are generated
    @GET("/results/{taskId}")
    suspend fun overviewResults(
        @Path("taskId") taskId: String
    ): OverviewResultsResponseDTO
    //Get the charts one page at a time (designed for fullscreen)
    @GET("/results/{taskId}")
    suspend fun pagedResults(
        @Path("taskId") taskId: String,
        @Query("chart") chart: Int,
        @Query("page") page: Int
    ): ChartResponseDTO
    //Get a preview of the chart (designed for feed)
    @GET("/results/{taskId}")
    suspend fun previewedResults(
        @Path("taskId") taskId: String,
        @Query("chart") chart: Int,
        @Query("preview") preview: Boolean
    ): ChartResponseDTO
}

