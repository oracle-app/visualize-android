package com.oracle.visualize.domain.models

import kotlinx.serialization.Serializable

/**
 * Defines the navigation graph destinations using type-safe objects.
 * This replaces string-based route matching with class-based matching.
 */
// En tu archivo de rutas (NavRoutes.kt)
sealed interface NavRoutes {
    sealed interface MainTab : NavRoutes

    @Serializable object Feed : MainTab
    @Serializable object Create : MainTab
    @Serializable object Notifications : MainTab
    @Serializable object Teams : MainTab
    @Serializable data class Profile(val userId: String) : MainTab

}