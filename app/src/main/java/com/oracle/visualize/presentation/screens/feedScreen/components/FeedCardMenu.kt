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
import androidx.compose.material3.HorizontalDivider
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
 * Matches the Figma design: white card, large text, no icons, rounded corners.
 *
 * The menu is ownership-aware via [isDeletable]:
 * - `true`  (owner)     → **Share** + **Delete for everyone** (red)
 * - `false` (non-owner) → **Hide for me** (red)
 *
 * Ownership logic is computed in [FeedViewModel] and passed down as a plain Boolean
 * to keep this composable free of business logic.
 *
 * @param isDeletable Whether the current user owns the visualization.
 * @param onDismiss   Called when an action is selected or the menu is dismissed.
 * @param onShare     Navigates to the Share with Teammates screen.
 * @param onDeleteForEveryone Requests permanent deletion confirmation.
 * @param onHideForMe Requests hide-for-me confirmation.
 */
@Composable
fun FeedCardMenu(
    isDeletable: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onHideForMe: () -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.secondaryContainer.let { c ->
        if (c == Color.Transparent) MaterialTheme.colorScheme.surface else c
    }

    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .shadow(
                elevation    = 8.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor    = Color.Black.copy(alpha = 0.15f)
            )
            .width(260.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            if (isDeletable) {
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_share),
                    color   = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onDismiss(); onShare() }
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_delete_for_everyone),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onDeleteForEveryone() }
                )
            } else {
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
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text       = label,
            color      = color,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Normal,
            style      = MaterialTheme.typography.bodyLarge
        )
    }
}
