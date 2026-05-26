package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
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
    ): Result<Comment> {

        if (content.isBlank()) {
            return Result.failure(AppError.InvalidComment())
        }

        return runCatching {
            commentsRepository.createComment(
                visualizationId = visualizationId,
                authorID = authorID,
                content = content.trim(),
                imageURL = imageURL
            )
        }
    }
}
