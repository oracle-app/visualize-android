package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.ShareUser

/**
 * Confirmation dialog shown when the user taps the remove (X) button next to a teammate.
 *
 * containerColor uses secondaryContainer (= White in light mode) and falls back to surface
 * in dark mode where secondaryContainer is Transparent — preventing an invisible dialog.
 *
 * @param user The [ShareUser] to remove.
 * @param onDismiss Called when the user taps Cancel.
 * @param onConfirm Called when the user confirms removal.
 */
@Composable
fun RemoveTeammateDialog(
    user: ShareUser,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.secondaryContainer.let { color ->
        if (color == Color.Transparent) MaterialTheme.colorScheme.surface else color
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = stringResource(R.string.share_remove_title, user.username),
                fontWeight = FontWeight.Normal,
                style      = MaterialTheme.typography.headlineSmall,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.share_remove_confirm),
                    color      = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                    style      = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text       = stringResource(R.string.cancel),
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    style      = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = containerColor,
        shape = RoundedCornerShape(28.dp)
    )
}
