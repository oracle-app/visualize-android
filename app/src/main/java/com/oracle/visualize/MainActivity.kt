package com.oracle.visualize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oracle.visualize.domain.models.NavRoutes
import com.oracle.visualize.presentation.screens.checkemail.CheckEmailScreen
import com.oracle.visualize.presentation.screens.loginScreen.LoginScreen
import com.oracle.visualize.presentation.screens.mainScreen.MainScreen
import com.oracle.visualize.presentation.screens.newpassword.NewPasswordScreen
import com.oracle.visualize.presentation.screens.resetpassword.ResetPasswordScreen
import com.oracle.visualize.presentation.screens.signupscreen.SignUpScreen
import com.oracle.visualize.presentation.screens.splashscreen.SplashScreen
import com.oracle.visualize.ui.theme.VisualizeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the Visualize application.
 * Sets up a NavHost routing between the auth flow and the main app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisualizeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.Splash.route
                ) {
                    composable(NavRoutes.Splash.route) {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate(NavRoutes.Login.route)
                            },
                            onNavigateToSignUp = {
                                navController.navigate(NavRoutes.Register.route)
                            }
                        )
                    }
                    composable(NavRoutes.Login.route) {
                        LoginScreen(
                            onNavigateToHome = {
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = {
                                navController.navigate(NavRoutes.Register.route)
                            },
                            onNavigateToResetPassword = {
                                navController.navigate(NavRoutes.ResetPassword.route)
                            }
                        )
                    }
                    composable(NavRoutes.Register.route) {
                        SignUpScreen(
                            onNavigateToLogin = { navController.popBackStack() },
                            onSignUpSuccess = {
                                navController.navigate(NavRoutes.Login.route) {
                                    popUpTo(NavRoutes.Splash.route)
                                }
                            }
                        )
                    }
                    composable(NavRoutes.ResetPassword.route) {
                        ResetPasswordScreen(
                            onNavigateToCheckEmail = { email ->
                                navController.navigate(
                                    NavRoutes.CheckEmail.createRoute(email)
                                )
                            }
                        )
                    }
                    composable(
                        route = NavRoutes.CheckEmail.route,
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        CheckEmailScreen(
                            email = email,
                            onVerified = {
                                navController.navigate(NavRoutes.NewPassword.route)
                            }
                        )
                    }
                    composable(NavRoutes.NewPassword.route) {
                        NewPasswordScreen(
                            onPasswordReset = {
                                navController.navigate(NavRoutes.Login.route) {
                                    popUpTo(NavRoutes.Splash.route)
                                }
                            }
                        )
                    }
                    composable(NavRoutes.Home.route) {
                        MainScreen()
                    }
                }
            }
        }
    }
}
