package com.oracle.visualize.domain.models

import java.util.Date

/**
 * Domain model representing a Comment.
 */
data class Comment(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val createdAt: Date = Date(),
    val imageURL: String? = null,
    val threads: List<Thread> = emptyList()
)
