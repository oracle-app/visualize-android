package com.oracle.visualize.domain.models

import java.util.Date

/**
 * Domain model representing a Thread
 */
data class Thread (
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorImageUrl: String? = null,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: Date
)
