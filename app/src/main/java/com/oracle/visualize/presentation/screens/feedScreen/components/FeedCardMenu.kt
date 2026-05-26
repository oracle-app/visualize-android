package com.oracle.visualize.presentation.screens.feedScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R

/**
 * Floating card menu displayed when the three-dot icon on a [FeedCard] is tapped.
 * Matches the Figma design: white card, medium text, no icons, rounded corners.
 */
@Composable
fun FeedCardMenu(
    isDeletable: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onHideForMe: () -> Unit
) {
    Card(
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .shadow(
                elevation    = 6.dp,
                shape        = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor    = Color.Black.copy(alpha = 0.12f)
            )
            .width(200.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            if (isDeletable) {
                // Share
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_share),
                    color   = MaterialTheme.colorScheme.onSurface,
                    onClick = { onDismiss(); onShare() }
                )
                // Delete for everyone
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_delete_for_everyone),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onDeleteForEveryone() }
                )
            } else {
                // Delete for me
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_hide_for_me),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onHideForMe() }
                )
            }
        }
    }
}

@Composable
private fun FeedCardMenuItem(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text       = label,
            color      = color,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Normal,
            style      = MaterialTheme.typography.bodyLarge
        )
    }
}
