package com.oracle.visualize.domain.repositories

import android.net.Uri
import com.oracle.visualize.domain.models.Dataset

interface DatasetRepository {
    fun getDatasetInfo(uri: Uri): Dataset?
}
