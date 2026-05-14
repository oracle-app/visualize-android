package com.oracle.visualize.data.datasources.dtos

import com.google.gson.annotations.SerializedName

data class OnTaskCreatingResponseDTO(
    val message: String = "",
    val status: String = "",
    @SerializedName("task_id")
    val taskId: String = ""
)
