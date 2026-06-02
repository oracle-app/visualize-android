package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.CommentRepository
import jakarta.inject.Inject

class DeleteThreadUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
) {
    suspend operator fun invoke(
        visualizationId: String,
        commentId: String,
        threadId: String
    ): AppResult<Unit> {
        if (visualizationId.isBlank()){
            return AppResult.Error(AppError.NotFound())
        }
        if (commentId.isBlank()){
            return AppResult.Error(AppError.NotFound())
        }
        if (threadId.isBlank()){
            return AppResult.Error(AppError.NotFound())
        }
        return commentsRepository.deleteThread(
                visualizationId = visualizationId,
                commentId = commentId,
                threadId = threadId
        )
    }
}
