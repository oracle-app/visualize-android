package com.oracle.visualize.presentation.navigation

import kotlinx.serialization.Serializable

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
    data class Threads(val visualizationId: String, val snipUri: String? = null) : NavRoutes
    @Serializable
    data class FullScreen(val visualizationId: String, val startInSnippingMode: Boolean = false) : NavRoutes
    @Serializable
    object Splash : NavRoutes
    @Serializable
    object Login : NavRoutes
    @Serializable
    object Signup : NavRoutes
    @Serializable
    data class ChartSelection(val taskId: String) : NavRoutes
    @Serializable
    object ShareAndPost : NavRoutes

    /**
     * Route for creating or editing a team.
     * @property teamId Null when creating a new team; non-null when editing an existing one.
     */
    @Serializable
    data class CreateEditTeam(val teamId: String? = null) : NavRoutes
}
