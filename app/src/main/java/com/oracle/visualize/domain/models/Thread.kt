package com.oracle.visualize.domain.models

import java.util.Date

data class Thread (
    val id: String,
    val content: String,
    val createdAt: Date
)