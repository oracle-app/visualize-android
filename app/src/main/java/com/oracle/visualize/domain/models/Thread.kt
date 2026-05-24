package com.oracle.visualize.domain.models

import java.util.Date

/**
 * Domain model representing a Thread
 */
data class Thread(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val createdAt: Date = Date()
)
