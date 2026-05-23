package com.oracle.visualize.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.oracle.visualize.R
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.presentation.screens.shareScreen.components.MemberAvatarStackFeed
import java.util.Date
import java.util.concurrent.TimeUnit

fun formatTime(date: Date, context: Context): String {
    val now   = Date()
    val diff  = now.time - date.time
    val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days  = TimeUnit.MILLISECONDS.toDays(diff)
    val weeks = (days / 7).toInt()

    return when {
        mins  < 1  -> context.getString(R.string.time_just_now)
        mins  < 60 -> context.getString(R.string.time_mins_ago, mins)
        hours < 24 -> context.getString(R.string.time_hours_ago, hours)
        days  < 7  -> context.getString(R.string.time_days_ago, days)
        else       -> context.resources.getQuantityString(R.plurals.time_weeks_ago, weeks, weeks)
    }
}

/**
 * A card component used in the feed to display a visualization's summary.
 *
 * The three-dot menu shows a custom floating Card with large text options:
 *  - Owner  → "Share" + "Delete for everyone" (red)
 *  - Others → "Hide for me" (red)
 *
 * @param item                The [VisualizationCard] data to display.
 * @param currentUserID       Logged-in user ID, used to decide which menu items to show.
 * @param isMenuOpen          Whether the dropdown for this card is open.
 * @param onClick             Opens the full-screen visualization.
 * @param onMenuOpen          Opens the three-dot dropdown.
 * @param onMenuDismiss       Closes the dropdown without action.
 * @param onDeleteForEveryone Triggers the delete-for-everyone confirmation dialog.
 * @param onHideForMe         Triggers the hide-for-me confirmation dialog.
 * @param onShare             Navigates to the Share with Teammates screen.
 */
@Composable
fun FeedCard(
    item: VisualizationCard,
    currentUserID: String,
    isMenuOpen: Boolean,
    onClick: () -> Unit = {},
    onMenuOpen: () -> Unit = {},
    onMenuDismiss: () -> Unit = {},
    onDeleteForEveryone: () -> Unit = {},
    onHideForMe: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    val context = LocalContext.current
    val isOwner = item.authorID == currentUserID

    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {

            // ── Header row ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 14.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text     = stringResource(R.string.by_author, item.author),
                            color    = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                        Text(
                            text     = stringResource(
                                R.string.bullet_separator,
                                formatTime(item.createdAt, context)
                            ),
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                // Three-dot button + custom popup menu
                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    IconButton(onClick = onMenuOpen) {
                        Icon(
                            imageVector        = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.icon_menu),
                            tint               = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isMenuOpen) {
                        Popup(
                            alignment  = Alignment.TopEnd,
                            onDismissRequest = onMenuDismiss,
                            properties = PopupProperties(focusable = true)
                        ) {
                            FeedCardMenu(
                                isOwner             = isOwner,
                                onDismiss           = onMenuDismiss,
                                onShare             = onShare,
                                onDeleteForEveryone = onDeleteForEveryone,
                                onHideForMe         = onHideForMe
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Chart box ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(all = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val chart = item.chart
                    if (chart != null) {
                        ChartRenderGeneral(
                            chart          = chart,
                            showAxisLabels = false,
                            enableTooltips = false
                        )
                    } else {
                        Text(
                            text  = stringResource(R.string.error_chart_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Shared-with avatars ───────────────────────────────────────────
            Row(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .heightIn(min = 41.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemberAvatarStackFeed(item.allUsersSharedWith)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

// ── Custom popup card ─────────────────────────────────────────────────────────

/**
 * Floating card menu that appears when the three-dot icon is tapped.
 * Matches the Figma design: white card, large text, no icons, rounded corners.
 *
 * Owner  → Share  |  Delete for everyone (red)
 * Others → Hide for me (red)
 */
@Composable
private fun FeedCardMenu(
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onHideForMe: () -> Unit
) {
    // Use secondaryContainer (= White in light, surface fallback in dark)
    val bgColor = MaterialTheme.colorScheme.secondaryContainer.let { c ->
        if (c == Color.Transparent) MaterialTheme.colorScheme.surface else c
    }

    Card(
        shape   = RoundedCornerShape(16.dp),
        colors  = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .shadow(
                elevation        = 8.dp,
                shape            = RoundedCornerShape(16.dp),
                ambientColor     = Color.Black.copy(alpha = 0.15f),
                spotColor        = Color.Black.copy(alpha = 0.15f)
            )
            .width(260.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            if (isOwner) {
                // Share
                MenuTextItem(
                    label    = stringResource(R.string.feed_menu_share),
                    color    = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick  = { onDismiss(); onShare() }
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                // Delete for everyone
                MenuTextItem(
                    label   = stringResource(R.string.feed_menu_delete_for_everyone),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onDeleteForEveryone() }
                )
            } else {
                // Hide for me
                MenuTextItem(
                    label   = stringResource(R.string.feed_menu_hide_for_me),
                    color   = MaterialTheme.colorScheme.error,
                    onClick = { onDismiss(); onHideForMe() }
                )
            }
        }
    }
}

@Composable
private fun MenuTextItem(
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
