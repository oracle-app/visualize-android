package com.oracle.visualize.core.utils

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import java.io.IOException

/**
 * Global wrapper for asynchronous calls.
 * Catches any exception (Firebase, Retrofit, Coroutines) and safely
 * converts it into an [com.oracle.visualize.domain.exceptions.AppResult.Error] that can be handled by the Domain layer.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: AppError) {
        AppResult.Error(e)
    } catch (e: TimeoutCancellationException) {
        AppResult.Error(AppError.NetworkError("The request timed out. Please check your connection and try again."))
    } catch (e: IOException) {
        AppResult.Error(AppError.NetworkError("Network connection failed. Please check your internet."))
    } catch (e: HttpException) {
        val error = when (e.code()) {
            404 -> AppError.NotFound("Analysis results not found. They might still be processing.")
            401, 403 -> AppError.AuthFailed("Unauthorized access to the microservice.")
            else -> AppError.NetworkError("The server returned an error: ${e.code()}")
        }
        AppResult.Error(error)
    } catch (e: Exception) {
        AppResult.Error(AppError.NetworkError(e.message ?: "An unexpected error occurred."))
    }
}
