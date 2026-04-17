package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oracle.visualize.ui.theme.TextDark
import com.oracle.visualize.ui.theme.TextGray
import com.oracle.visualize.ui.theme.TealPrimary

@Composable
fun UnsavedChangesDialog(
    onDismiss: () -> Unit,
    onConfirmLeave: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Unsaved Changes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "You have unsaved changes. Are you sure you want to leave?",
                fontSize = 14.sp,
                color = TextGray,
                lineHeight = 21.sp
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = onConfirmLeave) {
                    Text(
                        text = "Share",
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}