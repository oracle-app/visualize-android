package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.models.Comment
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

/**
 * Use case for loading comments associated with a visualization.
 *
 * @property commentRepository Repository used for comment operations.
 */
class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        visualizationId: String
    ): Result<List<Comment>> {
        if (visualizationId.isBlank()) {
            return Result.success(emptyList())
        }
        return runCatching {
            commentRepository.getComments(visualizationId)
        }
    }
}
