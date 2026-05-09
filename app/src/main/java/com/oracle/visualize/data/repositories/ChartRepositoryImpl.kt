package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.ChartDataSource
import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.repositories.ChartRepository
import javax.inject.Inject


/**
 * Implementation of [ChartRepository] that brings chart mock data.
 *
 * @property chartDataSource Data source for chart operations.
 */
class ChartRepositoryImpl @Inject constructor(
    private val chartDataSource: ChartDataSource
) : ChartRepository {
    override suspend fun getMockChart(chartType: ChartTypes): Chart<*> {
        return chartDataSource.getMockChart(chartType.typeName).toDomain()
    }
}
