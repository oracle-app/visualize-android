package com.oracle.visualize.presentation.screens.login

import androidx.compose.runtime.Composable

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController




@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
    ) {

    // ScreenBackground 0xFFF5F4F2 -> Background
    // TealPrimary = 0xFF34797C -> Login button
    // TextDark = 0xFF13212C -> Welcome Text
    // TextGray = 0xFF323232 ->  show password & hide password
    // Green = 0xFFE6EDEC -> Input Text Field
    // TextGrayLight = 0xFF597271 -> Don't have an account?
    // TextOrange = 0xFFE18212 -> Sign Up
    // TextVersion = 0xFF798B8A -> Version
    // ErrorAuthInput = 0xFFFFDAD6 -> Error Text Field
    // ErrorAuthText = 0xFFBA1A1A  -> Error Text (Inputs)
    // TextHint = 0xFF49454F -> Hint Text



}