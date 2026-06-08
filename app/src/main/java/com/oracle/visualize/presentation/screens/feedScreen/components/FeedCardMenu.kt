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
 * Menus are dynamically generated based on the user's role and ownership via the Policy Object.
 */
@Composable
fun FeedCardMenu(
    canDelete: Boolean,
    canHide: Boolean,
    canShare: Boolean,
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
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor    = Color.Black.copy(alpha = 0.15f))
            .width(260.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {

            if (canShare) {
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_share),
                    color   = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onDismiss(); onShare() }
                )
            }

            if (canShare && (canDelete || canHide)) {
                MenuDivider()
            }

            if (canDelete) {
                FeedCardMenuItem(
                    label   = stringResource(R.string.feed_menu_delete_for_everyone),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onDeleteForEveryone() }
                )
            }

            if (canDelete && canHide) {
                MenuDivider()
            }

            if (canHide) {
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
private fun MenuDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
private fun FeedCardMenuItem(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
