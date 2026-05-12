package com.oracle.visualize.presentation.screens.registrationScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.loginScreen.components.LoginTextField

/**
 * RegistrationView: Dumb View for user account creation.
 * Follows MVVM and architectural spacing standards.
 * Fully adapted for Dark Mode and Visualize Brand Identity.
 * 
 * @param viewModel ViewModel handling registration logic.
 * @param onNavigateToLogin Callback to go back to login.
 * @param onRegistrationSuccess Callback to proceed to verification or next screen.
 */
@Composable
fun RegistrationView(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is RegistrationUiState.Success) {
            onRegistrationSuccess()
        }
    }

    if (uiState is RegistrationUiState.Content) {
        val state = uiState as RegistrationUiState.Content
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
                    RegistrationContent(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }
        }
    }
}

/**
 * Main content layout for the registration screen.
 */
@Composable
private fun RegistrationContent(
    state: RegistrationUiState.Content,
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = stringResource(R.string.registration_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(67.dp))

        LoginTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = stringResource(R.string.registration_name_label),
            error = state.nameError?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.registration_email_label),
            error = state.emailError?.let { stringResource(it) },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.registration_password_label),
            error = state.passwordError?.let { stringResource(it) },
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            onToggleVisibility = viewModel::togglePasswordVisibility
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = stringResource(R.string.registration_confirm_password_label),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(38.dp))

        Button(
            onClick = {
                viewModel.register()
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
                    text = stringResource(R.string.registration_signup_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.registration_already_have_account),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.registration_login_link),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToLogin() }
                    .padding(4.dp),
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.registration_version),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
