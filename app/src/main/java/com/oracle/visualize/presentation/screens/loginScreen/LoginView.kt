package com.oracle.visualize.presentation.screens.loginScreen

import androidx.compose.runtime.Composable

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController




/**
 * Composable representing the Login screen.
 *
 * @param navController NavController for navigating between screens.
 * @param viewModel The [LoginViewModel] that manages the login state.
 */
@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
    ) {

}