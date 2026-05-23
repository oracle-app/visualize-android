package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
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
        authorId: String,
        authorName: String,
        authorImageUrl: String?,
        content: String,
        imageUrl: String? = null
    ): Result<Unit> {

        if (content.isBlank()) {
            return Result.failure(AppError.InvalidComment())
        }
        return runCatching {
            commentsRepository.createComment(
                visualizationId = visualizationId,
                authorId = authorId,
                authorName = authorName,
                authorImageUrl = authorImageUrl,
                content = content.trim(),
                imageUrl = imageUrl
            )
        }
    }
}
