package com.oracle.visualize.domain.usecases

import com.oracle.visualize.data.datasources.local.ChartCacheManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearChartCacheUseCase @Inject constructor(
    private val chartCacheManager: ChartCacheManager
) {
    operator fun invoke() = chartCacheManager.clearCache()
}
