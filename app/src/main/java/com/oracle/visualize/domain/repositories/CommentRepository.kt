package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Comment

interface CommentRepository {
    suspend fun createComment(
        visualizationId: String,
        authorId: String,
        authorName: String,
        authorImageUrl: String?,
        content: String,
        imageUrl: String?
    )

    suspend fun getComments(
        visualizationId: String
    ): List<Comment>
}

