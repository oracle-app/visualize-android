package com.oracle.visualize.presentation.screens.verificationScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.registrationScreen.components.CodeInputGroup

/**
 * VerificationView: Dumb View for code verification.
 * Follows MVVM where the View only observes state and delegates events.
 * Fully adapted for Dark Mode and Visualize Brand Identity.
 * 
 * @param viewModel ViewModel handling the verification logic.
 * @param onNavigateBack Callback to return to the previous screen.
 * @param onVerificationSuccess Callback to proceed after verification.
 */
@Composable
fun VerificationView(
    viewModel: VerificationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is VerificationUiState.Success) {
            onVerificationSuccess()
        }
    }

    if (uiState is VerificationUiState.Content) {
        val state = uiState as VerificationUiState.Content
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
                        .fillMaxSize()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                ) {
                    VerificationContent(
                        state = state,
                        onNavigateBack = onNavigateBack,
                        onCodeChange = viewModel::onCodeChange,
                        onResendCode = viewModel::resendCode,
                        onVerify = viewModel::verify
                    )
                }
            }

            ResendWaitMessage(isVisible = state.showResendWaitMessage)
        }
    }
}

@Composable
private fun VerificationContent(
    state: VerificationUiState.Content,
    onNavigateBack: () -> Unit,
    onCodeChange: (String) -> Unit,
    onResendCode: () -> Unit,
    onVerify: () -> Unit
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
            text = stringResource(R.string.verification_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(R.string.verification_description),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        CodeInputGroup(
            code = state.code,
            onCodeChange = onCodeChange,
            isError = state.codeError != null
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(top = 8.dp)
        ) {
            state.codeError?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ResendCodeSection(
            timer = state.resendTimer,
            isEnabled = state.isResendEnabled,
            onResend = onResendCode
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerify,
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
                    text = stringResource(R.string.verification_verify_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.registration_version),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun ResendCodeSection(
    timer: Int,
    isEnabled: Boolean,
    onResend: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.verification_resend_prompt),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (timer > 0)
                stringResource(R.string.verification_countdown, timer)
            else
                stringResource(R.string.verification_resend_link),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = isEnabled) { onResend() },
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun ResendWaitMessage(isVisible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(visible = isVisible) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.verification_resend_wait),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
