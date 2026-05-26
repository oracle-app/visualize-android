package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.CommentDatasource
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.models.Thread
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDatasource: CommentDatasource
) : CommentRepository {

    override suspend fun createComment(
        visualizationId: String,
        authorID: String,
        content: String,
        imageURL: String?
    ): Comment {
        val commentDTO = CommentDTO(
            authorID = authorID,
            content = content,
            imageURL = imageURL
        )

        val id = commentDatasource.createComment(
            visualizationId = visualizationId,
            commentDTO = commentDTO
        )

        return commentDTO
            .copy(id = id)
            .toDomain()
    }

    override suspend fun getComments(
        visualizationId: String
    ): List<Comment> {
        return commentDatasource
            .getComments(visualizationId)
            .map { it.toDomain() }
    }

    override suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): List<Thread> {
        return commentDatasource
            .getThreads(
                visualizationId = visualizationId,
                commentId = commentId
            )
            .map { it.toDomain() }
    }

    override suspend fun createThread(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): Thread {
        val threadDTO = ThreadDTO(
            authorID = authorID,
            authorName = authorName,
            authorAvatarURL = authorAvatarURL,
            content = content
        )

        val id = commentDatasource.createThread(
            visualizationId = visualizationId,
            commentId = commentId,
            threadDTO = threadDTO
        )

        return threadDTO
            .copy(id = id)
            .toDomain()
    }

    override suspend fun deleteComment(
        visualizationId: String,
        commentId: String
    ) {
        commentDatasource.deleteComment(
            visualizationId = visualizationId,
            commentId = commentId
        )
    }

    override suspend fun deleteThread(
        visualizationId: String,
        commentId: String,
        threadId: String
    ){
        commentDatasource.deleteThread(
            visualizationId = visualizationId,
            commentId = commentId,
            threadId = threadId
        )
    }
}
