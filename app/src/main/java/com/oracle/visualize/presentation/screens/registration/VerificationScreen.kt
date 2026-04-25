package com.oracle.visualize.presentation.screens.registration

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
import androidx.compose.runtime.collectAsState
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
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.registration.components.CodeInputGroup
import com.oracle.visualize.ui.theme.*

/**
 * VerificationScreen: Dumb View for code verification.
 * Follows MVVM where the View only observes state and delegates events.
 * 
 * @param viewModel ViewModel handling the verification logic.
 * @param onNavigateBack Callback to return to the previous screen.
 */
@Composable
fun VerificationScreen(
    viewModel: VerificationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GREY_50)
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
                color = GREY_50,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            ) {
                VerificationContent(
                    uiState = uiState,
                    onNavigateBack = onNavigateBack,
                    onCodeChange = viewModel::onCodeChange,
                    onResendCode = viewModel::resendCode,
                    onVerify = viewModel::verify
                )
            }
        }

        ResendWaitMessage(isVisible = uiState.showResendWaitMessage)
    }
}

/**
 * Main content of the verification screen.
 */
@Composable
private fun VerificationContent(
    uiState: VerificationUiState,
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
                    tint = BLACK
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.verification_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BLUE_GREY_900
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(R.string.verification_description),
            fontSize = 16.sp,
            color = BLUE_GREY_400,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        CodeInputGroup(
            code = uiState.code,
            onCodeChange = onCodeChange,
            isError = uiState.codeError != null
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(top = 8.dp)
        ) {
            uiState.codeError?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = RED_900,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ResendCodeSection(
            timer = uiState.resendTimer,
            isEnabled = uiState.isResendEnabled,
            onResend = onResendCode
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerify,
            modifier = Modifier
                .fillMaxWidth(0.65f)
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
                    text = stringResource(R.string.verification_verify_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.registration_version),
            color = BLUE_GREY_400,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

/**
 * Section for resending the verification code.
 */
@Composable
private fun ResendCodeSection(
    timer: Int,
    isEnabled: Boolean,
    onResend: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.verification_resend_prompt),
            color = BLUE_GREY_400,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (timer > 0)
                stringResource(R.string.verification_countdown, timer)
            else
                stringResource(R.string.verification_resend_link),
            color = ORANGE_500,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = isEnabled) { onResend() },
            textDecoration = TextDecoration.Underline
        )
    }
}

/**
 * Floating message displayed when the user needs to wait to resend.
 */
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
                color = WHITE,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.verification_resend_wait),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = BLACK
                )
            }
        }
    }
}
