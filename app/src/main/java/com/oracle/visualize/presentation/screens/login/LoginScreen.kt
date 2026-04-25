package com.oracle.visualize.presentation.screens.login

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
 * LoginScreen: Entry point for user authentication.
 * Follows the "Dumb View" pattern where the UI reacts to state from the ViewModel.
 * 
 * @param viewModel ViewModel that handles the business logic for login.
 * @param onNavigateToRegistration Callback to navigate to the registration screen.
 * @param onNavigateToResetPassword Callback to navigate to the reset password screen.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegistration: () -> Unit,
    onNavigateToResetPassword: () -> Unit
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
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 70dp spacing from top of surface to title as per architectural guide
                    Spacer(modifier = Modifier.height(70.dp))

                    Text(
                        text = stringResource(R.string.login_welcome_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = BLUE_GREY_900,
                        textAlign = TextAlign.Center
                    )

                    // 67dp spacing from title to input fields
                    Spacer(modifier = Modifier.height(67.dp))

                    LoginTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = stringResource(R.string.login_email_label),
                        error = uiState.emailError?.let { stringResource(it) },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.login_password_label),
                        error = uiState.passwordError?.let { stringResource(it) },
                        isPassword = true,
                        isPasswordVisible = uiState.isPasswordVisible,
                        onToggleVisibility = viewModel::togglePasswordVisibility
                    )

                    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            color = CORAL_500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable { onNavigateToResetPassword() }
                                .padding(4.dp),
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    // 95dp spacing from last input to action button
                    Spacer(modifier = Modifier.height(95.dp))

                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier.fillMaxWidth(0.65f).height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DEEP_TEAL_900),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = WHITE, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = stringResource(R.string.login_button),
                                fontSize = 20.sp,
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
                            text = stringResource(R.string.login_no_account_prompt),
                            color = BLUE_GREY_400,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.login_signup_link),
                            color = CORAL_500,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToRegistration() }.padding(4.dp),
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
        }
    }
}
