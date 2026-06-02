package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Thread
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

/**
 * Use case for loading threads associated with a comment.
 *
 * @property commentRepository Repository used for comment and thread operations.
 */
class GetThreadsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        visualizationId: String,
        commentId: String
    ): AppResult<List<Thread>> {
        if (visualizationId.isBlank() || commentId.isBlank()) {
            return AppResult.Success(emptyList())
        }

        return commentRepository.getThreads(
                visualizationId = visualizationId,
                commentId = commentId
        )
    }
}
