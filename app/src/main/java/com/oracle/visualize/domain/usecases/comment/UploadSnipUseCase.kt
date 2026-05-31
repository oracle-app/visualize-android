package com.oracle.visualize.domain.usecases.comment

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

class UploadSnipUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(userID: String, uri: String): Result<String> {
        if (uri == "") {
            return Result.failure(AppError.NotFound())
        }
        return runCatching {
            commentRepository.uploadSnip(userID, uri)
        }
    }
}
