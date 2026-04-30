package com.oracle.visualize.presentation.screens.profileScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.ChartPalette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.components.AppDropdownMenu
import com.oracle.visualize.presentation.screens.profileScreen.components.ThemeItem
import com.oracle.visualize.presentation.screens.profileScreen.components.ProfileHeader
import com.oracle.visualize.presentation.screens.profileScreen.components.SettingsCard
import androidx.compose.runtime.setValue


// This is where everything is assembled.

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    // This is where the UI state is fetched.

    val context = LocalContext.current
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // This is where the page fetches the current app version.

    val unknown = stringResource(R.string.error_unknown)
    val appVersion = remember {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: unknown
        }

    val selectedPalette = profileViewModel.selectedPalette
    val userName = profileViewModel.userName
    val email = profileViewModel.email
    val profileImage = profileViewModel.profileImage

    // Background Image Setup

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.profilebgtransparent),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.FillBounds
        )

        // Page content starts here.

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            // Check view model, decide if to draw or not.

            when (val state = uiState) {
                is ProfileUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileUiState.Ready -> {

                    //Spacer at top of the page.

                    Spacer(modifier = Modifier.height(98.dp))

                    // Profile picture, username and email.

                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        ProfileHeader(
                            userName = userName,
                            email = email,
                            profileImageUrl = profileImage,

                            // Upon clicking, it displays the dropdown menu.

                            onEditClick = { expanded = true }
                        )

                        AppDropdownMenu(
                            expanded = expanded,
                            onDismiss = { expanded = false },
                            items = listOf(
                                stringResource(R.string.take_photo) to { /* launch camera */ },
                                stringResource(R.string.choose_photo) to { /* launch gallery */ },
                                stringResource(R.string.delete_photo) to { /* delete pfp */ }
                            )
                        )
                    }

                    // Spacer between profile header and cards.

                    Spacer(modifier = Modifier.height(32.dp))

                    // Chart theme selector.

                    SettingsCard(title = stringResource(R.string.chart_theme_title)) {

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                ThemeItem(
                                    palette = ChartPalette.THEME1,
                                    isSelected = selectedPalette == ChartPalette.THEME1,
                                    onClick = { profileViewModel.onPaletteChange(ChartPalette.THEME1) },
                                    modifier = Modifier.weight(1f)
                                )
                                ThemeItem(
                                    palette = ChartPalette.THEME2,
                                    isSelected = selectedPalette == ChartPalette.THEME2,
                                    onClick = { profileViewModel.onPaletteChange(ChartPalette.THEME2) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                ThemeItem(
                                    palette = ChartPalette.THEME3,
                                    isSelected = selectedPalette == ChartPalette.THEME3,
                                    onClick = { profileViewModel.onPaletteChange(ChartPalette.THEME3) },
                                    modifier = Modifier.weight(1f)
                                )
                                ThemeItem(
                                    palette = ChartPalette.THEME4,
                                    isSelected = selectedPalette == ChartPalette.THEME4,
                                    onClick = { profileViewModel.onPaletteChange(ChartPalette.THEME4) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // 4 dp spacer.

                    Spacer(modifier = Modifier.height(4.dp))

                    // "About app" card.

                    SettingsCard(title = stringResource(R.string.about_title)) {
                        Text(
                            text = "${stringResource(R.string.version_declaration)} $appVersion\n${stringResource(R.string.developer)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${stringResource(R.string.terms_of_service)}\n${stringResource(R.string.licenses)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    //Spacer between about and logout button.

                    Spacer(modifier = Modifier.height(64.dp))

                    // Logout button, logic not yet implemented

                    OutlinedButton(
                        onClick = { },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                            .height(64.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.log_out),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }
            }

        }
    }
}

