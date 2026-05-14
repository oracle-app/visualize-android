package com.oracle.visualize.domain.exceptions

sealed class AppError(message: String) : Exception(message) {
    class ValidationError(message: String) : AppError(message)
    class AuthFailed(message: String = "Login or Registration failed") : AppError(message)
    class NotFound(message: String = "The requested resource was not found") : AppError(message)
    class ParsingError(message: String = "Failed to parse database object") : AppError(message)
    class NetworkError(message: String = "Network connection failed") : AppError(message)
    class UnavailableMockData(message: String) : AppError(message)
}
