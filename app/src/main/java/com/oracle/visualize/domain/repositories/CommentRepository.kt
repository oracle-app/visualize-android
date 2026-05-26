package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread

interface CommentRepository {
    suspend fun createComment(
        visualizationId: String,
        authorID: String,
        content: String,
        imageURL: String?
    ): Comment

    suspend fun getComments(
        visualizationId: String
    ): List<Comment>

    suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): List<Thread>

    suspend fun createThread(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): Thread
}

