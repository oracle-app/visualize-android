package com.oracle.visualize.domain.usecases.chart

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.ui.theme.ChartPalette
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Use case to get a user's Chart Theme value from the database.
 *
 * @returns Result<ChartPalette> A Result that only indicates if the operation succeeded or failed.
 * @property userRepository The repository to change the selected chart theme.
 */
@Singleton
class GetUserChartThemeUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userID: String): AppResult<ChartPalette> {
        if (userID.isBlank()) return AppResult.Error(AppError.GeneralValidationError("User ID empty"))
        return userRepository.getChartTheme(userID)
    }
}
