package com.oracle.visualize.data.repositories

import android.net.Uri
import com.oracle.visualize.data.datasources.CommentDatasource
import com.oracle.visualize.data.datasources.dtos.CommentDTO
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
    ) {
        commentDatasource.createComment(
            visualizationId = visualizationId,
            commentDTO = CommentDTO(
                authorID = authorID,
                content = content,
                imageURL = imageURL
            )
        )
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

    override suspend fun uploadSnip(userID: String, uri: Uri): String {
        return commentDatasource.uploadSnip(userID, uri)
    }
}
