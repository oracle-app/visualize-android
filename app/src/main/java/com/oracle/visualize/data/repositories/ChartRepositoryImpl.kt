package com.oracle.visualize.data.repositories

import android.net.Uri
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.repositories.ChartRepository
import javax.inject.Inject

class ChartRepositoryImpl @Inject constructor(
    private val chartDataSource: ChartDataSource
) : ChartRepository {
    override suspend fun getCharts(file: Uri?): List<Chart> {
        return chartDataSource.getCharts(Uri)
    }
}