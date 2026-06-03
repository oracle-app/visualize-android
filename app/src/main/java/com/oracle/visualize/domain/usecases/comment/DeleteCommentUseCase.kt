package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.CommentRepository
import jakarta.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
){
    suspend operator fun invoke(
        visualizationId: String,
        commentId: String
    ): AppResult<Unit> {
        if (visualizationId.isBlank()){
            return AppResult.Error(AppError.NotFound())
        }
        if (commentId.isBlank()){
            return AppResult.Error(AppError.NotFound())
        }
        return commentsRepository.deleteComment(
                visualizationId = visualizationId,
                commentId = commentId
        )
    }
}
