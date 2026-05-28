package com.oracle.visualize.presentation.screens.profileScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.profileScreen.components.ProfileHeader
import com.oracle.visualize.presentation.screens.profileScreen.components.ThemeItem
import com.oracle.visualize.ui.theme.ChartPalette

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

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

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.profilebgtransparent),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is ProfileUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileUiState.Ready -> {
                    Spacer(modifier = Modifier.height(98.dp))

                    ProfileHeader(
                        userName = userName,
                        email = email,
                        profileImage = painterResource(id = profileImage),
                        onEditClick = { /* implement select image later*/ }
                    )

                    Spacer(modifier = Modifier.height(if (isSystemInDarkTheme()) 16.dp else 32.dp))

                    SettingsCard(
                        title = stringResource(R.string.chart_theme_title),
                        icon = Icons.Default.Palette
                    ) {
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

                    Spacer(modifier = Modifier.height(if (isSystemInDarkTheme()) 24.dp else 4.dp))

                    SettingsCard(
                        title = stringResource(R.string.about_title),
                        icon = Icons.Default.Info
                    ) {
                        Text(
                            text = "${stringResource(R.string.version_declaration)} $appVersion\n${stringResource(R.string.developer)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.terms_of_service),
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.Underline),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.licenses),
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.Underline),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(64.dp))

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

@Composable
fun SettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (isSystemInDarkTheme()) {
        Column(modifier = modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            content()
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                content()
            }
        }
    }
}
