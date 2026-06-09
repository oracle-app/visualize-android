package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

/**
 * Use case for creating a comment in a visualization.
 *
 * Validates the comment content before calling the repository.
 *
 * @property commentsRepository The repository used for comment operations.
 */
class CreateCommentUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
) {
    suspend operator fun invoke(
        visualizationId: String,
        authorID: String,
        content: String,
        imageURL: String? = null
    ): AppResult<Comment> {

        if (content.isBlank() && imageURL.isNullOrBlank()) {
            return AppResult.Error(AppError.InvalidComment())
        }

        return commentsRepository.createComment(
                visualizationId = visualizationId,
                authorID = authorID,
                content = content.trim(),
                imageURL = imageURL
        )
    }
}
