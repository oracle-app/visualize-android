package com.oracle.visualize.navigation

sealed class NavRoutes(val route: String) {
    object Feed : NavRoutes("feed")
    object Notifications : NavRoutes("notifications")
}