package com.oracle.visualize.presentation.screens.resetPasswordScreen

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.loginScreen.components.LoginTextField
import com.oracle.visualize.presentation.screens.registrationScreen.components.CodeInputGroup

/**
 * ResetPasswordView: Dumb View for requesting a password reset or setting a new one.
 * It manages the multi-step flow (Email, Verification, New Password) based on the ViewModel state.
 * Fully adapted for Dark Mode and Visualize Brand Identity.
 *
 * @param viewModel ViewModel that manages the reset password process.
 * @param onNavigateBack Callback to navigate back to the previous screen.
 * @param onResetSuccess Callback to navigate after successful reset.
 */
@Composable
fun ResetPasswordView(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is ResetPasswordUiState.Success) {
            onResetSuccess()
        }
    }

    if (uiState is ResetPasswordUiState.Content) {
        val state = uiState as ResetPasswordUiState.Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
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
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = stringResource(R.string.reset_password_title),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        Text(
                            text = when (state.currentStep) {
                                ResetPasswordStep.EMAIL -> stringResource(R.string.reset_password_email_description)
                                ResetPasswordStep.VERIFICATION -> stringResource(R.string.verification_description)
                                ResetPasswordStep.NEW_PASSWORD -> stringResource(R.string.reset_password_new_password_description)
                            },
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        ResetPasswordContent(
                            state = state,
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        ResetPasswordButton(
                            state = state,
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.registration_version),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResetPasswordContent(
    state: ResetPasswordUiState.Content,
    viewModel: ResetPasswordViewModel
) {
    when (state.currentStep) {
        ResetPasswordStep.EMAIL -> {
            LoginTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(R.string.reset_password_email_label),
                error = state.emailError?.let { stringResource(it) },
                keyboardType = KeyboardType.Email
            )
        }
        ResetPasswordStep.VERIFICATION -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CodeInputGroup(
                    code = state.code,
                    onCodeChange = viewModel::onCodeChange,
                    isError = state.codeError != null
                )
                
                if (state.codeError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(state.codeError),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        ResetPasswordStep.NEW_PASSWORD -> {
            Column {
                LoginTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = stringResource(R.string.reset_password_password_label),
                    error = state.passwordError?.let { stringResource(it) },
                    isPassword = true,
                    isPasswordVisible = state.isPasswordVisible,
                    onToggleVisibility = viewModel::togglePasswordVisibility
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = stringResource(R.string.reset_password_confirm_password_label),
                    error = state.confirmPasswordError?.let { stringResource(it) },
                    isPassword = true,
                    isPasswordVisible = state.isConfirmPasswordVisible,
                    onToggleVisibility = viewModel::toggleConfirmPasswordVisibility
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.reset_password_password_helper),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordButton(
    state: ResetPasswordUiState.Content,
    viewModel: ResetPasswordViewModel
) {
    Button(
        onClick = {
            when (state.currentStep) {
                ResetPasswordStep.EMAIL -> viewModel.sendResetLink()
                ResetPasswordStep.VERIFICATION -> viewModel.verifyCode()
                ResetPasswordStep.NEW_PASSWORD -> viewModel.confirmNewPassword()
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = if (state.currentStep == ResetPasswordStep.NEW_PASSWORD)
                    stringResource(R.string.reset_password_confirm_button)
                else
                    stringResource(R.string.reset_password_send_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
