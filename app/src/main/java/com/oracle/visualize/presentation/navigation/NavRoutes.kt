package com.oracle.visualize.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Defines the navigation graph destinations using type-safe objects.
 * Each destination exposes a stable [route] string for use with the
 * string-based composable API while retaining type-safety.
 */
@Serializable
sealed interface NavRoutes {

    val route: String

    @Serializable
    sealed interface MainTab : NavRoutes

    @Serializable
    object Feed : MainTab {
        override val route = "feed"
    }

    @Serializable
    object Create : MainTab {
        override val route = "create"
    }

    @Serializable
    object Notifications : MainTab {
        override val route = "notifications"
    }

    @Serializable
    object Teams : MainTab {
        override val route = "teams"
    }

    @Serializable
    data class Profile(val userId: String) : MainTab {
        override val route get() = "profile/$userId"

        companion object {
            const val ROUTE_PATTERN = "profile/{userId}"
        }
    }

    @Serializable
    data class FullScreen(val visualizationId: String) : NavRoutes {
        override val route get() = "full_screen/$visualizationId"

        companion object {
            const val ROUTE_PATTERN = "full_screen/{visualizationId}"
        }
    }
}
