package com.oracle.visualize.presentation.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.DarkMode_StrongBlue

@Composable
fun SplashPage(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
    onSessionActive: () -> Unit,
    onLoginClick: () -> Unit,
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

    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    LaunchedEffect(uiState.hasActiveSession) {
        if (uiState.hasActiveSession == true) {
            onSessionActive()
        }
    }

    if (uiState.isCheckingSession || uiState.hasActiveSession == true) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // AppLogo
                CircularProgressIndicator()

            }
        }
    } else {
        // Landing page
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
                    .fillMaxHeight(0.38f),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.visualizeicon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(350.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 22.dp,
                            topEnd = 22.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 36.dp)
                    .padding(top = 46.dp, bottom = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.visualize),
                    fontWeight = FontWeight.Bold,
                    fontSize = 52.sp,
                    letterSpacing = 3.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.slogan),
                    fontSize = 13.sp,
                    letterSpacing = 0.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(R.string.splash_description),
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSystemInDarkTheme()) 0.26f else 1f),
                        contentColor = if (isSystemInDarkTheme()) DarkMode_StrongBlue else MaterialTheme.colorScheme.primary
                    ),

                ) {
                    Text(
                        text = stringResource(R.string.signup),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
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
}
