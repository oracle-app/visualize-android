package com.oracle.visualize.domain.models

/**
 * Defines all navigation routes used across the application.
 * Auth routes handle the authentication flow; app routes handle post-login screens.
 */
sealed class NavRoutes(val route: String) {

    // Auth flow routes
    object Splash : NavRoutes("splash")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ResetPassword : NavRoutes("resetPassword")
    object CheckEmail : NavRoutes("checkEmail/{email}") {
        /** Builds the route with the given email argument. */
        fun createRoute(email: String): String = "checkEmail/$email"
    }
    object NewPassword : NavRoutes("newPassword")

    // Main app routes
    object Feed : NavRoutes("feed")
    object Notifications : NavRoutes("notifications")
    object Profile : NavRoutes("profile")
    object Teams : NavRoutes("teams")
    object Create : NavRoutes("create")
    object Home : NavRoutes("home")
}