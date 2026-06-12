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
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R

@Composable
private fun VisualizationActionDialog(
    title: String,
    message: String,
    confirmLabel: String,
    confirmColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.secondaryContainer.let { c ->
        if (c == Color.Transparent) MaterialTheme.colorScheme.surface else c
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = containerColor,
        shape            = RoundedCornerShape(28.dp),
        title = {
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        },
        text = {
            Text(text = message, fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dialog_delete_team_cancel),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel, color = confirmColor,
                    fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun DeleteForEveryoneDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    VisualizationActionDialog(
        title        = stringResource(R.string.feed_delete_title),
        message      = stringResource(R.string.feed_delete_for_everyone_message),
        confirmLabel = stringResource(R.string.dialog_delete_team_confirm),
        confirmColor = MaterialTheme.colorScheme.error,
        onDismiss    = onDismiss,
        onConfirm    = onConfirm
    )
}

@Composable
fun DeleteForMeDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    VisualizationActionDialog(
        title        = stringResource(R.string.feed_hide_title),
        message      = stringResource(R.string.feed_delete_for_me_message),
        confirmLabel = stringResource(R.string.dialog_delete_team_confirm),
        confirmColor = MaterialTheme.colorScheme.error,
        onDismiss    = onDismiss,
        onConfirm    = onConfirm
    )
}
