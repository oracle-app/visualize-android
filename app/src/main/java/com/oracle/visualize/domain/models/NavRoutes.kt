package com.oracle.visualize.domain.models

sealed class NavRoutes(val route: String) {
    object Feed : NavRoutes("feed")
    object Notifications : NavRoutes("notifications")
    object Profile : NavRoutes("profile")
    object Teams : NavRoutes("teams")
    object Create : NavRoutes("create")
    object Share : NavRoutes("share")
}