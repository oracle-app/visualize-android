package com.oracle.visualize.domain.models

/**
 * Domain model representing an authenticated user.
 */
data class AuthUser (
    val uid: String,
    val email: String
)