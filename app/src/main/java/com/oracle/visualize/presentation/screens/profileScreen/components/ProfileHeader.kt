package com.oracle.visualize.presentation.screens.profileScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.Avatar

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    userName: String,
    email: String,
    profileImageUrl: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Avatar(
                modifier = Modifier.size(180.dp),
                userName = userName,
                profileImageUrl = profileImageUrl
            )

            OutlinedIconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = (-6).dp, y = (-6).dp),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_image_description)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
