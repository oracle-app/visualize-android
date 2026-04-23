package com.oracle.visualize.presentation.screens.signupscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AuthButton
import com.oracle.visualize.presentation.components.AuthIllustration
import com.oracle.visualize.presentation.components.AuthTextField
import com.oracle.visualize.presentation.components.ErrorBanner

/**
 * Sign Up screen matching the Figma design.
 * Collects name, email, password, and confirm password fields.
 * Validates locally and shows field-level or general error banners.
 *
 * Observes [SignUpViewModel.uiState] for loading, error, and success states.
 */
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val hasError = uiState.error != null

    // Clear error when user edits any field
    LaunchedEffect(name, email, password, confirmPassword) {
        if (hasError) viewModel.clearError()
    }

    // Navigate on success
    LaunchedEffect(uiState.success) {
        if (uiState.success) onSignUpSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Teal header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.30f)
                .background(MaterialTheme.colorScheme.outline)
        )

        // Card body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            AuthIllustration()

            Spacer(Modifier.height(8.dp))

            // Title
            Text(
                text = stringResource(id = R.string.signup_screen_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp)
            )

            // Subtitle
            Text(
                text = stringResource(id = R.string.signup_screen_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Name field
            AuthTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(id = R.string.signup_screen_input_name),
                hasError = hasError
            )

            Spacer(Modifier.height(16.dp))

            // Email field
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.signup_screen_input_email),
                hasError = hasError
            )

            Spacer(Modifier.height(16.dp))

            // Password field
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(id = R.string.signup_screen_input_password),
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                hasError = hasError
            )

            Spacer(Modifier.height(16.dp))

            // Confirm password field
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = stringResource(id = R.string.signup_screen_input_confirm_password),
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                hasError = hasError
            )

            // Error banner
            ErrorBanner(
                message = uiState.error ?: "",
                visible = hasError
            )

            Spacer(Modifier.height(32.dp))

            // Sign up button
            AuthButton(
                text = stringResource(id = R.string.signup_screen_button_signup),
                onClick = {
                    viewModel.signUp(
                        name = name,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                },
                isLoading = uiState.isLoading
            )

            Spacer(Modifier.height(24.dp))

            // Login link row
            LoginLinkRow(onNavigateToLogin = onNavigateToLogin)

            Spacer(Modifier.height(32.dp))
        }
    }
}

/**
 * Row showing "Already have an account?" with an orange "Log in" button.
 */
@Composable
private fun LoginLinkRow(onNavigateToLogin: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.signup_screen_text_login),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(
            onClick = onNavigateToLogin,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.signup_screen_button_login),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
