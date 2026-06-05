package com.oracle.visualize.presentation.screens.teamsScreen.components

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R

/**
 * Confirmation dialog shown before permanently deleting a team.
 * containerColor uses secondaryContainer (White in light mode) with surface fallback in dark mode.
 */
@Composable
fun DeleteTeamDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.secondaryContainer.let { c ->
        if (c == Color.Transparent) MaterialTheme.colorScheme.surface else c
    }

    val titleTextColor = if (isSystemInDarkTheme()) { MaterialTheme.colorScheme.onPrimary } else {
        MaterialTheme.colorScheme.onSurface
    }

    val dialogDescriptionTextColor = if (isSystemInDarkTheme()) { MaterialTheme.colorScheme.outlineVariant } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = containerColor,
        shape            = RoundedCornerShape(28.dp),
        title = {
            Text(
                text       = stringResource(R.string.dialog_delete_team_title),
                fontSize   = 24.sp,
                fontWeight = FontWeight.Normal,
                color      = titleTextColor
            )
        },
        text = {
            Text(
                text     = stringResource(R.string.dialog_delete_team_message),
                fontSize = 16.sp,
                color    = dialogDescriptionTextColor
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text       = stringResource(R.string.dialog_delete_team_cancel),
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = stringResource(R.string.dialog_delete_team_confirm),
                    color      = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 16.sp
                )
            }
        }
    )
}
