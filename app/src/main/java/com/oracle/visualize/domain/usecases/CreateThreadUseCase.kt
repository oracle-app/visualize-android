package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.CommentRepository
import jakarta.inject.Inject

/**
 * Use case for creating a thread in a visualization.
 *
 * Validates the thread content before calling the repository.
 *
 * @property commentsRepository The repository used for comment operations.
 */
class CreateThreadUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
) {

    suspend operator fun invoke(
        visualizationId: String,
        commentId: String,
        authorID: String,
        authorName: String,
        authorAvatarURL: String?,
        content: String
    ): Result<Unit> {
        if (content.isBlank()) {
            return Result.failure(AppError.InvalidComment())
        }

        return runCatching {
            commentsRepository.createThread(
                visualizationId = visualizationId,
                commentId = commentId,
                authorID = authorID,
                authorName = authorName,
                authorAvatarURL = authorAvatarURL,
                content = content.trim()
            )
        }
    }
}
