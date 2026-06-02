package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.CommentRepository
import jakarta.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
){
    suspend operator fun invoke(
        visualizationId: String,
        commentId: String
    ): Result<Unit> {
        if (visualizationId.isBlank()){
            return Result.failure(AppError.NotFound())
        }
        if (commentId.isBlank()){
            return Result.failure(AppError.NotFound())
        }
        return runCatching {
            commentsRepository.deleteComment(
                visualizationId = visualizationId,
                commentId = commentId
            )
        }
    }
}
