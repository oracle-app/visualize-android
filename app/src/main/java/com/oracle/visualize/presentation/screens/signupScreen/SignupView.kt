package com.oracle.visualize.presentation.screens.signupScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AuthTextField
import com.oracle.visualize.ui.theme.Orange
import com.oracle.visualize.ui.theme.SloganGray

@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSignUpSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashbackground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.18f),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-18).dp)
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clip(
                    RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 36.dp)
                .padding(top = 46.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.create_account),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(58.dp))

            AuthTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                placeholder = stringResource(R.string.name),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.5f),
                isError = uiState.nameErrorRes != null
            )

            uiState.nameErrorRes ?.let {
                Text(
                    text = stringResource(id = it),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start= 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            AuthTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = stringResource(R.string.email),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.5f),
                isError = uiState.emailErrorRes != null
            )

            uiState.emailErrorRes ?.let {
                Text(
                    text = stringResource(id = it),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start= 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            AuthTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = stringResource(R.string.password),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.5f),
                isError = uiState.passwordErrorRes != null,
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onVisibilityClick = viewModel::onPasswordVisibilityChange
            )

            uiState.passwordErrorRes ?.let {
                Text(
                    text = stringResource(id = it),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start= 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            AuthTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = stringResource(R.string.confirm_password),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.5f),
                isError = uiState.confirmPasswordErrorRes != null,
                isPassword = true,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onVisibilityClick = viewModel::onConfirmPasswordVisibilityChange
            )

            uiState.confirmPasswordErrorRes ?.let {
                Text(
                    text = stringResource(id = it),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start= 8.dp, top = 4.dp)
                )
            }

            uiState.errorRes?.let { errorId ->
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = errorId),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Button(
                onClick = { viewModel.signUp() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .width(154.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.signup),
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.already_have_account),
                fontSize = 14.sp,
                color = SloganGray
            )

            TextButton(
                onClick = onLoginClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 14.sp,
                    color = Orange,
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
