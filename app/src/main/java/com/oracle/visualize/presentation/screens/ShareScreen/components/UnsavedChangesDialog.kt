package com.oracle.visualize.presentation.screens.ShareScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.oracle.visualize.ui.theme.TealPrimary

// Figma AC-70: overlay Color(0xFF1A2F3F) alpha 0.2, dialog offset x=50, width=312, corners=28dp
private val OverlayColor   = Color(0xFF1A2F3F).copy(alpha = 0.2f)
private val DialogBg       = Color.White
private val TitleColor     = Color(0xFF1D1B20)
private val BodyColor      = Color(0xFF49454F)

@Composable
fun UnsavedChangesDialog(
    onDismiss: () -> Unit,
    onConfirmLeave: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth(0.76f)
                    .background(DialogBg, RoundedCornerShape(28.dp))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = "Unsaved Changes",
                        color = TitleColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 1.33.em
                    )
                    Text(
                        text = "You have unsaved changes. Are you sure you want to leave?",
                        color = BodyColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 1.43.em
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 8.dp, end = 24.dp,
                        top = 20.dp, bottom = 20.dp
                    )
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            color = TealPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 1.43.em
                        )
                    }
                    TextButton(onClick = onConfirmLeave) {
                        Text(
                            text = "Share",
                            color = TealPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 1.43.em
                        )
                    }
                }
            }
        }
    }
}