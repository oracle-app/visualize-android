package com.oracle.visualize.presentation.screens.feedScreen.components

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

// In light mode, AlertDialog container should be white (secondaryContainer = White in LightColorScheme)
// not surfaceVariant, to match the Figma design.
private val DialogContainerColor: @Composable () -> Color = {
    MaterialTheme.colorScheme.secondaryContainer
}

/**
 * Confirmation dialog shown before deleting a visualization for everyone (owner action).
 * Permanently removes the visualization from all recipients' feeds.
 *
 * @param onDismiss Called when the user taps Cancel.
 * @param onConfirm Called when the user confirms the deletion.
 */
@Composable
fun DeleteForEveryoneDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = stringResource(R.string.feed_delete_title),
                fontWeight = FontWeight.Normal,
                style      = MaterialTheme.typography.headlineSmall,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            Text(
                text  = stringResource(R.string.feed_delete_for_everyone_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.delete),
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
        containerColor = DialogContainerColor(),
        shape = RoundedCornerShape(28.dp)
    )
}

/**
 * Confirmation dialog shown before hiding a visualization from the current user's feed (non-owner action).
 * The visualization remains visible to other users with access.
 *
 * @param onDismiss Called when the user taps Cancel.
 * @param onConfirm Called when the user confirms hiding the visualization.
 */
@Composable
fun DeleteForMeDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = stringResource(R.string.feed_hide_title),
                fontWeight = FontWeight.Normal,
                style      = MaterialTheme.typography.headlineSmall,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            Text(
                text  = stringResource(R.string.feed_delete_for_me_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.delete),
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
        containerColor = DialogContainerColor(),
        shape = RoundedCornerShape(28.dp)
    )
}
