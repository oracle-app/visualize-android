package com.oracle.visualize.domain.usecases

import android.net.Uri
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.CommentRepository
import javax.inject.Inject

class UploadSnipUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(userID: String, uri: Uri): Result<String> {
        if (uri == Uri.EMPTY) {
            return Result.failure(AppError.NotFound())
        }
        return runCatching {
            commentRepository.uploadSnip(userID, uri)
        }
    }
}
