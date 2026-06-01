package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R

@Composable
fun TeammateShareBottomBar(
    isSubmitting: Boolean,
    hasUsers: Boolean,
    onConfirmShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .height(100.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Button(
            onClick  = onConfirmShare,
            enabled  = !isSubmitting && hasUsers,
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = MaterialTheme.colorScheme.secondary,
                contentColor           = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                disabledContentColor   = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .requiredWidth(80.dp)
                .requiredHeight(56.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color       = Color.White,
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector        = Icons.Outlined.Send,
                    contentDescription = stringResource(R.string.share_send),
                    modifier           = Modifier.size(24.dp)
                )
            }
        }
    }
}
