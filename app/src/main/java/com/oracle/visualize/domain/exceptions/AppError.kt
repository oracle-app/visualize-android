package com.oracle.visualize.domain.exceptions

sealed class AppError(message: String) : Exception(message) {

    /**
     * Represents the specific UI Fields in the authentication flow that can fail validation.
     */
    enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

    /**
     *  Triggered when a specific field in the authentication process fails local validation.
     *  (e.g., an empty email or a password that is too short).
     *
     *  @property field the specific [AuthField] that caused the validation to fail.
     */
    class AuthValidationError(
        val field: AuthField,
        message: String
    ): AppError(message)

    /**
     * Triggered when a validation error occurs that is not tied a specific UI field,
     * or when it applies to a general business rule (e.g., empty lists, invalid files).
     */
    class GeneralValidationError(message: String): AppError(message)
    class AuthFailed(message: String = "Login or Registration failed") : AppError(message)
    class EmailAlreadyExists(
        message: String = "The email address you entered is already registered.") : AppError(
        message)
    class NotFound(message: String = "The requested resource was not found") : AppError(message)
    class ParsingError(message: String = "Failed to parse database object") : AppError(message)
    class NetworkError(message: String = "Network connection failed") : AppError(message)
    class UnavailableMockData(message: String) : AppError(message)
    class InvalidCredentials(message: String = "The email or password is incorrect") : AppError(message)

    class InvalidComment(message: String = "The comment is empty") : AppError(message)
}
