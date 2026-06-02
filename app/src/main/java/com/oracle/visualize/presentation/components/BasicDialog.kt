package com.oracle.visualize.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oracle.visualize.R

@Composable
fun BasicDialog(
    title: String,
    message: String,
    confirm: String,
    cancel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val titleTextColor = if (isSystemInDarkTheme()) { MaterialTheme.colorScheme.onPrimary } else {
        MaterialTheme.colorScheme.onSurface
    }

    val dialogDescriptionTextColor = if (isSystemInDarkTheme()) { MaterialTheme.colorScheme.outlineVariant } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, color = titleTextColor) },
        text = { Text(text = message, color = dialogDescriptionTextColor) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                if (confirm == stringResource(R.string.delete) || confirm == stringResource(R.string.log_out)) {
                    Text(text = confirm, color = MaterialTheme.colorScheme.error)
                } else {
                    Text(text = confirm, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancel, color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
