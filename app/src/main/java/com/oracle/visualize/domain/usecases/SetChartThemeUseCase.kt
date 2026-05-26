package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.ui.theme.ChartPalette
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to update a user's Chart Theme value within the database.
 *
 * @returns Result<Unit> A Result that only indicates if the operation succeeded or failed.
 * @property userRepository The repository to change the selected chart theme.
 */
@Singleton
class SetChartThemeUseCase @Inject constructor(
    private val userRepository: UserRepository,
){
    suspend operator fun invoke(userID: String, selectedPalette: String): Result<Unit> {
        if (selectedPalette.isEmpty()) {
            return Result.failure(AppError.GeneralValidationError("Theme cannot be empty."))
        }

        val isValid = ChartPalette.entries.any { it.name == selectedPalette }

        if (!isValid) {
            return Result.failure(AppError.GeneralValidationError("Selected palette name does not match any of the existing options."))
        }

        if (userID == "") {
            return Result.failure(AppError.AuthFailed("User ID was not found."))
        }

        return runCatching {
            userRepository.setChartTheme(userID, selectedPalette)
        }
    }
}
