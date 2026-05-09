package com.oracle.visualize.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Defines the navigation graph destinations using type-safe objects.
 * This replaces string-based route matching with class-based matching.
 */
@Serializable
sealed interface NavRoutes {

    @Serializable
    sealed interface MainTab : NavRoutes

    @Serializable
    object Feed : MainTab
    @Serializable
    object Create : MainTab
    @Serializable
    object Notifications : MainTab
    @Serializable
    object Teams : MainTab
    @Serializable
    data class Profile(val userId: String) : MainTab

    @Serializable
    data class SnippingTool(val imageUri: String) : NavRoutes
    @Serializable
    data class FullScreen(val visualizationId: String) : NavRoutes
}