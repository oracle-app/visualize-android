package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.repositories.ChartRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to a mock chart associated, filtered by type.
 *
 * @property chartRepository: The repository to fetch chart data from.
 */
@Singleton
class GetMockChartUseCase @Inject constructor(
    private val chartRepository: ChartRepository
) {
    // Return type Result<Chart<*>>
    suspend operator fun invoke(chartType: ChartTypes): Result<Chart<*>> {
        return try {
            Result.success(chartRepository.getMockChart(chartType))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}