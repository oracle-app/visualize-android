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
    data class ShareAndPost(
        val taskId: String,
        val selectedChartIndices: List<Int>,
        val customTitles: List<String>,
    ) : NavRoutes
    @Serializable
    data class ShareWithTeammates(val visualizationId: String) : NavRoutes
    @Serializable
    data class CreateEditTeam(val teamId: String? = null) : NavRoutes
    @Serializable
    object ResetPassword : NavRoutes
    @Serializable
    data class SnipPreview(val visualizationId: String, val snipUri: String): NavRoutes
}
