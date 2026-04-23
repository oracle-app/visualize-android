package com.oracle.visualize.presentation.screens.loginScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.LoginTextField
import com.oracle.visualize.ui.theme.DarkOrange
import com.oracle.visualize.ui.theme.GrayishBlue
import com.oracle.visualize.ui.theme.NotAsDarkGray
import com.oracle.visualize.ui.theme.NotAsLightGray
import com.oracle.visualize.ui.theme.PalePink
import com.oracle.visualize.ui.theme.Red
import com.oracle.visualize.ui.theme.StrongBlue
import com.oracle.visualize.ui.theme.VeryDarkBlue
import com.oracle.visualize.ui.theme.VeryLightGray


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
    ) {

    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val hasError = uiState.error != null

    LaunchedEffect(email, password) {
        if (hasError) viewModel.clearError()
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .background(GrayishBlue)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.77f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(VeryLightGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(160.dp))

            Text(
                text = stringResource(id = R.string.login_screen_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = VeryDarkBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 36.dp)
            )

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.login_screen_input_email),
                hasError = hasError
            )

            Spacer(Modifier.height(20.dp))

            // Input Password
            LoginTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(id = R.string.login_screen_input_password),
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                hasError = hasError
            )

            AnimatedVisibility(
                visible = hasError,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = viewModel.mapError(uiState.error),
                    color = Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PalePink)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            Spacer(Modifier.height(52.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrongBlue,
                    disabledContainerColor = StrongBlue.copy(alpha = 0.55f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.login_screen_button_login),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.login_screen_text_signup),
                    fontSize = 13.sp,
                    color = NotAsLightGray
                )
                TextButton(
                    onClick = { navController.navigate("register") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login_screen_button_signup),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkOrange
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.app_version),
                fontSize = 11.sp,
                color = NotAsDarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}