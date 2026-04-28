package com.oracle.visualize.data.datasources.dtos

import com.oracle.visualize.domain.models.enums.ChartTypes

data class ChartDTO<T>(
    val chartTitle: String = "",
    val chartType: String = "",
    val data: Any? = null,
    val fieldNames: Map<String, String> = emptyMap()
)