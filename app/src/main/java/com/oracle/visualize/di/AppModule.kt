package com.oracle.visualize.di

import android.content.Context
import com.oracle.visualize.data.repositories.DatasetRepositoryImpl
import com.oracle.visualize.domain.repositories.DatasetRepository
import com.oracle.visualize.domain.usecases.GetDatasetInfoUseCase
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase

/**
 * Basic dependency injection container.
 */
class AppModule(private val context: Context) {
    
    private val datasetRepository: DatasetRepository by lazy {
        DatasetRepositoryImpl(context)
    }
    
    val getDatasetInfoUseCase: GetDatasetInfoUseCase by lazy {
        GetDatasetInfoUseCase(datasetRepository)
    }
    
    val validateDatasetUseCase: ValidateDatasetUseCase by lazy {
        ValidateDatasetUseCase()
    }
}
