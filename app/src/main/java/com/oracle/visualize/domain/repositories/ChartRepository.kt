package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.enums.ChartTypes

/**
 * Interface defining the operations for chart management.
 */
interface ChartRepository {
    suspend fun getMockChart(chartType: ChartTypes): Chart<*>
}