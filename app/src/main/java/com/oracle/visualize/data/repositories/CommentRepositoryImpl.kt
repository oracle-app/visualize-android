package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.CommentDatasource
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.core.utils.safeApiCall
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
    ): AppResult<Comment> {
        return safeApiCall {
            val commentDTO = CommentDTO(
                authorID = authorID,
                content = content,
                imageURL = imageURL
            )

            val id = commentDatasource.createComment(
                visualizationId = visualizationId,
                commentDTO = commentDTO
            )

            commentDTO
                .copy(id = id)
                .toDomain()
        }

    }

    override suspend fun getComments(
        visualizationId: String
    ): AppResult<List<Comment>> {
        return safeApiCall {
            commentDatasource
                .getComments(visualizationId)
                .map { it.toDomain() }
        }
    }

    override suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): AppResult<List<Thread>> {
        return safeApiCall {
            commentDatasource
                .getThreads(
                    visualizationId = visualizationId,
                    commentId = commentId
                )
                .map { it.toDomain() }
        }
    }


    override suspend fun uploadSnip(userID: String, uri: String): AppResult<String> {
        return safeApiCall {
            commentDatasource.uploadSnip(userID, uri)
        }
    }

    override suspend fun createThread(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): AppResult<Thread> {
        return safeApiCall {
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

            threadDTO
                .copy(id = id)
                .toDomain()
        }

    }

    override suspend fun deleteComment(
        visualizationId: String,
        commentId: String
    ): AppResult<Unit> {
        return safeApiCall {
            commentDatasource.deleteComment(
                visualizationId = visualizationId,
                commentId = commentId
            )
        }
    }

    override suspend fun deleteThread(
        visualizationId: String,
        commentId: String,
        threadId: String
    ): AppResult<Unit> {
        return safeApiCall {
            commentDatasource.deleteThread(
                visualizationId = visualizationId,
                commentId = commentId,
                threadId = threadId
            )
        }
    }
}
