package com.oracle.visualize.domain.models

/**
 * Domain model representing a Comment.
 */
data class Comment (
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorImageUrl: String? = null,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: String
)