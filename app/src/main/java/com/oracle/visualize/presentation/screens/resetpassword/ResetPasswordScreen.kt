package com.oracle.visualize.presentation.screens.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.oracle.visualize.presentation.components.AuthTextField
import com.oracle.visualize.presentation.components.ErrorBanner

/**
 * Reset Password screen matching the Figma design.
 * Shows a single email field and a "Send" button.
 * On success, navigates to the Check Email screen.
 */
@Composable
fun ResetPasswordScreen(
    onNavigateToCheckEmail: (String) -> Unit,
    viewModel: ResetPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    val hasError = uiState.error != null

    // Clear error on edit
    LaunchedEffect(email) {
        if (hasError) viewModel.clearError()
    }

    // Navigate on success
    LaunchedEffect(uiState.emailSent) {
        if (uiState.emailSent) onNavigateToCheckEmail(email)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
                .background(MaterialTheme.colorScheme.outline)
        )

        // Card body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
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
            Spacer(Modifier.height(80.dp))

            // Title
            Text(
                text = stringResource(id = R.string.reset_password_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Subtitle
            Text(
                text = stringResource(id = R.string.reset_password_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email field
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.reset_password_input_email),
                hasError = hasError
            )

            // Error banner
            ErrorBanner(
                message = uiState.error ?: "",
                visible = hasError
            )

            Spacer(Modifier.height(40.dp))

            // Send button
            AuthButton(
                text = stringResource(id = R.string.reset_password_button_send),
                onClick = { viewModel.sendResetEmail(email) },
                isLoading = uiState.isLoading
            )
        }
    }
}
