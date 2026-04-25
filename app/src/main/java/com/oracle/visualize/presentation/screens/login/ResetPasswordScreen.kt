package com.oracle.visualize.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.login.components.LoginTextField
import com.oracle.visualize.ui.theme.*

/**
 * ResetPasswordScreen: Dumb View for requesting a password reset or setting a new one.
 * It manages the multi-step flow (Email, Verification, New Password) based on the ViewModel state.
 *
 * @param viewModel ViewModel that manages the reset password process.
 * @param onNavigateBack Callback to navigate back to the previous screen.
 */
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SURFACE_WHITE)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                color = SURFACE_WHITE,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = BLACK
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = stringResource(R.string.reset_password_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = BLACK
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = when (uiState.currentStep) {
                            ResetPasswordStep.EMAIL -> stringResource(R.string.reset_password_email_description)
                            ResetPasswordStep.VERIFICATION -> stringResource(R.string.verification_description)
                            ResetPasswordStep.NEW_PASSWORD -> stringResource(R.string.reset_password_new_password_description)
                        },
                        fontSize = 16.sp,
                        color = BLACK,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    ResetPasswordContent(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ResetPasswordButton(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.registration_version),
                        color = BLACK,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable that displays the input fields based on the current step.
 * 
 * @param uiState Current state of the screen.
 * @param viewModel ViewModel to handle input changes.
 */
@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    viewModel: ResetPasswordViewModel
) {
    when (uiState.currentStep) {
        ResetPasswordStep.EMAIL -> {
            LoginTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(R.string.reset_password_email_label),
                error = uiState.emailError?.let { stringResource(it) },
                keyboardType = KeyboardType.Email
            )
        }
        ResetPasswordStep.VERIFICATION -> {
            LoginTextField(
                value = uiState.code,
                onValueChange = viewModel::onCodeChange,
                label = "Verification Code",
                error = uiState.codeError?.let { stringResource(it) },
                keyboardType = KeyboardType.Number
            )
        }
        ResetPasswordStep.NEW_PASSWORD -> {
            Column {
                LoginTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = stringResource(R.string.reset_password_password_label),
                    error = uiState.passwordError?.let { stringResource(it) },
                    isPassword = true,
                    isPasswordVisible = uiState.isPasswordVisible,
                    onToggleVisibility = viewModel::togglePasswordVisibility
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = stringResource(R.string.reset_password_confirm_password_label),
                    error = uiState.confirmPasswordError?.let { stringResource(it) },
                    isPassword = true,
                    isPasswordVisible = uiState.isConfirmPasswordVisible,
                    onToggleVisibility = viewModel::toggleConfirmPasswordVisibility
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.reset_password_password_helper),
                    color = BLACK,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

/**
 * Composable that displays the action button for the current step.
 * 
 * @param uiState Current state of the screen.
 * @param viewModel ViewModel to handle the button click action.
 */
@Composable
private fun ResetPasswordButton(
    uiState: ResetPasswordUiState,
    viewModel: ResetPasswordViewModel
) {
    Button(
        onClick = {
            when (uiState.currentStep) {
                ResetPasswordStep.EMAIL -> viewModel.sendResetLink()
                ResetPasswordStep.VERIFICATION -> viewModel.verifyCode()
                ResetPasswordStep.NEW_PASSWORD -> viewModel.confirmNewPassword()
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TEAL_700),
        shape = RoundedCornerShape(32.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = WHITE,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = if (uiState.currentStep == ResetPasswordStep.NEW_PASSWORD)
                    stringResource(R.string.reset_password_confirm_button)
                else
                    stringResource(R.string.reset_password_send_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
