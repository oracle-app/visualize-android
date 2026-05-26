package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.CommentRepository
import jakarta.inject.Inject

class DeleteThreadUseCase @Inject constructor(
    private val commentsRepository: CommentRepository
) {
    suspend operator fun invoke(
        visualizationId: String,
        commentId: String,
        threadId: String
    ): Result<Unit> {
        if (visualizationId.isBlank()){
            return Result.failure(AppError.NotFound())
        }
        if (commentId.isBlank()){
            return Result.failure(AppError.NotFound())
        }
        if (threadId.isBlank()){
            return Result.failure(AppError.NotFound())
        }
        return runCatching {
            commentsRepository.deleteThread(
                visualizationId = visualizationId,
                commentId = commentId,
                threadId = threadId
            )
        }
    }
}
