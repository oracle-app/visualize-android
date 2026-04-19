package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Dataset
import java.util.Locale

/**
 * Use case to validate if a dataset file has a supported format (.csv or .xlsx).
 */
class ValidateDatasetUseCase {
    operator fun invoke(fileName: String): Result<Unit> {
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)
        return if (extension == "csv" || extension == "xlsx") {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Please upload a .xlsx or .csv file to continue."))
        }
    }
}
