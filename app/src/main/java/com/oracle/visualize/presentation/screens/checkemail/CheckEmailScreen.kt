package com.oracle.visualize.presentation.screens.checkemail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AuthButton
import com.oracle.visualize.presentation.components.CodeInputRow
import com.oracle.visualize.presentation.components.ErrorBanner
import com.oracle.visualize.ui.theme.SuccessGreen

/**
 * Check Email screen matching the Figma design.
 * Shows a 4-digit OTP code entry, resend link, and verify button.
 * Displays a green checkmark confirmation on successful verification.
 */
@Composable
fun CheckEmailScreen(
    email: String,
    onVerified: () -> Unit,
    viewModel: CheckEmailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasError = uiState.error != null

    // Navigate on verification success
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) onVerified()
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
                text = stringResource(id = R.string.check_email_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Subtitle with email
            Text(
                text = stringResource(id = R.string.check_email_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // User email display
            Text(
                text = email,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Code input boxes
            CodeInputRow(
                code = uiState.code,
                onCodeChange = { viewModel.updateCode(it) }
            )

            // Error banner
            ErrorBanner(
                message = uiState.error ?: "",
                visible = hasError
            )

            Spacer(Modifier.height(16.dp))

            // Resend code link
            ResendCodeRow(
                onResend = { viewModel.resendCode() }
            )

            Spacer(Modifier.height(32.dp))

            // Verify / Confirm button
            if (uiState.isVerified) {
                VerifiedBadge()
                Spacer(Modifier.height(24.dp))
                AuthButton(
                    text = stringResource(id = R.string.check_email_button_confirm),
                    onClick = onVerified
                )
            } else {
                AuthButton(
                    text = stringResource(id = R.string.check_email_button_verify),
                    onClick = { viewModel.verifyCode() },
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

/**
 * Row with "Didn't receive the code?" text and an orange "Resend" link.
 */
@Composable
private fun ResendCodeRow(onResend: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.check_email_resend),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.width(4.dp))
        TextButton(
            onClick = onResend,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.check_email_resend_link),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Green circular checkmark badge shown after successful code verification.
 */
@Composable
private fun VerifiedBadge() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SuccessGreen),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
