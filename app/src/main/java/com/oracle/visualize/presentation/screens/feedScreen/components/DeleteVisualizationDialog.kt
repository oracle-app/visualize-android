package com.oracle.visualize.presentation.screens.feedScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R

// In light mode, AlertDialog container should be white (secondaryContainer = White in LightColorScheme)
// not surfaceVariant, to match the Figma design. In dark mode, secondaryContainer is Transparent,
// so we fall back to surface to avoid an invisible dialog.
@Composable
private fun dialogContainerColor() = MaterialTheme.colorScheme.secondaryContainer.let { color ->
    if (color == androidx.compose.ui.graphics.Color.Transparent) {
        MaterialTheme.colorScheme.surface
    } else {
        color
    }
}

/**
 * Generic confirmation dialog for visualization actions (delete for everyone / hide for me).
 * Both dialogs share the same structure; only the title and body text differ.
 *
 * @param title    The dialog headline (e.g. "Delete visualization?").
 * @param message  The body text explaining what will happen.
 * @param confirmLabel Label for the destructive confirm button (defaults to "Delete").
 * @param onDismiss Called when the user taps Cancel.
 * @param onConfirm Called when the user confirms the action.
 */
@Composable
fun VisualizationActionDialog(
    title: String,
    message: String,
    confirmLabel: String = stringResource(R.string.delete),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.Normal,
                style      = MaterialTheme.typography.headlineSmall,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = confirmLabel,
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
        containerColor = dialogContainerColor(),
        shape = RoundedCornerShape(28.dp)
    )
}

/**
 * Convenience wrapper: confirms permanent deletion for every recipient (owner action).
 */
@Composable
fun DeleteForEveryoneDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    VisualizationActionDialog(
        title     = stringResource(R.string.feed_delete_title),
        message   = stringResource(R.string.feed_delete_for_everyone_message),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

/**
 * Convenience wrapper: confirms hiding the visualization from the current user's feed only.
 */
@Composable
fun DeleteForMeDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    VisualizationActionDialog(
        title     = stringResource(R.string.feed_hide_title),
        message   = stringResource(R.string.feed_delete_for_me_message),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}
