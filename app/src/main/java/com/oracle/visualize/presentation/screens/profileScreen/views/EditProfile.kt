package com.oracle.visualize.presentation.screens.profileScreen.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.AppDropdownMenu
import com.oracle.visualize.presentation.screens.profileScreen.components.ChartThemePicker
import com.oracle.visualize.presentation.screens.profileScreen.components.ProfileHeader
import com.oracle.visualize.presentation.screens.profileScreen.components.SettingsCard

@Composable
fun EditProfile(
    appversion: String,
    username: String,
    email: String,
    image: String,
    chartTheme: String,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
    onPaletteChange: (String) -> Unit,
    onLogoutClick: () -> Unit
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
        Spacer(modifier = Modifier.height(98.dp))

        var expanded by remember { mutableStateOf(false) }

        Box(contentAlignment = Alignment.Center) {
            ProfileHeader(
                userName = username,
                email = email,
                profileImageUrl = image,
                onEditClick = { expanded = true }
            )

            Box(contentAlignment = Alignment.Center) {
                AppDropdownMenu(
                    expanded = expanded,
                    onDismiss = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 64.dp),
                    items = listOf(
                        stringResource(R.string.take_photo) to { onTakePhoto() },
                        stringResource(R.string.choose_photo) to { onChoosePhoto() },
                        stringResource(R.string.delete_photo) to { onDeletePhoto() }
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ChartThemePicker(
            selectedPalette = chartTheme,
            onPaletteChange = { onPaletteChange(it) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        SettingsCard(title = stringResource(R.string.about_title)) {
            Text(
                text = "${stringResource(R.string.version_declaration)} $appversion\n${stringResource(R.string.developer)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${stringResource(R.string.terms_of_service)}\n${stringResource(R.string.licenses)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        OutlinedButton(
            onClick = { onLogoutClick() },
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
