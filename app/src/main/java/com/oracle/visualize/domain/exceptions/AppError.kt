package com.oracle.visualize.domain.exceptions

sealed class AppError(message: String) : Exception(message) {

    enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

    class AuthValidationError(
        val field: AuthField,
        message: String
    ): AppError(message)


    class GeneralValidationError(message: String): AppError(message)
    class AuthFailed(message: String = "Login or Registration failed") : AppError(message)
    class EmailAlreadyExists(
        message: String = "This email is already registered.") : AppError(
        message)
    class NotFound(message: String = "The requested resource was not found") : AppError(message)
    class ParsingError(message: String = "Failed to parse database object") : AppError(message)
    class NetworkError(message: String = "Network connection failed") : AppError(message)
    class UnavailableMockData(message: String) : AppError(message)
    class InvalidCredentials(message: String = "The email or password is incorrect") : AppError(message)

    class InvalidComment(message: String = "The comment is empty") : AppError(message)
    class UnknownError(message: String): AppError(message)
}
