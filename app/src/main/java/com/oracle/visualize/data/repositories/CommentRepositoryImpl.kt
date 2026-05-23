package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.CommentDatasource
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDatasource: CommentDatasource
) : CommentRepository {

    override suspend fun createComment(
        visualizationId: String,
        authorId: String,
        authorName: String,
        authorImageUrl: String?,
        content: String,
        imageUrl: String?
    ) {
        commentDatasource.createComment(
            visualizationId = visualizationId,
            commentDTO = CommentDTO(
                authorId = authorId,
                authorName = authorName,
                authorImageUrl = authorImageUrl,
                content = content,
                imageUrl = imageUrl
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
}
