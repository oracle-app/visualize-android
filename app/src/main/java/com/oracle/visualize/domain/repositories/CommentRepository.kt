package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread

interface CommentRepository {
    suspend fun createComment(
        visualizationId: String,
        authorID: String,
        content: String,
        imageURL: String?
    ): AppResult<Comment>

    suspend fun getComments(
        visualizationId: String
    ): AppResult<List<Comment>>

    suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): AppResult<List<Thread>>


    suspend fun uploadSnip(
        userID: String,
        uri: String
    ): AppResult<String>

    suspend fun createThread(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): AppResult<Thread>

    suspend fun deleteComment(
        visualizationId: String,
        commentId: String
    ): AppResult<Unit>

    suspend fun deleteThread(
        visualizationId: String,
        commentId: String,
        threadId: String
    ): AppResult<Unit>

}

