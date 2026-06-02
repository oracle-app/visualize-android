package com.oracle.visualize.domain.usecases.visualization

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to validate if a dataset file has a supported format (.csv or .xlsx)
 * and its size is within the allowed limits.
 */
@Singleton
class ValidateDatasetUseCase @Inject constructor() {
    operator fun invoke(fileName: String, fileSizeBytes: Long): AppResult<Unit> {
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)
        val maxSizeBytes = 100 * 1024 * 1024 // 100 MB

        // 1. Validate extension
        if (extension != "csv" && extension != "xlsx") {
            return AppResult.Error(
                AppError.GeneralValidationError(
                "Please upload a .xlsx or .csv file to continue.")
            )
        }

        // 2. Validate size
        if (fileSizeBytes > maxSizeBytes) {
            return AppResult.Error(AppError.GeneralValidationError("Please upload a smaller dataset (Max 100 MB)."))
        }

        return AppResult.Success(Unit)
    }
}
