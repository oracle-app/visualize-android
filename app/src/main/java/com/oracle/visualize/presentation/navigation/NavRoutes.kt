package com.oracle.visualize.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Defines the navigation graph destinations using type-safe serializable objects only.
 * No string-based routes — every destination is a @Serializable class or object.
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
    data class FullScreen(val visualizationId: String) : NavRoutes
    @Serializable
    data class Threads(val visualizationId: String) : NavRoutes
    @Serializable
    data class ShareWithTeammates(val visualizationId: String) : NavRoutes
    @Serializable
    data class CreateEditTeam(val teamId: String? = null) : NavRoutes
    @Serializable
    data class ChartSelection(val taskId: String) : NavRoutes

    @Serializable
    object Splash : NavRoutes
    @Serializable
    object Login : NavRoutes
    @Serializable
    object Signup : NavRoutes
    @Serializable
    object ShareAndPost : NavRoutes
}
