package com.oracle.visualize.presentation.screens.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AuthTextField

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val unknown = stringResource(R.string.error_unknown)
    val appVersion = remember {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: unknown
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onLoginSuccess()
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
                .fillMaxHeight(0.84f)
                .clip(
                    RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 26.dp)
                .padding(top = 62.dp, bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome),
                fontSize = 32.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(58.dp))

            AuthTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = stringResource(R.string.email),
                isError = uiState.emailError != null
            )

            Spacer(modifier = Modifier.height(20.dp))

            AuthTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = stringResource(R.string.password),
                isError = uiState.passwordError != null,
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onVisibilityClick = viewModel::onPasswordVisibilityChange
            )

            uiState.error?.let {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(82.dp))

            Button(
                onClick = { viewModel.login() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .width(164.dp)
                    .height(52.dp),
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
                        text = stringResource(R.string.login),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.no_account),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(
                onClick = onSignUpClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.signup),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "V${appVersion}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
