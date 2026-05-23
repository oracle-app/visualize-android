package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread

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

    suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): List<Thread>
}

