package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
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
        } catch (ex: AppError.NetworkError) {
            Result.failure(AppError.NetworkError("Could not get the mock chart"))
        } catch (ex: AppError.UnavailableMockData) {
            Result.failure(AppError.UnavailableMockData("Mock data for ${chartType.typeName} chart is not supported"))
        }
    }
}
