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
    data class FullScreen(val visualizationId: String) : NavRoutes


    @Serializable
    data class Share(val visualizationId: String) : NavRoutes

    @Serializable
    data class ShareWithTeammates(val visualizationId: String) : NavRoutes
}
