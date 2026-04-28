package com.oracle.visualize.domain.models

import kotlinx.serialization.Serializable

/**
 * Defines the navigation graph destinations using type-safe objects.
 * This replaces string-based route matching with class-based matching.
 */
sealed class NavRoutes {
    @Serializable object Feed : NavRoutes()
    @Serializable object Notifications : NavRoutes()
    @Serializable object Teams : NavRoutes()
    @Serializable object Create : NavRoutes()
    @Serializable object ChartSelection : NavRoutes()
    @Serializable object Share : NavRoutes()

    // Example of a typed argument:
    @Serializable data class Profile(val userId: String? = null) : NavRoutes()
}