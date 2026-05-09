package com.oracle.visualize.presentation.screens.feedScreen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.oracle.visualize.R

/**
 * Confirmation dialog shown before deleting a visualization for everyone.
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
                fontWeight = FontWeight.SemiBold,
                style      = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text  = stringResource(R.string.feed_delete_for_everyone_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.delete),
                    color      = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * Confirmation dialog shown before hiding a visualization from the current user's feed.
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
                text       = stringResource(R.string.feed_delete_title),
                fontWeight = FontWeight.SemiBold,
                style      = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text  = stringResource(R.string.feed_delete_for_me_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.delete),
                    color      = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
