package com.oracle.visualize.presentation.screens.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AuthButton
import com.oracle.visualize.presentation.components.AuthOutlinedButton

/**
 * Splash / Welcome screen shown on app launch.
 * Displays the Visualize logo, tagline, and two CTA buttons
 * to navigate to Login or Sign Up.
 *
 * This screen is stateless and does not require a ViewModel.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        // Brand logo illustration
        Image(
            painter = painterResource(id = R.drawable.ic_visualize_logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(24.dp))

        // App name
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Tagline
        Text(
            text = stringResource(id = R.string.splash_tagline),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(Modifier.weight(1f))

        // Primary CTA — Log in
        AuthButton(
            text = stringResource(id = R.string.splash_login_button),
            onClick = onNavigateToLogin
        )

        Spacer(Modifier.height(16.dp))

        // Secondary CTA — Sign up
        AuthOutlinedButton(
            text = stringResource(id = R.string.splash_signup_button),
            onClick = onNavigateToSignUp
        )

        Spacer(Modifier.height(48.dp))
    }
}
