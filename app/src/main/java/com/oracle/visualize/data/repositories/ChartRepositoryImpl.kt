package com.oracle.visualize.data.repositories

import android.net.Uri
import com.oracle.visualize.data.datasources.ChartDataSource
import com.oracle.visualize.data.datasources.dtos.ChartDTO
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.repositories.ChartRepository
import javax.inject.Inject

//class ChartRepositoryImpl @Inject constructor(
//    private val chartDataSource: ChartDataSource
//) : ChartRepository {
//    override suspend fun getChartsFromFile(file: Uri?): List<ChartDTO<*>> {
//        return chartDataSource.getChartsFromFile(Uri)
//    }
//}