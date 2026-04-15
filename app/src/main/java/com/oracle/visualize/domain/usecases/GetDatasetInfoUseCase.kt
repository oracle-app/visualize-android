package com.oracle.visualize.domain.usecases

import android.net.Uri
import com.oracle.visualize.domain.models.Dataset
import com.oracle.visualize.domain.repositories.DatasetRepository

/**
 * Use case to retrieve dataset information from a given Uri.
 */
class GetDatasetInfoUseCase(private val repository: DatasetRepository) {
    operator fun invoke(uri: Uri): Dataset? {
        return repository.getDatasetInfo(uri)
    }
}
