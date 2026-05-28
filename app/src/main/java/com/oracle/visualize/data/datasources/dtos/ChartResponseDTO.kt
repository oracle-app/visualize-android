package com.oracle.visualize.data.datasources.dtos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.oracle.visualize.data.mapper.ChartMapper
import com.oracle.visualize.domain.models.Chart

data class ChartResponseDTO(
    val chartIndex: String = "",
    val chartName: String = "",
    val chartType: String = "",
    val data: JsonObject? = null,
    val metrics: JsonObject? = null,
    val page: Int = 0,
    val preview: Boolean = true, //It's safer to unpack a preview instead of a full page in feed.
    val status: String = "",
    val totalPages: Int = 0,
    val totalPoints: Int = 0

)

fun ChartResponseDTO.toDomain(): Chart<*>? {
    if (status.isNotEmpty() && status != "COMPLETED") {
        val msg = if (status == "FAILED") {
            "Analysis failed. Please upload a valid dataset."
        } else {
            "Analysis is still in progress (status: $status). Please try again in a moment."
        }
        throw com.oracle.visualize.domain.exceptions.AppError.NotFound(msg)
    }
    val jsonString = Gson().toJson(this)
    return ChartMapper.fromPreviewJson(jsonString)
}
