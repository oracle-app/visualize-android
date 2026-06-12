package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.presentation.components.UserAvatar
@Composable
fun TeammateList(
    users: List<ShareUser>,
    onRemove: (ShareUser) -> Unit
) {
    if (users.isEmpty()) return

    val visible = remember { mutableStateListOf(*users.toTypedArray()) }
    LaunchedEffect(users) {
        visible.clear()
        visible.addAll(users)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        visible.forEach { user ->
            AnimatedVisibility(
                visible = user in users,
                enter   = fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                TeammateRow(user = user, onRemove = { onRemove(user) })
            }
        }
    }
}

@Composable
fun TeammateRow(
    user: ShareUser,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        UserAvatar(
            username          = user.username,
            profilePictureURL = user.profilePictureURL,
            size              = 44
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = user.username,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 1.5.em
            )
            Text(
                text       = user.email,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 1.43.em
            )
        }

        IconButton(
            onClick  = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = stringResource(R.string.icon_remove_user),
                tint               = MaterialTheme.colorScheme.error,
                modifier           = Modifier.size(24.dp)
            )
        }
    }
}
