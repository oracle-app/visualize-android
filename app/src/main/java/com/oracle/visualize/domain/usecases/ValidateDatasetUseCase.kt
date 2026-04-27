package com.oracle.visualize.domain.usecases

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Use case to validate if a dataset file has a supported format (.csv or .xlsx).
 */
@Singleton
class ValidateDatasetUseCase @Inject constructor() {
    operator fun invoke(fileName: String, fileSizeBytes: Long): Result<Unit> {
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)
        val maxSizeBytes = 100 * 1024 * 1024 // 100 MB

        // 1. Validate extension
        if (extension != "csv" && extension != "xlsx") {
            return Result.failure(IllegalArgumentException("Please upload a .xlsx or .csv file to continue."))
        }

        // 2. Validate size
        if (fileSizeBytes > maxSizeBytes) {
            return Result.failure(IllegalArgumentException("Please upload a smaller dataset (Max 100 MB)."))
        }

        return Result.success(Unit)
    }
}
