package com.oracle.visualize.presentation.screens.shareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareUser

private val CardShape   = RoundedCornerShape(12.dp)


@Composable
fun SelectedUserRow(
    user: ShareUser,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(68.dp)
            .clip(CardShape)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 12.dp)
                .requiredWidth(288.dp)
                .requiredHeight(44.dp)
        ) {
            UserAvatar(
                user,
                size = 40
            )
            Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 56.dp, y = 0.dp)
                    .requiredWidth(232.dp)
                    .requiredHeight(44.dp)
            ) {
                Text(
                    text = user.username,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 16.sp,
                    lineHeight = 1.5.em,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = user.email,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 14.sp,
                    lineHeight = 1.43.em,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 339.dp, y = 20.dp)
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.requiredSize(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.icon_remove_user),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.requiredSize(16.dp)
                )
            }
        }
    }
}
