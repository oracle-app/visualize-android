package com.oracle.visualize.domain.models

sealed class NavRoutes(val route: String) {
    object Feed          : NavRoutes("feed")
    object Notifications : NavRoutes("notifications")
    object Profile : NavRoutes("profile")
    object Teams : NavRoutes("teams")
    object Create : NavRoutes("create")
    object ChartSelection : NavRoutes("chart_selection")
    object Share : NavRoutes("share")
    object Registration : NavRoutes("registration")
    object Login : NavRoutes("login")
    object Verification : NavRoutes("verification")
    object ForgotPassword : NavRoutes("forgot_password")
    object NewPassword : NavRoutes("new_password")
}
