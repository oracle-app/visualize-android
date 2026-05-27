package com.oracle.visualize.domain.repositories

import android.net.Uri
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread
import kotlinx.coroutines.tasks.await
import java.util.UUID

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


    suspend fun uploadSnip(
        userID: String,
        uri: Uri
    ): String

    suspend fun createThread(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): Thread

    suspend fun deleteComment(
        visualizationId: String,
        commentId: String
    )

    suspend fun deleteThread(
        visualizationId: String,
        commentId: String,
        threadId: String
    )

}

