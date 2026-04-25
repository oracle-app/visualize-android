package com.oracle.visualize.presentation.screens.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.login.components.LoginTextField
import com.oracle.visualize.ui.theme.*

/**
 * RegistrationScreen: Dumb View for user account creation.
 * Follows MVVM and architectural spacing standards.
 * 
 * @param viewModel ViewModel handling registration logic.
 * @param onNavigateToLogin Callback to go back to login.
 * @param onNavigateToVerification Callback to proceed to verification.
 */
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToVerification: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(GREY_50)) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            Surface(
                modifier = Modifier.fillMaxSize().weight(1f),
                color = GREY_50,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            ) {
                RegistrationContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToVerification = onNavigateToVerification
                )
            }
        }
    }
}

/**
 * Main content layout for the registration screen.
 */
@Composable
private fun RegistrationContent(
    uiState: RegistrationUiState,
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerification: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 70dp spacing from top of surface as per architectural guide
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = stringResource(R.string.registration_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BLUE_GREY_900,
            textAlign = TextAlign.Center
        )

        // 67dp spacing from title to first input
        Spacer(modifier = Modifier.height(67.dp))

        LoginTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = stringResource(R.string.registration_name_label),
            error = uiState.nameError?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.registration_email_label),
            error = uiState.emailError?.let { stringResource(it) },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.registration_password_label),
            error = uiState.passwordError?.let { stringResource(it) },
            isPassword = true,
            isPasswordVisible = uiState.isPasswordVisible,
            onToggleVisibility = viewModel::togglePasswordVisibility
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = stringResource(R.string.registration_confirm_password_label),
            error = uiState.confirmPasswordError?.let { stringResource(it) },
            isPassword = true,
            isPasswordVisible = uiState.isConfirmPasswordVisible,
            onToggleVisibility = viewModel::toggleConfirmPasswordVisibility
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.reset_password_password_helper),
            color = BLUE_GREY_400,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(38.dp))

        Button(
            onClick = {
                if (viewModel.validateInputs()) {
                    onNavigateToVerification()
                }
            },
            modifier = Modifier.fillMaxWidth(0.65f).height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DEEP_TEAL_900),
            shape = RoundedCornerShape(32.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = WHITE, modifier = Modifier.size(24.dp))
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
                color = BLUE_GREY_400,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.registration_login_link),
                color = CORAL_500,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }.padding(4.dp),
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.registration_version),
            color = BLUE_GREY_400,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
