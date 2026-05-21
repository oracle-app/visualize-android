package com.oracle.visualize.data.datasources.dtos

import kotlinx.serialization.json.JsonObject

data class OverviewResultsResponseDTO(
    val charts: List<JsonObject> = emptyList()
)
