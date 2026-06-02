package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppResult
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
    ): AppResult<List<Comment>> {
        if (visualizationId.isBlank()) {
            return AppResult.Success(emptyList())
        }
        return commentRepository.getComments(visualizationId)
    }
}
